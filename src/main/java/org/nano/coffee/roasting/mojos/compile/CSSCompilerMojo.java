package org.nano.coffee.roasting.mojos.compile;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.nano.coffee.roasting.mojos.AbstractRoastingCoffeeMojo;
import org.nano.coffee.roasting.processors.CSSLintProcessor;
import org.nano.coffee.roasting.processors.Processor;
import ro.isdc.wro.extensions.processor.support.csslint.CssLintError;
import ro.isdc.wro.extensions.processor.support.csslint.CssLintException;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Copy CSS to the <tt>work</tt> directory and check CSS file using CSSLint.
 *
 * @goal compile-css
 */
public class CSSCompilerMojo extends AbstractRoastingCoffeeMojo {

    /**
     * Sets to true to disable CSSLint
     *
     * @parameter default-value="false"
     */
    protected boolean skipCSSLint;


    public void execute() throws MojoExecutionException, MojoFailureException {
        if (! stylesheetsDir.exists()) {
            getLog().info("The stylesheet directory does not exist - skipping CSS compilation");
            return;
        }

        copyCSSFiles();

        if (! skipCSSLint) {
            lint();
        }
    }

    private void copyCSSFiles() throws MojoFailureException {
        Collection<File> files = FileUtils.listFiles(stylesheetsDir, new String[]{"css"}, true);
        for (File file : files) {
            try {
                FileUtils.copyFileToDirectory(file, getWorkDirectory());
            } catch (IOException e) {
                throw new MojoFailureException("Cannot copy " + file.getAbsolutePath() + " to the work directory", e);
            }
        }
        getLog().info(files.size() + " file copied to the " + getWorkDirectory().getAbsolutePath());
    }

    private void lint() throws MojoFailureException {
        CSSLintProcessor processor = new CSSLintProcessor();
        Map<String, Object> options = new HashMap<String, Object>();
        options.put("output", getWorkDirectory());
        options.put("logger", getLog());

        Collection<File> files = FileUtils.listFiles(getWorkDirectory(), new String[]{"css"}, true);
        for (File file : files) {
            try {
                processor.process(file, options);
            } catch (Processor.ProcessorException e) {
                getLog().error("Cannot run the CSS Lint Processor on " + file.getAbsolutePath(), e);
            }
        }
    }
}
