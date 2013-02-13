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

package org.nanoko.coffee.mill.mojos.test;

import com.github.searls.jasmine.TestMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.FileUtils;
import org.nanoko.coffee.mill.mojos.AbstractCoffeeMillMojo;
import org.nanoko.coffee.mill.utils.JasmineUtils;

import java.io.File;
import java.io.IOException;

/**
 * @goal jasmine-it-test
 */
public class JasmineITMojo extends AbstractCoffeeMillMojo {

    /**
     * @parameter default-value="false"
     */
    protected boolean skipJasmineITTest;

    /**
     * @parameter default-value="true"
     */
    protected boolean runJasmineTestOnAggregatedVersion;

    /**
     * @parameter default-value="true"
     */
    protected boolean runJasmineTestOnMinifiedVersion;

    /**
     * Where are JavaScript files implementing integration tests.
     *
     * @parameter default-value="src/integration-test/js"
     */
    public File javaScriptIntegrationTestDir;

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skipJasmineITTest) {
            getLog().debug("Skipping Jasmine Integration Tests");
            return;
        }
        if (! javaScriptIntegrationTestDir.exists()) {
            getLog().debug("Skipping Jasmine Tests - " + javaScriptIntegrationTestDir.getAbsolutePath() + " not found");
            return;
        }

        // Process-Resource goal

        // Prepare execution
        // Copy the right library to target/it-jasmine/src
        try {
            if (runJasmineTestOnAggregatedVersion) {
                File lib = new File(getTarget(), project.getBuild().getFinalName() + ".js");
                FileUtils.copyFileToDirectory(lib, JasmineUtils.getJasmineITSourceDirectory(project));
            }
            if (runJasmineTestOnMinifiedVersion) {
                File lib = new File(getTarget(), project.getBuild().getFinalName() + "-min.js");
                FileUtils.copyFileToDirectory(lib, JasmineUtils.getJasmineITSourceDirectory(project));
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Cannot prepare Jasmine execution", e);
        }

        // Copy target/work-test to target/jasmine/spec
        try {
            FileUtils.copyDirectory(getWorkTestDirectory(), JasmineUtils.getJasmineITSpecDirectory(project));
        } catch (IOException e) {
            throw new MojoExecutionException("Cannot prepare Jasmine execution", e);
        }

        if (runJasmineTestOnAggregatedVersion) {
            String library = project.getBuild().getFinalName() + ".js";
            String reportName = "TEST-jasmine-it-" + library + ".xml";
            try {
                getLog().info("Running integration tests on aggregated version");
                TestMojo testMojo = new TestMojo();
                JasmineUtils.prepareJasmineMojo(this, testMojo, null);
                JasmineUtils.extendJasmineMojoForIT(this, testMojo, reportName);
                JasmineUtils.configureJasmineToRunOnLibrary(testMojo, library);
                testMojo.execute();
            } finally {
                File report = new File(JasmineUtils.getJasmineITDirectory(project), reportName);
                JasmineUtils.copyJunitReport(this, report, "integration-test.jasmine.aggregated");
            }
        }

        if (runJasmineTestOnMinifiedVersion) {
            String library = project.getBuild().getFinalName() + "-min.js";
            String reportName = "TEST-jasmine-it-" + library + ".xml";
            try {
                getLog().info("Running integration tests on minified version");
                TestMojo testMojo = new TestMojo();
                JasmineUtils.prepareJasmineMojo(this, testMojo, null);
                JasmineUtils.extendJasmineMojoForIT(this, testMojo, reportName);
                JasmineUtils.configureJasmineToRunOnLibrary(testMojo, library);
                testMojo.execute();
            } finally {
                File report = new File(JasmineUtils.getJasmineITDirectory(project), reportName);
                JasmineUtils.copyJunitReport(this, report, "integration-test.jasmine.minified");
            }
        }

    }

}
