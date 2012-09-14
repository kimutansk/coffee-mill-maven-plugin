package org.nano.coffee.mill.mojos.compile;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.nano.coffee.mill.mojos.AbstractCoffeeMillMojo;
import org.nano.coffee.mill.processors.JSHintProcessor;
import org.nano.coffee.mill.processors.JSLintProcessor;
import org.nano.coffee.mill.processors.JavaScriptFileCopyProcessor;
import org.nano.coffee.mill.processors.Processor;
import org.nano.coffee.mill.utils.OptionsHelper;

/**
 * Copy JavaScript sources to the <tt>work</tt> directory and check JavaScript sources with
 * <ul>
 *     <li>Check the code using JSLint</li>
 *     <li>Check the code using JSHint</li>
 * </ul>
 *
 * @goal compile-javascript
 *
 */
public class JavaScriptCompilerMojo extends AbstractCoffeeMillMojo {

    /**
     * Sets to true to disable JSLint
     *
     * @parameter default-value="false"
     */
    protected boolean skipJsLint;

    /**
     * Sets to true to disable JSHint
     *
     * @parameter default-value="false"
     */
    protected boolean skipJsHint;


    public void execute() throws MojoExecutionException, MojoFailureException {
        if (! javaScriptDir.exists()) {
            getLog().debug("The javascript directory does not exist - skipping JavaScript compilation");
            return;
        }

        JavaScriptFileCopyProcessor processor = new JavaScriptFileCopyProcessor();
        processor.configure(this, new OptionsHelper.OptionsBuilder().set("test", false).build());
        try {
            processor.processAll();
        } catch (Processor.ProcessorException e) {
            throw new MojoExecutionException("Cannot copy JavaScript files", e);
        }

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

    private void doJsLint() throws MojoExecutionException {
        getLog().info("Checking sources with JsLint");
        JSLintProcessor processor = new JSLintProcessor();
        processor.configure(this, null);
        try {
            processor.processAll();
        } catch (Processor.ProcessorException e) {
            throw new MojoExecutionException("", e);
        }
    }

    private void doJsHint() throws MojoExecutionException {
        getLog().info("Checking sources with JsHint");
        JSHintProcessor processor = new JSHintProcessor();
        processor.configure(this, null);
        try {
            processor.processAll();
        } catch (Processor.ProcessorException e) {
            throw new MojoExecutionException("", e);
        }
    }
}
