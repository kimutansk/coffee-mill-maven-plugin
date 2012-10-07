package org.nano.coffee.mill.processors;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;
import org.nano.coffee.mill.mojos.compile.CoffeeScriptCompilerMojo;
import org.nano.coffee.mill.mojos.compile.CoffeeScriptTestCompilerMojo;

import java.io.File;

import static org.fest.assertions.Assertions.assertThat;

public class CoffeeScriptCompilationProcessorTest {

    @Test
    public void testCoffeeScriptCompilation() throws MojoExecutionException, MojoFailureException {
        CoffeeScriptCompilerMojo mojo = new CoffeeScriptCompilerMojo();
        mojo.coffeeScriptDir = new File("src/test/resources/coffee");
        mojo.coffeeScriptTestDir = new File("src/test/resources/coffee_donotexist");
        mojo.workDir = new File("target/test/testCoffeeScriptCompilation-www");
        mojo.workTestDir = new File("target/test/testCoffeeScriptCompilation-www-test");
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
        mojo1.execute();

        mojo2.coffeeScriptDir = new File("src/test/resources/coffee");
        mojo2.coffeeScriptTestDir = new File("src/test/resources/coffee");
        mojo2.workDir = new File("target/test/testCoffeeScriptTestAndMainCompilation-www");
        mojo2.workTestDir = new File("target/test/testCoffeeScriptTestAndMainCompilation-www-test");
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
        mojo.execute();

        assertThat(mojo.workDir.list()).isNull();
        assertThat(mojo.workTestDir.list()).isNull();
    }
}
