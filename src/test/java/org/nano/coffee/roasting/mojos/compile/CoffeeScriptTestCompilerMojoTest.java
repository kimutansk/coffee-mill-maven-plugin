package org.nano.coffee.roasting.mojos.compile;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;

import java.io.File;

/**
 * Test the CoffeeScriptTestCompilerMojo.
 */
public class CoffeeScriptTestCompilerMojoTest {

    @Test
    public void testCoffeeScriptCompilation() {
        // TODO
    }

    @Test
    public void testSkippedCoffeeScriptCompilation() throws MojoExecutionException, MojoFailureException {
        CoffeeScriptTestCompilerMojo mojo = new CoffeeScriptTestCompilerMojo();
        mojo.skipCoffeeScriptCompilation = true;
        mojo.execute();
    }

    @Test
    public void testSkippedTestCoffeeScriptCompilation() throws MojoExecutionException, MojoFailureException {
        CoffeeScriptTestCompilerMojo mojo = new CoffeeScriptTestCompilerMojo();
        mojo.skipCoffeeScriptTestCompilation = true;
        mojo.execute();
    }


    @Test
    public void testSkippedCoffeeScriptCompilationBecauseOfMissingFolder() throws MojoExecutionException,
            MojoFailureException {
        CoffeeScriptTestCompilerMojo mojo = new CoffeeScriptTestCompilerMojo();
        mojo.skipCoffeeScriptCompilation = false;
        mojo.coffeeScriptDir = new File("doesNotExist");
        mojo.coffeeScriptTestDir = new File("doesNotExist");
        mojo.execute();
    }

}
