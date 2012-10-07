package org.nano.coffee.mill.mojos.processResources;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.nano.coffee.mill.mojos.AbstractCoffeeMillMojo;
import org.nano.coffee.mill.processors.CopyAssetProcessor;
import org.nano.coffee.mill.processors.Processor;

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
