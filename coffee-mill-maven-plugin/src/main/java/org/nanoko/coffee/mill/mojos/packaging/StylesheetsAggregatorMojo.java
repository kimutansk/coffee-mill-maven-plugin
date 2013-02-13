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

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.nanoko.coffee.mill.mojos.AbstractCoffeeMillMojo;
import org.nanoko.coffee.mill.processors.CSSAggregator;
import org.nanoko.coffee.mill.processors.Processor;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Aggregate stylesheets sources:
 *
 * Aggregation order can be specified in the Maven Configuration
 * It can also include:
 * <ul>
 *     <li>The CSS file from the project ((<tt>filename.js</tt>)</li>
 *     <li>Compiled files (LESS) (<tt>less_filename.js</tt>)</li>
 * </ul>
 *
 * If no file are included, the project CSS files are aggregated in the alphabetical order.
 *
 * @goal aggregate-stylesheets
 */
public class StylesheetsAggregatorMojo extends AbstractCoffeeMillMojo {

    /**
     * @parameter
     */
    protected List<String> cssAggregation;


    public void execute() throws MojoExecutionException, MojoFailureException {

        // Do we have css files ?
        System.out.println(getWorkDirectory().getAbsolutePath());
        if (FileUtils.listFiles(getWorkDirectory(), new String[] {"css"}, true).size() == 0) {
            getLog().info("Skipping Stylessheets aggregation - no files");
            return;
        }

        File output = new File(getWorkDirectory(), project.getBuild().getFinalName() + ".css");

        Processor aggregator = new CSSAggregator();
        Map<String, Object> options = new HashMap<String, Object>();
        options.put("output", output);
        options.put("names", cssAggregation);
        options.put("extension", "css");
        aggregator.configure(this, options);
        try {
            aggregator.processAll();
        } catch (Processor.ProcessorException e) {
            throw new MojoExecutionException("Cannot aggregate CSS files", e);
        }

        if (output.isFile()) {
            try {
                FileUtils.copyFileToDirectory(output, getTarget());
                // Do we already have a main JS artifact ?
                if (project.getArtifact().getFile() != null  && project.getArtifact().getFile().exists()) {
                    projectHelper.attachArtifact(project, "css", output);
                } else {
                    project.getArtifact().setFile(output);
                }
            } catch (IOException e) {
                throw new MojoExecutionException("Cannot copy the aggregated file to the target folder", e);
            }
        } else {
            throw new MojoExecutionException("Cannot copy the aggregated file to the target folder - the output file " +
                    "does not exist");
        }

    }
}
