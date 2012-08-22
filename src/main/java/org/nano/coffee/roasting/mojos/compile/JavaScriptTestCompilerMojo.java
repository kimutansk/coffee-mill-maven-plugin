package org.nano.coffee.roasting.mojos.compile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.nano.coffee.roasting.mojos.AbstractRoastingCoffeeMojo;
import ro.isdc.wro.extensions.processor.support.linter.JsHint;
import ro.isdc.wro.extensions.processor.support.linter.JsLint;
import ro.isdc.wro.extensions.processor.support.linter.LinterError;
import ro.isdc.wro.extensions.processor.support.linter.LinterException;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * Copy JavaScript sources to the <tt>work</tt> directory and check JavaScript sources with
 * <ul>
 *     <li>Check the code using JSLint</li>
 *     <li>Check the code using JSHint</li>
 * </ul>
 * TODO Exclude strict mode.
 *
 * @goal test-compile-javascript
 *
 */
public class JavaScriptTestCompilerMojo extends AbstractRoastingCoffeeMojo {

    /**
     * Sets to true to disable JSLint
     *
     * @parameter default-value="true"
     */
    protected boolean skipJsLint;

    /**
     * Sets to true to disable JSHint
     *
     * @parameter default-value="true"
     */
    protected boolean skipJsHint;


    public void execute() throws MojoExecutionException, MojoFailureException {
        if (! javaScriptTestDir.exists()) {
            getLog().debug("The javascript directory does not exist - skipping JavaScript compilation");
            return;
        }

        copyJavascriptFiles();

        if (! skipJsLint) {
            doJsLint();
        } else {
            getLog().debug("JS Lint skipped");
        }

        if (! skipJsHint) {
            doJsHint();
        } else {
            getLog().debug("JS Hint skipped");
        }

    }


    private void copyJavascriptFiles() throws MojoFailureException {
        // Create a filter for ".js" files
        IOFileFilter jsSuffixFilter = FileFilterUtils.suffixFileFilter(".js");
        IOFileFilter jsFiles = FileFilterUtils.and(FileFileFilter.FILE, jsSuffixFilter);

        // Create a filter for either directories or ".js" files
        IOFileFilter filter = FileFilterUtils.or(DirectoryFileFilter.DIRECTORY, jsFiles);

        // Copy using the filter
        try {
            FileUtils.copyDirectory(javaScriptTestDir, getWorkTestDirectory(), filter);
        } catch (IOException e) {
            throw new MojoFailureException("", e);
        }
    }

    private void doJsLint() {
        getLog().info("Checking sources with JsLint");
        JsLint processor = new JsLint();
        int errorCount = 0;
        Collection<File> files = FileUtils.listFiles(getWorkTestDirectory(), new String[] {"js"}, true);
        for (File file : files) {
            getLog().debug("JSLint-ing " + file.getAbsolutePath());
            try {
                String data = FileUtils.readFileToString(file);
                // Prepend options:
                data = "/*jslint sloppy:true */\n" + data;
                processor.validate(data);
            } catch (IOException e) {
                getLog().error("Can't analyze " + file.getAbsolutePath() + " with JSLint",e);
            } catch (LinterException e) {
                errorCount += e.getErrors().size();
                if (! e.getErrors().isEmpty()) {
                    for (LinterError exp : e.getErrors()) {
                        if (exp == null) {
                            continue; // How can this be ever possible ?
                        }
                        String message = "";
                        if (exp.getEvidence() != null) {
                            message += " - " + exp.getEvidence();
                        }
                        if (exp.getReason() != null) {
                            message += " - " + exp.getReason();
                        }
                        getLog().warn("In " + file.getName() + " at " + exp.getLine() + ":" + exp.getCharacter()
                                + message);
                    }
                }
            }
        }
        if (errorCount == 0) {
            getLog().info("Well Done ! No warning found during the JSLint analysis");
        } else {
            getLog().info(errorCount + " warning(s) found during the JSLint analysis");
        }
    }

    private void doJsHint() {
        getLog().info("Checking sources with JsHint");
        JsHint processor = new JsHint();
        int errorCount = 0;
        Collection<File> files = FileUtils.listFiles(getWorkTestDirectory(), new String[] {"js"}, true);
        for (File file : files) {
            getLog().debug("JSHint-ing " + file.getAbsolutePath());
            try {
                processor.validate(FileUtils.readFileToString(file));
            } catch (IOException e) {
                getLog().error("Can't analyze " + file.getAbsolutePath() + " with JSHint",e);
            } catch (LinterException e) {
                errorCount += e.getErrors().size();
                if (! e.getErrors().isEmpty()) {
                    for (LinterError exp : e.getErrors()) {
                        if (exp == null) {
                            continue;
                        }
                        getLog().warn("In " + file.getName() + " at " + exp.getLine() + ":" + exp.getCharacter()
                                + " - "
                                + exp.getEvidence() + " - " + exp.getReason());
                    }
                }
            }
        }
        if (errorCount == 0) {
            getLog().info("Well Done ! No warning found during the JSHint analysis");
        } else {
            getLog().info(errorCount + " warning(s) found during the JSHint analysis");
        }
    }
}
