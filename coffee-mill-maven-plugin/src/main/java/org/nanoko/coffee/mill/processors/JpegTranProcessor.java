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

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.io.FileUtils;
import org.nanoko.coffee.mill.mojos.AbstractCoffeeMillMojo;
import org.nanoko.coffee.mill.utils.ExecUtils;
import org.nanoko.coffee.mill.utils.OptionsHelper;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * A processor optimizing JPEG files using JpegTran.
 * JpegTran must be installed on the system and available from the path.
 */
public class JpegTranProcessor extends DefaultProcessor {

    /**
     * The JpegTran executable file name without extension.
     * This field is not final for testing purpose.
     */
    public static String EXECUTABLE_NAME = "jpegtran";

    /**
     * The JpegTran executable.
     */
    private File jpegTranExec;

    /**
     * Enables verbose mode.
     */
    private boolean verbose;

    @Override
    public void configure(AbstractCoffeeMillMojo mojo, Map<String, Object> options) {
        super.configure(mojo, options);

        jpegTranExec = ExecUtils.findExecutableInPath(EXECUTABLE_NAME);

        if (jpegTranExec == null) {
            getLog().error("Cannot optimize JPEG files - jpegtran not installed.");
            return;
        } else {
            getLog().info("Invoking jpegtran : " + jpegTranExec.getAbsolutePath());
        }

        OptionsHelper.getBoolean(options, "verbose", false);
    }

    /**
     * Iterates over project resources and optimize all JPEG files.
     *
     * @throws org.nanoko.coffee.mill.processors.Processor.ProcessorException
     */
    @Override
    public void processAll() throws ProcessorException {
        if (jpegTranExec == null) {
            return;
        }

        if (! mojo.getWorkDirectory().exists()) {
            return;
        }

        Iterator<File> files = FileUtils.iterateFiles(mojo.workDir, new String[]{"jpg", "jpeg"}, true);
        while (files.hasNext()) {
            File file = files.next();
            optimize(file);
        }
    }

    @Override
    public boolean accept(File file) {
        return jpegTranExec != null
                && isFileContainedInDirectory(file, mojo.workDir)
                && (file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg"));
    }

    @Override
    public void fileCreated(File file) throws ProcessorException {
        optimize(file);
    }

    @Override
    public void fileUpdated(File file) throws ProcessorException {
        optimize(file);
    }

    private void optimize(File file) throws ProcessorException {
        File dir = file.getParentFile();

        // Build command line
        CommandLine cmdLine = CommandLine.parse(jpegTranExec.getAbsolutePath());

        if (verbose) {
            cmdLine.addArgument("-verbose");
        }

        cmdLine.addArgument("-copy");
        cmdLine.addArgument("none");

        cmdLine.addArgument("-optimize");

        cmdLine.addArgument("-outfile");
        cmdLine.addArgument("out.jpeg");

        cmdLine.addArgument(file.getName());

        DefaultExecutor executor = new DefaultExecutor();

        executor.setWorkingDirectory(dir);
        executor.setExitValue(0);
        try {
            getLog().info("Executing " + cmdLine.toString());
            executor.execute(cmdLine);

            // Overwrite the original file
            File out = new File(dir, "out.jpeg");
            if (out.exists()) {
                FileUtils.copyFile(new File(dir, "out.jpeg"), file);
            } else {
                throw new IOException("Output file not found : " + out.getAbsolutePath());
            }

            getLog().info(file.getName() + " optimized");
        } catch (IOException e) {
            throw new ProcessorException("Error during JPG optimization of " + file.getAbsolutePath(), e);
        }
    }
}
