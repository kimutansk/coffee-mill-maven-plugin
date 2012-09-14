package org.nano.coffee.mill.mojos.compile;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.nano.coffee.mill.mojos.AbstractCoffeeMillMojo;
import org.nano.coffee.mill.processors.CoffeeScriptCompilationProcessor;
import org.nano.coffee.mill.processors.Processor;
import org.nano.coffee.mill.utils.OptionsHelper;

/**
 * Compiles CoffeeScript files.
 * CoffeeScript files are generally in the <tt>src/main/coffee</tt> directory. It can be configured using the
 * <tt>coffeeScriptDir</tt> parameter.
 * If the directory does not exist, the compilation is skipped.
 * @goal compile-coffeescript
 */
public class CoffeeScriptCompilerMojo extends AbstractCoffeeMillMojo {

    /**
     * Enables / Disables the coffeescript compilation.
     * Be aware that this property disables the compilation on both main sources and test sources.
     * @parameter default-value="false"
     */
    protected boolean skipCoffeeScriptCompilation;

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skipCoffeeScriptCompilation) {
            getLog().info("CoffeeScript compilation skipped");
            return;
        }

        if (! coffeeScriptDir.exists()) {
            getLog().info("CoffeeScript compilation skipped - " + coffeeScriptDir.getAbsolutePath() + " does not " +
                    "exist");
            return;
        }

        CoffeeScriptCompilationProcessor processor = new CoffeeScriptCompilationProcessor();
        processor.configure(this, new OptionsHelper.OptionsBuilder().set("test", false).build());
        try {
            processor.processAll();
        } catch (Processor.ProcessorException e) {
            throw new MojoExecutionException("", e);
        }

    }



}
