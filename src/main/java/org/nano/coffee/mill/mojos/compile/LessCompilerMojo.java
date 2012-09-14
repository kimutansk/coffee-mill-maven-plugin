package org.nano.coffee.mill.mojos.compile;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.nano.coffee.mill.mojos.AbstractCoffeeMillMojo;
import org.nano.coffee.mill.processors.LessCompilationProcessor;
import org.nano.coffee.mill.processors.Processor;

/**
 * Compiles Less files.
 *
 * @goal compile-less
 *
 */
public class LessCompilerMojo extends AbstractCoffeeMillMojo {

    LessCompilationProcessor processor;

    public LessCompilerMojo() {
        processor = new LessCompilationProcessor();
    }


    public void execute() throws MojoExecutionException, MojoFailureException {
        processor.configure(this, null);

        if (! stylesheetsDir.exists()) {
            getLog().debug("The stylesheet directory does not exist - skipping LESS compilation");
            return;
        }

        try {
            processor.processAll();
        } catch (Processor.ProcessorException e) {
            throw new MojoFailureException("Less compilation failed", e);
        }
    }

    public LessCompilationProcessor getProcessor() {
        processor.configure(this, null);
        return processor;
    }
}
