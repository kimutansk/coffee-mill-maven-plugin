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
import org.nanoko.coffee.mill.processors.OptiPNGProcessor;
import org.nanoko.coffee.mill.processors.Processor;
import org.nanoko.coffee.mill.utils.OptionsHelper;

/**
 * Optimizes PNG files using optiPNG (http://optipng.sourceforge.net/).
 * OptiPNG must be installed and the executable `optipng` available form the system path.
 * @goal optimize-png
 *
 */
public class OptiPNGMojo extends AbstractCoffeeMillMojo {

    OptiPNGProcessor processor;

    /**
     * Enables the verbose mode of optiPNG.
     * @parameter default-value=true
     */
    public boolean optiPNGVerbose;

    /**
     * The optiPNG optimization level among 0-7.
     * @parameter default-value=2
     */
    protected int optiPngOptimizationLevel;

    /**
     * Skips the PNG files optimization.
     * @parameter default-value=false
     */
    protected boolean skipOptiPNG;

    public OptiPNGMojo() {
        processor = new OptiPNGProcessor();
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skipOptiPNG) {
            return;
        }

        if (optiPngOptimizationLevel < 0  || optiPngOptimizationLevel > 7) {
            throw new MojoExecutionException("Invalid optiPNG optimization level, it must be in [0-7]");
        }


        processor.configure(this, new OptionsHelper.OptionsBuilder().set("verbose", true).set("level",
                optiPngOptimizationLevel).build());

        try {
            processor.processAll();
        } catch (Processor.ProcessorException e) {
            throw new MojoFailureException("PNG Optimization failed", e);
        }
    }

}
