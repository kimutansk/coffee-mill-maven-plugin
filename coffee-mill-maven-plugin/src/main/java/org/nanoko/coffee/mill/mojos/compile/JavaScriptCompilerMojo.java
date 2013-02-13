/*
 * Copyright 2013 OW2 Nanoko Project
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.nanoko.coffee.mill.mojos.compile;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.nanoko.coffee.mill.mojos.AbstractCoffeeMillMojo;
import org.nanoko.coffee.mill.processors.*;
import org.nanoko.coffee.mill.utils.OptionsHelper;

/**
 * Copy JavaScript sources to the <tt>work</tt> directory and check JavaScript sources with
 * <ul>
 *     <li>Check the code using JSLint</li>
 *     <li>Check the code using JSHint</li>
 *     <li>Compile dust template (<tt>.dust</tt> files)</li>
 * </ul>
 *
 * @goal compile-javascript
 *
 */
public class JavaScriptCompilerMojo extends AbstractCoffeeMillMojo {

    /**
     * Enables / disables JSLint
     *
     * @parameter default-value="false"
     */
    protected boolean skipJsLint;

    /**
     * Enables / disables JSHint
     *
     * @parameter default-value="false"
     */
    protected boolean skipJsHint;

    /**
     * Enables / disables dust compilation
     *
     * @parameter default-value="false"
     */
    protected boolean skipDustCompilation;


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

        if ( ! skipDustCompilation) {
            doDust();
        }  else {
            getLog().debug("Dust Compilation skipped");
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

    private void doDust() throws MojoExecutionException {
        DustJSProcessor processor = new DustJSProcessor();
        processor.configure(this, null);
        try {
            processor.processAll();
        } catch (Processor.ProcessorException e) {
            throw new MojoExecutionException("", e);
        }
    }
}
