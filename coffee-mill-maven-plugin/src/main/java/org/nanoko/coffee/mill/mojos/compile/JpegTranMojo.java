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
import org.nanoko.coffee.mill.processors.JpegTranProcessor;
import org.nanoko.coffee.mill.processors.Processor;
import org.nanoko.coffee.mill.utils.OptionsHelper;

/**
 * Optimizes JPEG files using jpegtran (http://jpegclub.org/jpegtran/).
 * JpegTran must be installed and the executable `jpegtran` available form the system path.
 * @goal optimize-jpeg
 *
 */
public class JpegTranMojo extends AbstractCoffeeMillMojo {

    JpegTranProcessor processor;

    /**
     * Enables the verbose mode of optiPNG.
     * @parameter default-value=true
     */
    public boolean jpegTranVerbose;

    /**
     * Skips the JPEG files optimization.
     * @parameter default-value=false
     */
    protected boolean skipjpegTran;


    public JpegTranMojo() {
        processor = new JpegTranProcessor();
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skipjpegTran) {
            return;
        }

        processor.configure(this, new OptionsHelper.OptionsBuilder().set("verbose", true).build());

        try {
            processor.processAll();
        } catch (Processor.ProcessorException e) {
            throw new MojoFailureException("JPEG Optimization failed", e);
        }
    }

}
