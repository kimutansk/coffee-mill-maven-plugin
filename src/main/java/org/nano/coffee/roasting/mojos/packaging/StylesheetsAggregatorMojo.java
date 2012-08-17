package org.nano.coffee.roasting.mojos.packaging;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.nano.coffee.roasting.mojos.AbstractRoastingCoffeeMojo;
import org.nano.coffee.roasting.processors.CSSAggregator;
import org.nano.coffee.roasting.processors.JavaScriptAggregator;
import org.nano.coffee.roasting.processors.Processor;

import java.io.*;
import java.util.ArrayList;
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
public class StylesheetsAggregatorMojo extends AbstractRoastingCoffeeMojo {

    /**
     * @parameter
     */
    protected List<String> cssAggregation;


    public void execute() throws MojoExecutionException, MojoFailureException {

        // Do we have css files ?
        if (FileUtils.listFiles(getWorkDirectory(), new String[] {"css"}, true).size() == 0) {
            getLog().info("Skipping Stylessheets aggregation - no files");
            return;
        }

        File output = new File(getWorkDirectory(), project.getBuild().getFinalName() + ".css");

        Processor aggregator = new CSSAggregator();
        Map<String, Object> options = new HashMap<String, Object>();
        options.put("output", output);
        options.put("work", getWorkDirectory());
        options.put("names", cssAggregation);
        options.put("extension", "css");
        options.put("libs", getLibDirectory());
        try {
            aggregator.process(null, options);
        } catch (Processor.ProcessorException e) {
            throw new MojoExecutionException("Cannot aggregate CSS files", e);
        }

        if (output.isFile()) {
            try {
                FileUtils.copyFileToDirectory(output, getTarget());
                // Do we already have a main JS artifact ?
                if (project.getArtifact().getFile().exists()) {
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
