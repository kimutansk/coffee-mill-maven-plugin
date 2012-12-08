package org.nanoko.coffee.mill.mojos.compile;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.nanoko.coffee.mill.mojos.AbstractCoffeeMillMojo;
import org.nanoko.coffee.mill.processors.LessCompilationProcessor;
import org.nanoko.coffee.mill.processors.OptiPNGProcessor;
import org.nanoko.coffee.mill.processors.Processor;
import org.nanoko.coffee.mill.utils.OptionsHelper;

/**
 * Optimizes PNG files using optiPNG (http://optipng.sourceforge.net/).
 * OptiPNG must be installed and the executable `optipng` available form the system path.
 * @goal optimize-png
 *
 */
public class OptiPNGMojo extends AbstractCoffeeMillMojo {

    OptiPNGProcessor processor;

    /**
     * Enables the verbose mode of optiPNG.
     * @parameter default-value=true
     */
    public boolean optiPNGVerbose;

    /**
     * The optiPNG optimization level among 0-7.
     * @parameter default-value=2
     */
    protected int optiPngOptimizationLevel;

    public OptiPNGMojo() {
        processor = new OptiPNGProcessor();
    }

    public void execute() throws MojoExecutionException, MojoFailureException {

        if (optiPngOptimizationLevel < 0  || optiPngOptimizationLevel > 7) {
            throw new MojoExecutionException("Invalid optiPNG optimization level, it must be in [0-7]");
        }


        processor.configure(this, new OptionsHelper.OptionsBuilder().set("verbose", true).set("level",
                optiPngOptimizationLevel).build());

        try {
            processor.processAll();
        } catch (Processor.ProcessorException e) {
            throw new MojoFailureException("PNG Optimization failed", e);
        }
    }

}
