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
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;
import org.nanoko.coffee.mill.mojos.compile.CoffeeScriptCompilerMojo;
import org.nanoko.coffee.mill.mojos.compile.CoffeeScriptTestCompilerMojo;

import java.io.File;
import java.util.ArrayList;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Test the coffeescript compilation.
 *
 * The coffeescript compiler (js file) is injected as a fake artifact using 'src/test/resources/jslibs/coffee-script
 * .js' as compiler.
 */
public class CoffeeScriptCompilationProcessorTest {

    @Test
    public void testCoffeeScriptCompilation() throws MojoExecutionException, MojoFailureException {
        CoffeeScriptCompilerMojo mojo = new CoffeeScriptCompilerMojo();
        mojo.coffeeScriptDir = new File("src/test/resources/coffee");
        mojo.coffeeScriptTestDir = new File("src/test/resources/coffee_donotexist");
        mojo.workDir = new File("target/test/testCoffeeScriptCompilation-www");
        mojo.workTestDir = new File("target/test/testCoffeeScriptCompilation-www-test");
        mojo.pluginDependencies = new ArrayList<Artifact>();
        mojo.pluginDependencies.add(new FakeArtifact("org.nanoko.libs", "coffeescript", "1.6.2",
                new File("src/test/resources/jslibs/coffee-script.js")));
        mojo.execute();

        assertThat(new File(mojo.workDir, "SyndicationService.js").isFile()).isTrue();
        assertThat(new File(mojo.workDir, "interfaces").isDirectory()).isTrue();
        assertThat(new File(mojo.workDir, "interfaces/FeedEntry.js").isFile()).isTrue();
        assertThat(new File(mojo.workDir, "interfaces/FeedReader.js").isFile()).isTrue();

        assertThat(mojo.workTestDir.list()).isNull();
    }

    @Test
    public void testCoffeeScriptTestCompilation() throws MojoExecutionException, MojoFailureException {
        CoffeeScriptTestCompilerMojo mojo = new CoffeeScriptTestCompilerMojo();
        mojo.coffeeScriptDir = new File("src/test/resources/coffee_donotexist");
        mojo.coffeeScriptTestDir = new File("src/test/resources/coffee");
        mojo.workDir = new File("target/test/testCoffeeScriptTestCompilation-www");
        mojo.workTestDir = new File("target/test/testCoffeeScriptTestCompilation-www-test");
        mojo.pluginDependencies = new ArrayList<Artifact>();
        mojo.pluginDependencies.add(new FakeArtifact("org.nanoko.libs", "coffeescript", "1.6.2",
                new File("src/test/resources/jslibs/coffee-script.js")));
        mojo.execute();

        assertThat(new File(mojo.workTestDir, "SyndicationService.js").isFile()).isTrue();
        assertThat(new File(mojo.workTestDir, "interfaces").isDirectory()).isTrue();
        assertThat(new File(mojo.workTestDir, "interfaces/FeedEntry.js").isFile()).isTrue();
        assertThat(new File(mojo.workTestDir, "interfaces/FeedReader.js").isFile()).isTrue();

        assertThat(mojo.workDir.list()).isNull();
    }

    @Test
    public void testCoffeeScriptTestAndMainCompilation() throws MojoExecutionException, MojoFailureException {
        CoffeeScriptCompilerMojo mojo1 = new CoffeeScriptCompilerMojo();
        CoffeeScriptTestCompilerMojo mojo2 = new CoffeeScriptTestCompilerMojo();
        mojo1.coffeeScriptDir = new File("src/test/resources/coffee");
        mojo1.coffeeScriptTestDir = new File("src/test/resources/coffee");
        mojo1.workDir = new File("target/test/testCoffeeScriptTestAndMainCompilation-www");
        mojo1.workTestDir = new File("target/test/testCoffeeScriptTestAndMainCompilation-www-test");
        mojo1.pluginDependencies = new ArrayList<Artifact>();
        mojo1.pluginDependencies.add(new FakeArtifact("org.nanoko.libs", "coffeescript", "1.6.2",
                new File("src/test/resources/jslibs/coffee-script.js")));
        mojo1.execute();

        mojo2.coffeeScriptDir = new File("src/test/resources/coffee");
        mojo2.coffeeScriptTestDir = new File("src/test/resources/coffee");
        mojo2.workDir = new File("target/test/testCoffeeScriptTestAndMainCompilation-www");
        mojo2.workTestDir = new File("target/test/testCoffeeScriptTestAndMainCompilation-www-test");
        mojo2.pluginDependencies = new ArrayList<Artifact>();
        mojo2.pluginDependencies.add(new FakeArtifact("org.nanoko.libs", "coffeescript", "1.6.2",
                new File("src/test/resources/jslibs/coffee-script.js")));
        mojo2.execute();

        assertThat(new File(mojo1.workDir, "SyndicationService.js").isFile()).isTrue();
        assertThat(new File(mojo1.workDir, "interfaces").isDirectory()).isTrue();
        assertThat(new File(mojo1.workDir, "interfaces/FeedEntry.js").isFile()).isTrue();
        assertThat(new File(mojo1.workDir, "interfaces/FeedReader.js").isFile()).isTrue();

        assertThat(new File(mojo1.workTestDir, "SyndicationService.js").isFile()).isTrue();
        assertThat(new File(mojo1.workTestDir, "interfaces").isDirectory()).isTrue();
        assertThat(new File(mojo1.workTestDir, "interfaces/FeedEntry.js").isFile()).isTrue();
        assertThat(new File(mojo1.workTestDir, "interfaces/FeedReader.js").isFile()).isTrue();
    }

    @Test
    public void testWhenCoffeeScriptDirectoryDoesNotExist() throws MojoExecutionException,
            MojoFailureException {
        CoffeeScriptCompilerMojo mojo = new CoffeeScriptCompilerMojo();
        mojo.coffeeScriptDir = new File("src/test/resources/coffee_donotexist");
        mojo.coffeeScriptTestDir = new File("src/test/resources/coffee_donotexist");
        mojo.workDir = new File("target/test/testWhenCoffeeScriptDirectoryDoesNotExist-www");
        mojo.workTestDir = new File("target/test/testWhenCoffeeScriptDirectoryDoesNotExist-www-test");
        mojo.pluginDependencies = new ArrayList<Artifact>();
        mojo.pluginDependencies.add(new FakeArtifact("org.nanoko.libs", "coffeescript", "1.6.2",
                new File("src/test/resources/jslibs/coffee-script.js")));
        mojo.execute();

        assertThat(mojo.workDir.list()).isNull();
        assertThat(mojo.workTestDir.list()).isNull();
    }
}
