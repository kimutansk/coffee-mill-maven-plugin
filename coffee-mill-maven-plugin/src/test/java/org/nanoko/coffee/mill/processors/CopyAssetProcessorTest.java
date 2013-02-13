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
import org.nanoko.coffee.mill.mojos.processResources.CopyAssetsMojo;

import java.io.File;

import static org.fest.assertions.Assertions.assertThat;

public class CopyAssetProcessorTest {

    @Test
    public void testAssetCopy() throws MojoExecutionException, MojoFailureException {
        CopyAssetsMojo mojo = new CopyAssetsMojo();
        mojo.assetsDir = new File("src/test/resources/assets");
        mojo.workDir = new File("target/test/testAssetCopy-www");
        mojo.execute();

        assertThat(new File(mojo.workDir, "index.html").isFile()).isTrue();
        assertThat(new File(mojo.workDir, "img").isDirectory()).isTrue();
        assertThat(new File(mojo.workDir, "img/demo.png").exists()).isTrue();
    }

    @Test
    public void testIgnoredFileDuringCopy() throws MojoExecutionException, MojoFailureException {
        CopyAssetsMojo mojo = new CopyAssetsMojo();
        mojo.assetsDir = new File("src/test/resources/assets");
        mojo.workDir = new File("target/test/testAssetCopy-www");
        mojo.execute();

        assertThat(new File(mojo.workDir, "project.pj").isFile()).isFalse();
        assertThat(new File(mojo.workDir, "BitKeeper").isDirectory()).isFalse();
        assertThat(new File(mojo.workDir, "BitKeeper/example").exists()).isFalse();
    }

    @Test
    public void testAssetCopyWhenAssetDoesNotExist() throws MojoExecutionException, MojoFailureException {
        CopyAssetsMojo mojo = new CopyAssetsMojo();
        mojo.assetsDir = new File("src/test/resources/assets_donotexist");
        mojo.workDir = new File("target/test/testAssetCopyWhenAssetDoesNotExist-www");
        mojo.execute();

        assertThat(mojo.workDir.list()).isEmpty();
    }

}
