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
import org.nanoko.coffee.mill.mojos.AbstractCoffeeMillMojo;
import org.nanoko.coffee.mill.processors.JavaScriptAggregator;
import org.nanoko.coffee.mill.processors.Processor;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Aggregate JavaScript sources:
 * <p/>
 * Aggregation order can be specified in the Maven Configuration
 * It can also include:
 * <ul>
 * <li>The Javascript file from the project ((<tt>filename.js</tt>)</li>
 * <li>Dependencies files (<tt>artifactId.js</tt>)</li>
 * <li>Compile files (CoffeeScript) (<tt>coffee_filename.js</tt>)</li>
 * </ul>
 * <p/>
 * If no file are included, the project js files are aggregated in the alphabetical order.
 *
 * @goal aggregate-javascript
 */
public class JavaScriptAggregatorMojo extends AbstractCoffeeMillMojo {

    /**
     * @parameter
     */
    protected List<String> javascriptAggregation;

    public void execute() throws MojoExecutionException {
        // Do we have js files ?
        if (FileUtils.listFiles(getWorkDirectory(), new String[] {"js"}, true).size() == 0) {
            getLog().info("Skipping JavaScript aggregation - no files");
            return;
        }

        File output = new File(getWorkDirectory(), project.getBuild().getFinalName() + ".js");

        JavaScriptAggregator aggregator = new JavaScriptAggregator();
        Map<String, Object> options = new HashMap<String, Object>();
        options.put("output", output);
        options.put("names", javascriptAggregation);
        options.put("extension", "js");
        aggregator.configure(this, options);
        try {
            aggregator.processAll();
        } catch (Processor.ProcessorException e) {
            throw new MojoExecutionException("Cannot aggregate JavaScript files", e);
        }

        if (output.isFile()) {
            try {
                File artifact = new File(getTarget(), project.getBuild().getFinalName() + ".js");
                getLog().info("Copying " + output.getAbsolutePath() + " to the " + artifact.getAbsolutePath());
                FileUtils.copyFile(output, artifact, true);
                project.getArtifact().setFile(artifact);
            } catch (IOException e) {
                throw new MojoExecutionException("Cannot copy the aggregated file to the target folder", e);
            }
        } else {
            throw new MojoExecutionException("Cannot copy the aggregated file to the target folder - the output file " +
                    "does not exist");
        }
    }

}
