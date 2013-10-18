/*
 * Copyright 2013 OW2 Nanoko Project
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.nanoko.coffee.mill.processors;

import org.apache.commons.io.FileUtils;
import org.nanoko.coffee.mill.mojos.AbstractCoffeeMillMojo;
import org.nanoko.coffee.mill.utils.RhinoLauncher;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

/**
 * Compiles ractive templates.
 * Ractive templates are in the javascript directory using the .rk extension.
 * More info <a href="http://www.ractivejs.org">here</a>.
 */
public class RactiveJSProcessor extends DefaultProcessor {

    public static final String RACTIVE_SCRIPT = "/ractive/ractive-0.3.7.js";

    private File source;
    private File destination;

    @Override
    public void configure(AbstractCoffeeMillMojo mojo, Map<String, Object> options) {
        super.configure(mojo, options);
        this.source = mojo.javaScriptDir;
        this.destination = mojo.getWorkDirectory();
    }

    @Override
    public void processAll() throws ProcessorException {
        if (! source.exists()) {
            return;
        }
        getLog().info("Compiling ractive templates");
        Collection<File> files = FileUtils.listFiles(source, new String[]{"rk"}, true);
        for (File file : files) {
            ractive(file);
        }
    }

    private void ractive(File input) throws ProcessorException {
        try {
            File output = getOutputJSFile(input);
            RhinoLauncher launcher = initScriptBuilder();
            String content = FileUtils.readFileToString(input);
            String compileScript = String.format("(%s(%s)).toSource();", "Ractive.parse", RhinoLauncher.toJSMultiLineString(content));
            String result = String.format("(function(){Ractive.templates['%s']=%s;})();",
                input.getName().substring(0, input.getName().length() - ".rk".length()),
                (String)launcher.evaluate(compileScript, "Ractive.parse"));
            FileUtils.write(output, result);
        } catch (IOException e) {
            getLog().error("Ractive parse failed - was not able to compile " + input.getAbsolutePath(), e);
            throw new ProcessorException("Ractive parse failed - was not able to parse" + input
                    .getAbsolutePath(), e);
        }
    }

    @Override
    public boolean accept(File file) {
        return file.isFile() && isFileContainedInDirectory(file, mojo.javaScriptDir)
                && file.getName().endsWith(".rk");
    }

    @Override
    public void fileCreated(File file) throws ProcessorException {
        ractive(file);
    }

    @Override
    public void fileUpdated(File file) throws ProcessorException {
        ractive(file);
    }

    @Override
    public void fileDeleted(File file) {
        File theFile = getOutputJSFile(file);
        if (theFile.exists()) {
            theFile.delete();
        }
    }

    private File getOutputJSFile(File input) {
        String jsFileName = input.getName().substring(0, input.getName().length() - ".rk".length()) + ".js";
        String path = input.getParentFile().getAbsolutePath().substring(source.getAbsolutePath().length());
        return new File(destination, path + "/" + jsFileName);
    }

    /**
     * Initialize script builder for evaluation.
     */
    private RhinoLauncher initScriptBuilder() {
        try {
            RhinoLauncher builder = null;
            final InputStream script = getScriptAsStream();
            builder = RhinoLauncher.newClientSideAwareChain().evaluateChain(script, RACTIVE_SCRIPT);
            return builder;
        } catch (final Exception e) {
            throw new IllegalStateException("Initialization of the Ractive processing failed", e);
        }
    }

    public InputStream getScriptAsStream() {
        return getClass().getResourceAsStream(RACTIVE_SCRIPT);
    }
}
