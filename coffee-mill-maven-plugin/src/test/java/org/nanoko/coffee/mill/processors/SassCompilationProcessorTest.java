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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;
import org.nanoko.coffee.mill.mojos.compile.SassCompilerMojo;

import java.io.File;
import java.util.ArrayList;

import static org.fest.assertions.Assertions.assertThat;

/**
 * This tests are checking the Sass / Compass compilation.
 * It contains a hack to retrive the compass-gems from the local maven repository.
 * {@code org.nanoko.libs:compass-gems:0.12.2}
 */
public class SassCompilationProcessorTest {

    @Test
    public void testSassCompilation() throws MojoExecutionException, MojoFailureException {
        SassCompilerMojo mojo = new SassCompilerMojo();
        mojo.stylesheetsDir = new File("src/test/resources/stylesheets");
        mojo.workDir = new File("target/test/testSassCompilation-www");
        mojo.buildDirectory = new File("target/test/tmp");
        mojo.useCompass = true;
        mojo.pluginDependencies = new ArrayList<Artifact>();
        mojo.pluginDependencies.add(
                new FakeArtifact("org.nanoko.libs", "coffeescript", "1.6.2",
                        new File("src/test/resources/jslibs/coffee-script.js")));
        mojo.pluginDependencies.add(
                new FakeArtifact("org.nanoko.libs", "compass-gems", "0.12.2", "frameworks",
                        new File(System.getProperty("user.home"), ".m2/repository/org/nanoko/libs/compass-gems/0.12" +
                                ".2/compass-gems-0.12.2-frameworks.zip")));
        mojo.execute();

        assertThat(new File(mojo.workDir, "a_style.css").isFile()).isTrue();
    }

    @Test
    public void testCompassCompilation() throws MojoExecutionException, MojoFailureException {
        SassCompilerMojo mojo = new SassCompilerMojo();
        mojo.stylesheetsDir = new File("src/test/resources/stylesheets");
        mojo.workDir = new File("target/test/testCompassCompilation-www");
        mojo.buildDirectory = new File("target/test/tmp");
        mojo.useCompass = true;
        mojo.pluginDependencies = new ArrayList<Artifact>();
        mojo.pluginDependencies.add(
                new FakeArtifact("org.nanoko.libs", "coffeescript", "1.6.2",
                new File("src/test/resources/jslibs/coffee-script.js")));
        mojo.pluginDependencies.add(
                new FakeArtifact("org.nanoko.libs", "compass", "0.12.2", "frameworks",
                        new File(System.getProperty("user.home"), ".m2/repository/org/nanoko/libs/compass-gems/0.12" +
                                ".2/compass-gems-0.12.2-frameworks.zip")));
        mojo.execute();

        // Mock the compass-framework lookup

        mojo.execute();

        assertThat(new File(mojo.workDir, "a_style.css").isFile()).isTrue();
        assertThat(new File(mojo.workDir, "style_scss").isDirectory()).isTrue();
        assertThat(new File(mojo.workDir, "style_scss/ie.css").isFile()).isTrue();
        assertThat(new File(mojo.workDir, "style_scss/screen.css").isFile()).isTrue();
        assertThat(new File(mojo.workDir, "style_scss/print.css").isFile()).isTrue();
    }

}
