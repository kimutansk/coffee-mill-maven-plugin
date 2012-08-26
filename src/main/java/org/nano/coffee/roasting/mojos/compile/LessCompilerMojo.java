package org.nano.coffee.roasting.mojos.compile;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.nano.coffee.roasting.mojos.AbstractRoastingCoffeeMojo;
import org.nano.coffee.roasting.processors.LessCompilationProcessor;
import org.nano.coffee.roasting.processors.Processor;
import ro.isdc.wro.extensions.processor.support.less.LessCss;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * Compiles Less files.
 *
 * @goal compile-less
 *
 */
public class LessCompilerMojo extends AbstractRoastingCoffeeMojo {

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
