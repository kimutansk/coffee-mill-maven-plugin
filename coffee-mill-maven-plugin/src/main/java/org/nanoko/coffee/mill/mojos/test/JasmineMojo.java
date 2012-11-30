package org.nanoko.coffee.mill.mojos.test;

import com.github.searls.jasmine.*;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.nanoko.coffee.mill.mojos.AbstractCoffeeMillMojo;
import org.nanoko.coffee.mill.utils.JasmineUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @goal jasmine-test
 */
public class JasmineMojo extends AbstractCoffeeMillMojo {


    /**
     * @parameter default-value="false"
     */
    protected boolean skipJasmineTest;

    /**
     * The list in order of the javascript file to include.
     * This list is shared with the javascript aggregation.
     * @parameter
     */
    protected List<String> javascriptAggregation;


    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skipJasmineTest) {
            getLog().debug("Skipping Jasmine Tests");
            return;
        }
        File test = new File(project.getBasedir(), "src/test");
        if (! test.exists()) {
            getLog().debug("Skipping Jasmine Tests - src/test not found");
            return;
        }

        // Process-Resource goal

        // Prepare execution
        // Copy target/work to target/jasmine/src
        try {
            FileUtils.copyDirectory(getWorkDirectory(), JasmineUtils.getJasmineSourceDirectory(project));
        } catch (IOException e) {
            throw new MojoExecutionException("Cannot prepare Jasmine execution", e);
        }

        // Copy target/work-test to target/jasmine/spec
        try {
            FileUtils.copyDirectory(getWorkTestDirectory(), JasmineUtils.getJasmineSpecDirectory(project));
        } catch (IOException e) {
            throw new MojoExecutionException("Cannot prepare Jasmine execution", e);
        }

        // Skip the execution if the WorkTest directory is empty
        if (getWorkTestDirectory().list().length == 0) {
            getLog().debug("Skipping Jasmine Tests - no spec found in the directory");
            return;
        }

        try {
            TestMojo testMojo = new TestMojo();
            JasmineUtils.prepareJasmineMojo(this, testMojo, javascriptAggregation);
            testMojo.execute();
        } finally {
            File report = new File(JasmineUtils.getJasmineDirectory(project), JasmineUtils.TEST_JASMINE_XML);
            JasmineUtils.copyJunitReport(this, report, "jasmine.test");
        }
    }
}
