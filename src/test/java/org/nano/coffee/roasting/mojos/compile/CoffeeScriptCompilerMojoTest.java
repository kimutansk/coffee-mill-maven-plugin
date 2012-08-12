package org.nano.coffee.roasting.mojos.compile;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;
import org.nano.coffee.roasting.mojos.compile.CoffeeScriptCompilerMojo;

import java.io.File;

/**
 * Test the CoffeeScriptCompilerMojo.
 */
public class CoffeeScriptCompilerMojoTest {

    @Test
    public void testCoffeeScriptCompilation() {
        // TODO
    }

    @Test
    public void testSkippedCoffeeScriptCompilation() throws MojoExecutionException, MojoFailureException {
        CoffeeScriptCompilerMojo mojo = new CoffeeScriptCompilerMojo();
        mojo.skipCoffeeScriptCompilation = true;
        mojo.execute();
    }

    @Test
    public void testSkippedCoffeeScriptCompilationBecauseOfMissingFolder() throws MojoExecutionException,
            MojoFailureException {
        CoffeeScriptCompilerMojo mojo = new CoffeeScriptCompilerMojo();
        mojo.skipCoffeeScriptCompilation = false;
        mojo.coffeeScriptDir = new File("doesNotExist");
        mojo.execute();
    }

}
