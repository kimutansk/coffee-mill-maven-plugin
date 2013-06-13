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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.nanoko.coffee.mill.mojos.AbstractCoffeeMillMojo;
import org.nanoko.coffee.mill.processors.LessCompilationProcessor;
import org.nanoko.coffee.mill.processors.Processor;

/**
 * Compiles Less files.
 *
 * @goal compile-less
 *
 */
public class LessCompilerMojo extends AbstractCoffeeMillMojo {

    LessCompilationProcessor processor;

    public LessCompilerMojo() {
        processor = new LessCompilationProcessor();
    }


    public void execute() throws MojoExecutionException, MojoFailureException {
        processor.configure(this, null);

        if (! stylesheetsDir.exists()) {
            getLog().debug("The stylesheet directory does not exist - skipping LESS compilation");
            return;
        }

        try {
            processor.processAll();
        } catch (Processor.ProcessorException e) {
            throw new MojoFailureException("Less compilation failed", e);
        }
    }

    public LessCompilationProcessor getProcessor() {
        processor.configure(this, null);
        return processor;
    }

}
