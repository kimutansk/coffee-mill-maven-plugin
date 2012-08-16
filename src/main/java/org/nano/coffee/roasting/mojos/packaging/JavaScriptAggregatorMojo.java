package org.nano.coffee.roasting.mojos.packaging;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.nano.coffee.roasting.mojos.AbstractRoastingCoffeeMojo;
import org.nano.coffee.roasting.processors.JavaScriptAggregator;
import org.nano.coffee.roasting.processors.Processor;
import ro.isdc.wro.extensions.processor.support.linter.JsHint;
import ro.isdc.wro.extensions.processor.support.linter.JsLint;
import ro.isdc.wro.extensions.processor.support.linter.LinterError;
import ro.isdc.wro.extensions.processor.support.linter.LinterException;

import java.io.*;
import java.util.*;

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
public class JavaScriptAggregatorMojo extends AbstractRoastingCoffeeMojo {

    /**
     * @parameter
     */
    protected List<String> javascriptAggregation;

    public void execute() throws MojoExecutionException {
        File output = new File(getWorkDirectory(), project.getBuild().getFinalName() + ".js");

        JavaScriptAggregator aggregator = new JavaScriptAggregator();
        Map<String, Object> options = new HashMap<String, Object>();
        options.put("output", output);
        options.put("work", getWorkDirectory());
        options.put("names", javascriptAggregation);
        try {
            aggregator.process(null, options);
        } catch (Processor.ProcessorException e) {
            throw new MojoExecutionException("Cannot aggregate JavaScript files", e);
        }

        if (output.isFile()) {
            try {
                FileUtils.copyFileToDirectory(output, new File(project.getBasedir(), project.getBuild().getDirectory()));
                project.getArtifact().setFile(output);
            } catch (IOException e) {
                throw new MojoExecutionException("Cannot copy the aggregated file to the target folder", e);
            }
        } else {
            throw new MojoExecutionException("Cannot copy the aggregated file to the target folder - the output file " +
                    "does not exist");
        }
    }

}
