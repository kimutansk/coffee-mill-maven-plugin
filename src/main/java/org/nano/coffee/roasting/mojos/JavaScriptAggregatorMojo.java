package org.nano.coffee.roasting.mojos;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.nano.coffee.roasting.processors.JavaScriptAggregator;
import ro.isdc.wro.extensions.processor.support.linter.JsHint;
import ro.isdc.wro.extensions.processor.support.linter.JsLint;
import ro.isdc.wro.extensions.processor.support.linter.LinterError;
import ro.isdc.wro.extensions.processor.support.linter.LinterException;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Aggregate JavaScript sources:
 *
 * Aggregation order can be specified in the Maven Configuration
 * It can also include:
 * <ul>
 *     <li>The Javascript file from the project ((<tt>filename.js</tt>)</li>
 *     <li>Dependencies files (<tt>artifactId.js</tt>)</li>
 *     <li>Compile files (CoffeeScript) (<tt>coffee_filename.js</tt>)</li>
 * </ul>
 *
 * If no file are included, the project js files are aggregated in the alphabetical order.
 *
 * @goal aggregate-javascript
 */
public class JavaScriptAggregatorMojo extends AbstractRoastingCoffeeMojo {

    /**
     * @parameter
     */
    protected List<String> javascriptAggregation;

    public void execute() throws MojoExecutionException, MojoFailureException {

        File output = new File("target", project.getBuild().getFinalName() + ".js");

        JavaScriptAggregator aggregator = new JavaScriptAggregator(getWorkDirectory(), output, javascriptAggregation);
        aggregator.process();

        project.getArtifact().setFile(output);

    }

}
