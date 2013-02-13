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

package org.nanoko.coffee.mill.mojos.packaging;

import com.yahoo.platform.yui.compressor.CssCompressor;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.nanoko.coffee.mill.mojos.AbstractCoffeeMillMojo;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Minify CSS sources.
 * It can use the CSS compressor from YUI.
 * The minified file is attached to the project using the <tt>min</tt> classifier,
 * except if the <tt>attachMinifiedCSS</tt> parameter is set to <tt>false</tt>
 * @goal minify-stylesheets
 */
public class StylesheetsMinifierMojo extends AbstractCoffeeMillMojo {

    /**
     * Enables to skip the minification phase.
     * @parameter default-value="false"
     */
    protected boolean skipMinification;

    /**
     * Enables / Disables the attachment of the minified file to the Maven project.
     * Enabled by default.
     * @parameter default-value="true"
     */
    protected boolean attachMinifiedCSS;


    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skipMinification) {
            getLog().debug("Stylesheets Minification skipped");
            return;

        }
        File input = new File(getTarget(), project.getBuild().getFinalName() + ".css");
        if (! input.exists()) {
            getLog().info("Stylesheets Minification skipped, no CSS file");
            return;
        }

        File output = new File(getTarget(), project.getBuild().getFinalName() + "-min.css");

        doYUICompression(input, output);


        if (! output.isFile()) {
            throw new MojoFailureException("The minified file "+ output.getAbsolutePath() + " does not exist - check " +
                    "log.");
        }

        if (attachMinifiedCSS) {
            projectHelper.attachArtifact(project, "css" , "min" , output);
        }
    }

    private void doYUICompression(File file, File output) throws MojoExecutionException {
        getLog().info("Compressing " + file.getName() + " using YUI Compressor");
        FileWriter writer = null;
        FileReader reader = null;
        try {
            reader = new FileReader(file);
            writer = new FileWriter(output);
            final CssCompressor compressor = new CssCompressor(reader);
            compressor.compress(writer, -1);
        } catch(IOException e) {
            getLog().error("Exception occurred while minifying file: " + file.getAbsolutePath());
            throw new MojoExecutionException("Exception during YuiCss processing of file: " +
                    file.getAbsolutePath(), e);
        } finally {
            if (reader != null) {
                IOUtils.closeQuietly(reader);
            }
            if (writer != null) {
                IOUtils.closeQuietly(writer);
            }
        }
    }


}
