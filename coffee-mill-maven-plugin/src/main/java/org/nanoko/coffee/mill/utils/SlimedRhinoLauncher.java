/*
 * Copyright 2013 OW2.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.nanoko.coffee.mill.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlimedRhinoLauncher {
    private static final Logger LOG = LoggerFactory.getLogger(RhinoLauncher.class);
    
    private ScriptableObject compileScope;
    
    final static private String[] jsPaths = new String[]{
    	/*"/rhino/env.rhino.min.js",
    	"/rhino/rhino.require.js",
    	"/rhino/json2.min.js",
    	,
    	"/dust/dust-full-1.0.0.js",*/
    	"/coffeescript/coffeescript-1.6.2-min.js",
    	"/rhino/commons.js",
    	"/csslint/csslint-0.9.10.js"
    	
    }; 

    public SlimedRhinoLauncher(){
        Context context = Context.enter();
        context.setOptimizationLevel(9);
        compileScope = context.initStandardObjects();
        
        for(String jsPath : jsPaths){
            try {
            	evaluate(this.getClass().getResourceAsStream(jsPath), jsPath);
            } catch (final IOException e) {
                throw new RuntimeException("Couldn't initialize"+jsPath+" script", e);
            }
        }
        
        Context.exit();
        
        /*Require require = new Require(
                context,
                context.initStandardObjects(),
                new StrongCachingModuleScriptProvider(
                    new UrlModuleSourceProvider(
                        Collections.singleton(coffeeURI), null
                    )
                ),
                null,
                null,
                true);
        Scriptable coffeeScript = require.requireMain(context, "coffeescript-1.6.2-min");
        compileScope = context.newObject(coffeeScript);
        compileScope.setParentScope(coffeeScript);*/
    }
    
    public Object evaluate(final String script, final String sourceName){
    	Context context = Context.enter();
    	context.setOptimizationLevel(9);
        Object result = context.evaluateString(compileScope, script, sourceName, 0, null);
        Context.exit();
        return result;
    }
    
    public Object evaluate(final InputStream stream, final String sourceName) throws IOException{
    	Context context = Context.enter();
        Object result = context.evaluateReader(compileScope, new InputStreamReader(stream), sourceName, 0, null);
        Context.exit();
        return result;
    }
    
    private static SlimedRhinoLauncher singleton = new SlimedRhinoLauncher();
    
    public static SlimedRhinoLauncher getDaBeast(){
    	return SlimedRhinoLauncher.singleton;
    }
    
    public static String toJSMultiLineString(String data){
        final String[] lines = data.split("\n");
        final StringBuffer result = new StringBuffer("[");
        if (lines.length == 0) {
            result.append("\"\"");
        }
        for (int i = 0; i < lines.length; i++) {
            final String line = lines[i];
            result.append("\"");
            result.append(line.replace("\\", "\\\\").replace("\"", "\\\"").replaceAll("\\r|\\n", ""));
            // this is used to force a single line to have at least one new line (otherwise cssLint fails).
            if (lines.length == 1) {
                result.append("\\n");
            }
            result.append("\"");
            if (i < lines.length - 1) {
                result.append(",");
            }
        }
        result.append("].join(\"\\n\")");
        return result.toString();
    }
}
