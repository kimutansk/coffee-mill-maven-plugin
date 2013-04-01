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
 * CoffeeScript files are generally in the <tt>src/main/coffee</tt> directory. It can be configured using the
 * <tt>coffeeScriptDir</tt> parameter.
 * If the directory does not exist, the compilation is skipped.
 * @goal compile-coffeescript
 */
public class CoffeeScriptCompilerMojo extends AbstractCoffeeMillMojo {

    /**
     * Enables / Disables the coffeescript compilation.
     * Be aware that this property disables the compilation on both main sources and test sources.
     * @parameter default-value="false"
     */
    protected boolean skipCoffeeScriptCompilation;

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skipCoffeeScriptCompilation) {
            getLog().info("CoffeeScript compilation skipped");
            return;
        }

        if (! coffeeScriptDir.exists()) {
            getLog().info("CoffeeScript compilation skipped - " + coffeeScriptDir.getAbsolutePath() + " does not " +
                    "exist");
            return;
        }

        File coffee = getCoffeeScript();
        if (coffee == null) {
            throw new MojoExecutionException("Cannot configure the coffeescript compiler without coffeescript");
        }

        CoffeeScriptCompilationProcessor processor = new CoffeeScriptCompilationProcessor();
        processor.configure(this, new OptionsHelper.OptionsBuilder()
                .set("test", false)
                .set("coffeescript", coffee)
                .build());

        try {
            processor.processAll();
        } catch (Processor.ProcessorException e) {
            throw new MojoExecutionException("", e);
        }

    }

    private static final String COFFEE_SCRIPT_ARTIFACTID = "coffeescript";

    /**
     * Extracts the coffeescript artifact from the plugin's dependency list and returns the file.
     * This file is injected into the processor.
     * @return the coffeescript's artifact file.
     */
    private File getCoffeeScript() {
        for (Artifact artifact : pluginDependencies) {
            if (artifact.getArtifactId().equals(COFFEE_SCRIPT_ARTIFACTID)) {
                return artifact.getFile();
            }
        }
        return null;
    }

}
