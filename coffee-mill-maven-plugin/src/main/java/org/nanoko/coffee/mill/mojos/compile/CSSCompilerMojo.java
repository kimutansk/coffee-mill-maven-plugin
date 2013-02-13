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

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.nanoko.coffee.mill.mojos.AbstractCoffeeMillMojo;
import org.nanoko.coffee.mill.processors.CSSFileCopyProcessor;
import org.nanoko.coffee.mill.processors.CSSLintProcessor;
import org.nanoko.coffee.mill.processors.Processor;
import org.nanoko.coffee.mill.utils.OptionsHelper;

import java.io.File;
import java.util.Collection;

/**
 * Copy CSS to the <tt>work</tt> directory and check CSS file using CSSLint.
 *
 * @goal compile-css
 */
public class CSSCompilerMojo extends AbstractCoffeeMillMojo {

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

        CSSFileCopyProcessor processor = new CSSFileCopyProcessor();
        processor.configure(this, null);
        try {
            processor.processAll();
        } catch (Processor.ProcessorException e) {
            throw new MojoExecutionException("Cannot copy CSS files", e);
        }

        if (! skipCSSLint) {
            lint();
        }
    }

    private void lint() throws MojoFailureException {
        CSSLintProcessor processor = new CSSLintProcessor();
        processor.configure(this, new OptionsHelper.OptionsBuilder().set("directory", getWorkDirectory()).build());

        Collection<File> files = FileUtils.listFiles(getWorkDirectory(), new String[]{"css"}, true);
        for (File file : files) {
            try {
                processor.processAll();
            } catch (Processor.ProcessorException e) {
                getLog().error("Cannot run the CSS Lint Processor on " + file.getAbsolutePath(), e);
            }
        }
    }
}
