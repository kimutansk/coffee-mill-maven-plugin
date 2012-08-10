package org.nano.coffee.roasting.mojos.test;

import com.github.searls.jasmine.*;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.FileUtils;
import org.nano.coffee.roasting.InjectionHelper;
import org.nano.coffee.roasting.mojos.AbstractRoastingCoffeeMojo;
import org.nano.coffee.roasting.processors.CSSAggregator;

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
            FileUtils.copyDirectory(getWorkDirectory(), getJasmineSourceDirectory());
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

        TestMojo testMojo = new TestMojo();
        populateJasmineMojo(testMojo);
        testMojo.execute();
    }

    private File getJasmineDirectory() {
        return new File(project.getBuild().getDirectory(), "jasmine");
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
                new File(project.getBuild().getDirectory(), "jasmine"));
        InjectionHelper.inject(mojo, AbstractJasmineMojo.class, "specDirectoryName",
                "spec");
        InjectionHelper.inject(mojo, AbstractJasmineMojo.class, "srcDirectoryName",
                "src");
        InjectionHelper.inject(mojo, AbstractJasmineMojo.class, "manualSpecRunnerHtmlFileName",
                "ManualSpecRunner.html");
        InjectionHelper.inject(mojo, AbstractJasmineMojo.class, "specRunnerHtmlFileName",
                "SpecRunner.html");
        InjectionHelper.inject(mojo, AbstractJasmineMojo.class, "junitXmlReportFileName",
                "TEST-jasmine.xml");
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
                        FileUtils.copyFile(file, new File(project.getBasedir(), "target/jasmine/" + filename));
                    } catch (IOException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                    deps.add(filename);
                }
            }
        }
        InjectionHelper.inject(mojo, AbstractJasmineMojo.class, "preloadSources",
                deps);
    }
}
