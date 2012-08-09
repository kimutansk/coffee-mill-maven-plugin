package org.nano.coffee.roasting.mojos;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.nano.coffee.roasting.processors.CSSAggregator;
import org.nano.coffee.roasting.processors.JavaScriptAggregator;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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

        File output = new File("target", project.getBuild().getFinalName() + ".css");

        CSSAggregator aggregator = new CSSAggregator(getWorkDirectory(), output, cssAggregation);
        aggregator.process();

        if (output.exists()) {
            // Do we already have a main JS artifact ?
            if (project.getArtifact().getFile().exists()) {
                projectHelper.attachArtifact(project, "css", output);
            } else {
                project.getArtifact().setFile(output);
            }
        }

    }
}
