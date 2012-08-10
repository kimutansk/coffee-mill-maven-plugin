package org.nano.coffee.roasting.mojos.compile;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.nano.coffee.roasting.mojos.AbstractRoastingCoffeeMojo;
import ro.isdc.wro.extensions.processor.support.csslint.CssLint;
import ro.isdc.wro.extensions.processor.support.csslint.CssLintError;
import ro.isdc.wro.extensions.processor.support.csslint.CssLintException;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

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
        Collection<File> files = FileUtils.listFiles(getWorkDirectory(), new String[]{"css"}, true);
        for (File file : files) {
            CssLint lint = new CssLint();
            try {
                lint.validate(FileUtils.readFileToString(file));
            } catch (IOException e) {
                throw new MojoFailureException("Can't check " + file.getAbsolutePath(), e);
            } catch (CssLintException e) {
                for (CssLintError exp : e.getErrors()) {
                    getLog().warn("In " + file.getName() + " at " + exp.getLine() + ":" + exp.getCol()
                            + " - "
                            + exp.getType() + " - " + exp.getMessage() + " (" + exp.getEvidence() + ")");
                }
            }
        }
    }
}
