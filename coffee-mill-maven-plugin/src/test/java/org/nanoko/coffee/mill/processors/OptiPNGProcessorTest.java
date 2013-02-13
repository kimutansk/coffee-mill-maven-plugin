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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;
import org.nanoko.coffee.mill.mojos.compile.OptiPNGMojo;
import org.nanoko.coffee.mill.mojos.processResources.CopyAssetsMojo;

import java.io.File;

import static org.junit.Assert.assertTrue;

public class OptiPNGProcessorTest {

    @Test
    public void testPNGOptimization() throws MojoExecutionException, MojoFailureException {
        CopyAssetsMojo mojo = new CopyAssetsMojo();
        mojo.assetsDir = new File("src/test/resources/assets");
        mojo.workDir = new File("target/test/testPNGOptimization-www");
        mojo.execute();

        OptiPNGMojo mojo2 = new OptiPNGMojo();
        mojo2.workDir = new File("target/test/testPNGOptimization-www");
        mojo2.optiPNGVerbose = true;

        File file = new File(mojo.workDir, "img/demo.png");
        long size = file.length();

        mojo2.execute();

        file = new File(mojo.workDir, "img/demo.png");
        long newSize = file.length();

        // Optimization, so the new size is smaller.
        assertTrue(newSize < size);
    }

    @Test
    public void testPNGOptimizationWhenOptiPNGIsNotInstalled() throws MojoExecutionException,
            MojoFailureException {
        CopyAssetsMojo mojo = new CopyAssetsMojo();
        mojo.assetsDir = new File("src/test/resources/assets");
        mojo.workDir = new File("target/test/testPNGOptimizationWhenOptiPNGIsNotInstalled-www");
        mojo.execute();

        OptiPNGMojo mojo2 = new OptiPNGMojo();
        mojo2.workDir = new File("target/test/testPNGOptimizationWhenOptiPNGIsNotInstalled-www");
        mojo2.optiPNGVerbose = true;

        String name = OptiPNGProcessor.EXECUTABLE_NAME;
        OptiPNGProcessor.EXECUTABLE_NAME ="do_not_exist";

        File file = new File(mojo.workDir, "img/demo.png");
        long size = file.length();

        mojo2.execute();

        long newSize = file.length();

        // Nothing happens.
        assertTrue(newSize == size);

        OptiPNGProcessor.EXECUTABLE_NAME = name;

    }
}
