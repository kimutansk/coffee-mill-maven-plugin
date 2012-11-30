package org.nanoko.coffee.mill.mojos.processResources;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.nanoko.coffee.mill.mojos.AbstractCoffeeMillMojo;
import org.nanoko.coffee.mill.processors.CopyAssetProcessor;
import org.nanoko.coffee.mill.processors.Processor;

/**
 * Copy src/main/www and src/main/resources to the www directory
 *
 * @goal copy-assets
 *
 */
public class CopyAssetsMojo extends AbstractCoffeeMillMojo {

    public void execute() throws MojoExecutionException, MojoFailureException {
        CopyAssetProcessor processor = new CopyAssetProcessor();
        processor.configure(this, null);
        try {
            processor.processAll();
        } catch (Processor.ProcessorException e) {
            throw new MojoExecutionException("", e);
        }
    }
}
