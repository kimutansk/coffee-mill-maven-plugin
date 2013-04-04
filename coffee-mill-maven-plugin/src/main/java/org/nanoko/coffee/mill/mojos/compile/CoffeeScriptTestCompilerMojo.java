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

package org.nanoko.coffee.mill.mojos.compile;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.nanoko.coffee.mill.mojos.AbstractCoffeeMillMojo;
import org.nanoko.coffee.mill.processors.CoffeeScriptCompilationProcessor;
import org.nanoko.coffee.mill.processors.Processor;
import org.nanoko.coffee.mill.utils.OptionsHelper;

import java.io.File;

/**
 * Compiles CoffeeScript files.
 * CoffeeScript files are generally in the <tt>src/test/coffee</tt> directory. It can be configured using the
 * <tt>coffeeScriptTestDir</tt> parameter.
 * If the directory does not exist, the compilation is skipped.
 *
 * @goal test-compile-coffeescript
 */
public class CoffeeScriptTestCompilerMojo extends AbstractCoffeeMillMojo {

    private static final String COFFEE_SCRIPT_ARTIFACTID = "coffeescript";
    /**
     * Enables / Disables the coffeescript compilation.
     * Be aware that this property disables the compilation on both main sources and test sources.
     *
     * @parameter default-value="false"
     */
    protected boolean skipCoffeeScriptCompilation;
    /**
     * Enables / Disables the coffeescript test compilation.
     * Be aware that this property disables the compilation of test sources only.
     *
     * @parameter default-value="false"
     */
    protected boolean skipCoffeeScriptTestCompilation;

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skipCoffeeScriptCompilation || skipCoffeeScriptTestCompilation) {
            getLog().info("CoffeeScript test compilation skipped");
            return;
        }

        if (!coffeeScriptTestDir.exists()) {
            return;
        }

        File coffee = CoffeeScriptCompilerMojo.getCoffeeScript(this);
        if (coffee == null) {
            throw new MojoExecutionException("Cannot configure the coffeescript compiler without coffeescript");
        }

        CoffeeScriptCompilationProcessor processor = new CoffeeScriptCompilationProcessor();
        processor.configure(this, new OptionsHelper.OptionsBuilder()
                .set("test", true)
                .set("coffeescript", coffee)
                .build());
        try {
            processor.processAll();
        } catch (Processor.ProcessorException e) {
            throw new MojoExecutionException("", e);
        }
    }

}