package org.nanoko.coffee.mill.mojos.compile;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.nanoko.coffee.mill.mojos.AbstractCoffeeMillMojo;
import org.nanoko.coffee.mill.processors.JpegTranProcessor;
import org.nanoko.coffee.mill.processors.Processor;
import org.nanoko.coffee.mill.utils.OptionsHelper;

/**
 * Optimizes JPEG files using jpegtran (http://jpegclub.org/jpegtran/).
 * JpegTran must be installed and the executable `jpegtran` available form the system path.
 * @goal optimize-jpeg
 *
 */
public class JpegTranMojo extends AbstractCoffeeMillMojo {

    JpegTranProcessor processor;

    /**
     * Enables the verbose mode of optiPNG.
     * @parameter default-value=true
     */
    public boolean jpegTranVerbose;


    public JpegTranMojo() {
        processor = new JpegTranProcessor();
    }

    public void execute() throws MojoExecutionException, MojoFailureException {


        processor.configure(this, new OptionsHelper.OptionsBuilder().set("verbose", true).build());

        try {
            processor.processAll();
        } catch (Processor.ProcessorException e) {
            throw new MojoFailureException("JPEG Optimization failed", e);
        }
    }

}
