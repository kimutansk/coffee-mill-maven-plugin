package org.nano.coffee.roasting.mojos.test;

import com.github.searls.jasmine.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.nano.coffee.roasting.InjectionHelper;
import org.nano.coffee.roasting.mojos.AbstractRoastingCoffeeMojo;
import org.nano.coffee.roasting.processors.CSSAggregator;
import org.nano.coffee.roasting.utils.JasmineUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @goal jasmine-test
 */
public class JasmineMojo extends AbstractRoastingCoffeeMojo {


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
