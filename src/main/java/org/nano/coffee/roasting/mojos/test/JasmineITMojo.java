package org.nano.coffee.roasting.mojos.test;

import com.github.searls.jasmine.AbstractJasmineMojo;
import com.github.searls.jasmine.ProcessTestResourcesMojo;
import com.github.searls.jasmine.TestMojo;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.FileUtils;
import org.nano.coffee.roasting.InjectionHelper;
import org.nano.coffee.roasting.mojos.AbstractRoastingCoffeeMojo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @goal jasmine-it-test
 */
public class JasmineITMojo extends AbstractRoastingCoffeeMojo {

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

    /**
     * The list in order of the javascript file to include.
     * This list is shared with the javascript aggregation.
     * @parameter
     */
    protected List<String> javascriptAggregation;


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
                File lib = new File(project.getBuild().getDirectory(), project.getBuild().getFinalName() + ".js");
                FileUtils.copyFileToDirectory(lib, getJasmineSourceDirectory());
            }
            if (runJasmineTestOnMinifiedVersion) {
                File lib = new File(project.getBuild().getDirectory(), project.getBuild().getFinalName() + "-min.js");
                FileUtils.copyFileToDirectory(lib, getJasmineSourceDirectory());
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Cannot prepare Jasmine execution", e);
        }

        // Copy target/work-test to target/jasmine/spec
        try {
            FileUtils.copyDirectory(getWorkTestDirectory(), getJasmineSpecDirectory());
        } catch (IOException e) {
            throw new MojoExecutionException("Cannot prepare Jasmine execution", e);
        }

        ProcessTestResourcesMojo processTestResourcesMojo = new ProcessTestResourcesMojo();
        populateJasmineMojo(processTestResourcesMojo);
        processTestResourcesMojo.execute();

        if (runJasmineTestOnAggregatedVersion) {
            String library = project.getBuild().getFinalName() + ".js";
            String reportName = "TEST-jasmine-it-" + library + ".xml";
            try {
                getLog().info("Running integration tests on aggregated version");
                TestMojo testMojo = new TestMojo();
                populateJasmineMojo(testMojo);
                configureMojoToRunOnLibrary(testMojo, library);
                testMojo.execute();
            } finally {
                // Copy the resulting junit report if exit
                File report = new File(getJasmineDirectory(), reportName);
                if (report.isFile()) {
                    try {
                        String reportContent = org.apache.commons.io.FileUtils.readFileToString(report);
                        reportContent = reportContent.replace("classname=\"jasmine\"",
                                "classname=\"integration-test.jasmine.aggregated\"");
                        File surefire = new File(project.getBuild().getDirectory(), "surefire-reports");
                        surefire.mkdirs();
                        File newReport = new File(surefire, reportName);
                        org.apache.commons.io.FileUtils.write(newReport, reportContent);
                    } catch (IOException e) {
                        getLog().error("Cannot write the surefire report", e);
                    }
                }
            }
        }

        if (runJasmineTestOnMinifiedVersion) {
            String library = project.getBuild().getFinalName() + "-min.js";
            String reportName = "TEST-jasmine-it-" + library + ".xml";
            try {
                getLog().info("Running integration tests on minified version");
                TestMojo testMojo = new TestMojo();
                populateJasmineMojo(testMojo);
                configureMojoToRunOnLibrary(testMojo, library);
                testMojo.execute();
            } finally {
                // Copy the resulting junit report if exit
                File report = new File(getJasmineDirectory(), reportName);
                if (report.isFile()) {
                    try {
                        String reportContent = org.apache.commons.io.FileUtils.readFileToString(report);
                        reportContent = reportContent.replace("classname=\"jasmine\"",
                                "classname=\"integration-test.jasmine.minified\"");
                        File surefire = new File(project.getBuild().getDirectory(), "surefire-reports");
                        surefire.mkdirs();
                        File newReport = new File(surefire, reportName);
                        org.apache.commons.io.FileUtils.write(newReport, reportContent);
                    } catch (IOException e) {
                        getLog().error("Cannot write the surefire report", e);
                    }
                }
            }
        }

    }

    private File getJasmineDirectory() {
        return new File(project.getBuild().getDirectory(), "it-jasmine");
    }

    private File getJasmineSourceDirectory() {
        return new File(getJasmineDirectory(), "src");
    }

    private File getJasmineSpecDirectory() {
        return new File(getJasmineDirectory(), "spec");
    }



    private void populateJasmineMojo(AbstractJasmineMojo mojo) {
        mojo.setLog(getLog());
        InjectionHelper.inject(mojo, AbstractJasmineMojo.class, "jsSrcDir",
                new File(project.getBasedir(), "src/main/coffee")); //TODO This should be configurable.
        InjectionHelper.inject(mojo, AbstractJasmineMojo.class, "jsTestSrcDir",
                new File(project.getBasedir(), "src/test/js")); //TODO This should be configurable.
        InjectionHelper.inject(mojo, AbstractJasmineMojo.class, "webDriverClassName",
                "org.openqa.selenium.htmlunit.HtmlUnitDriver"); //TODO This should be configurable.
        InjectionHelper.inject(mojo, AbstractJasmineMojo.class, "browserVersion",
                "FIREFOX_3"); //TODO This should be configurable.
        InjectionHelper.inject(mojo, AbstractJasmineMojo.class, "format",
                "documentation"); //TODO This should be configurable.
        InjectionHelper.inject(mojo, AbstractJasmineMojo.class, "jasmineTargetDir",
                new File(project.getBuild().getDirectory(), "it-jasmine"));
        InjectionHelper.inject(mojo, AbstractJasmineMojo.class, "specDirectoryName",
                "spec");
        InjectionHelper.inject(mojo, AbstractJasmineMojo.class, "srcDirectoryName",
                "src");
        InjectionHelper.inject(mojo, AbstractJasmineMojo.class, "manualSpecRunnerHtmlFileName",
                "ManualSpecRunner.html");
        InjectionHelper.inject(mojo, AbstractJasmineMojo.class, "specRunnerHtmlFileName",
                "SpecRunner.html");
        InjectionHelper.inject(mojo, AbstractJasmineMojo.class, "mavenProject",
                project);
        InjectionHelper.inject(mojo, AbstractJasmineMojo.class, "specRunnerTemplate",
                "DEFAULT");
        InjectionHelper.inject(mojo, AbstractJasmineMojo.class, "haltOnFailure",
                true);

        List<String> deps = new ArrayList<String>();
        for (Dependency dep : (Collection<Dependency>) project.getDependencies()) {
            if ("js".equals(dep.getType())) {
                String filename = dep.getArtifactId() + "-" + dep.getVersion() + ".js";
                if (dep.getClassifier() != null) {
                    filename = dep.getArtifactId() + "-" + dep.getVersion() + "-" + dep.getClassifier() + ".js";
                }
                File file = new File(project.getBasedir(), "target/web/" + filename);

                if (! file.exists()) {
                    getLog().error("Cannot preload " + dep.getArtifactId() + ":" + dep.getVersion() + " : " + file
                            .getAbsolutePath() + " not found");
                } else {
                    try {
                        FileUtils.copyFileToDirectory(file, getJasmineDirectory());
                    } catch (IOException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                    deps.add(filename);
                }
            }
        }
        InjectionHelper.inject(mojo, AbstractJasmineMojo.class, "preloadSources",
                deps);

        // TODO Parameter.
        InjectionHelper.inject(mojo, AbstractJasmineMojo.class, "timeout",
                300);
    }

    private void configureMojoToRunOnLibrary(AbstractJasmineMojo mojo, String library) {
        if (library != null) {
            List<String> list = new ArrayList<String>();
            list.add(library);
            InjectionHelper.inject(mojo, AbstractJasmineMojo.class, "sourceIncludes",
                    list);
            InjectionHelper.inject(mojo, AbstractJasmineMojo.class, "junitXmlReportFileName",
                    "TEST-jasmine-it-" + library + ".xml");
        }
    }
}
