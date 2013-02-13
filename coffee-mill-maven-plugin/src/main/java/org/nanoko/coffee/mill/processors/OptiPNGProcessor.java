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
 * A processor optimizing PNG files using OptiPNG.
 * OptiPNG must be installed on the system and available from the path.
 */
public class OptiPNGProcessor extends DefaultProcessor {

    /**
     * The optipng executable file name without extension.
     * This field is not final for testing purpose.
     */
    public static String EXECUTABLE_NAME = "optipng";

    /**
     * The optipng executable.
     */
    private File optiPNGExec;

    /**
     * Enables verbose mode.
     */
    private boolean verbose;

    /**
     * Optimization level (0-7).
     * 2 by default.
     * Higher values are more optimized, but make the process slower.
     */
    private int level = 2;

    @Override
    public void configure(AbstractCoffeeMillMojo mojo, Map<String, Object> options) {
        super.configure(mojo, options);

        optiPNGExec = ExecUtils.findExecutableInPath(EXECUTABLE_NAME);

        if (optiPNGExec == null) {
            getLog().error("Cannot optimize PNG files - optipng not installed.");
            return;
        } else {
            getLog().info("Invoking optipng : " + optiPNGExec.getAbsolutePath());
        }

        OptionsHelper.getBoolean(options, "verbose", false);
        OptionsHelper.getInteger(options, "level", 2);
    }

    /**
     * Iterates over project resources and optimize all PNG files.
     *
     * @throws ProcessorException
     */
    @Override
    public void processAll() throws ProcessorException {
        if (optiPNGExec == null) {
            return;
        }

        if (! mojo.getWorkDirectory().exists()) {
            return;
        }

        Iterator<File> files = FileUtils.iterateFiles(mojo.workDir, new String[]{"png"}, true);
        while (files.hasNext()) {
            File file = files.next();
            optimize(file);
        }
    }

    @Override
    public boolean accept(File file) {
        return optiPNGExec != null && isFileContainedInDirectory(file, mojo.workDir) && file.getName().endsWith("" +
                ".png");
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
        CommandLine cmdLine = CommandLine.parse(optiPNGExec.getAbsolutePath());
        cmdLine.addArgument(file.getName());

        if (verbose) {
            cmdLine.addArgument("-v");
        }

        cmdLine.addArgument("-o" + level);

        DefaultExecutor executor = new DefaultExecutor();

        executor.setWorkingDirectory(dir);
        executor.setExitValue(0);
        try {
            getLog().info("Executing " + cmdLine.toString());
            executor.execute(cmdLine);
            getLog().info(file.getName() + " optimized");
        } catch (IOException e) {
            throw new ProcessorException("Error during PNG optimization of " + file.getAbsolutePath(), e);
        }
    }
}
