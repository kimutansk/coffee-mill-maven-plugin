package org.nanoko.coffee.mill.utils;

import com.github.searls.jasmine.AbstractJasmineMojo;
import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;
import org.nanoko.coffee.mill.InjectionHelper;
import org.nanoko.coffee.mill.mojos.AbstractCoffeeMillMojo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Some helper methods related to Jasmine and the Jasmine Maven Plugin.
 */
public class JasmineUtils {
    public static final String TEST_JASMINE_XML = "TEST-jasmine.xml";

    public static void prepareJasmineMojo(AbstractCoffeeMillMojo mill, AbstractJasmineMojo mojo,
                                          List<String> aggregation) {
        MavenProject project = mill.project;
        mojo.setLog(mill.getLog());
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
                TEST_JASMINE_XML);
        InjectionHelper.inject(mojo, AbstractJasmineMojo.class, "mavenProject",
                project);
        InjectionHelper.inject(mojo, AbstractJasmineMojo.class, "specRunnerTemplate",
                "DEFAULT");
        InjectionHelper.inject(mojo, AbstractJasmineMojo.class, "haltOnFailure",
                true);

        List<String> deps = new ArrayList<String>();
        for (Dependency dep : (Collection<Dependency>) project.getDependencies()) {
            if ("js".equals(dep.getType())) {
                String filename = dep.getArtifactId() + ".js";
                if (dep.getClassifier() != null  && ! dep.getClassifier().equals("min")) {
                    filename = dep.getArtifactId() + "-" + dep.getClassifier() + ".js";
                }
                File file = new File(mill.getLibDirectory(), filename);

                if (! file.exists()) {
                    mill.getLog().error("Cannot preload " + dep.getArtifactId() + ":" + dep.getVersion() + " : " +
                            file
                            .getAbsolutePath() + " not found");
                } else {
                    try {
                        FileUtils.copyFileToDirectory(file, getJasmineDirectory(project));
                    } catch (IOException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                    deps.add(filename);
                }
            }
        }
        InjectionHelper.inject(mojo, AbstractJasmineMojo.class, "preloadSources",
                deps);

        // If javaScriptAggregation is set, use the right order.
        if (aggregation != null) {
            InjectionHelper.inject(mojo, AbstractJasmineMojo.class, "sourceIncludes",
                    aggregation);
        }

        // TODO Parameter.
        InjectionHelper.inject(mojo, AbstractJasmineMojo.class, "timeout",
                300);


    }

    public static void extendJasmineMojoForIT(AbstractCoffeeMillMojo mill, AbstractJasmineMojo mojo,
                                               String reportName) {
        InjectionHelper.inject(mojo, AbstractJasmineMojo.class, "jasmineTargetDir",
                getJasmineITDirectory(mill.project));
        if (reportName != null) {
            InjectionHelper.inject(mojo, AbstractJasmineMojo.class, "junitXmlReportFileName",
                    reportName);
        }

        List<String> deps = new ArrayList<String>();
        for (Dependency dep : (Collection<Dependency>) mill.project.getDependencies()) {
            if ("js".equals(dep.getType())) {
                String filename = dep.getArtifactId() + ".js";
                if (dep.getClassifier() != null  && ! dep.getClassifier().equals("min")) {
                    filename = dep.getArtifactId() + "-" + dep.getClassifier() + ".js";
                }
                File file = new File(mill.getLibDirectory(), filename);

                if (! file.exists()) {
                    mill.getLog().error("Cannot preload " + dep.getArtifactId() + ":" + dep.getVersion() + " : " +
                            file
                                    .getAbsolutePath() + " not found");
                } else {
                    try {
                        FileUtils.copyFileToDirectory(file, getJasmineITDirectory(mill.project));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    deps.add(filename);
                }
            }
        }
    }

    public static void configureJasmineToRunOnLibrary(AbstractJasmineMojo mojo, String library) {
        if (library != null) {
            List<String> list = new ArrayList<String>();
            list.add(library);
            InjectionHelper.inject(mojo, AbstractJasmineMojo.class, "sourceIncludes",
                    list);
        }
    }

    public static File getJasmineDirectory(MavenProject project) {
        return new File(project.getBuild().getDirectory(), "jasmine");
    }

    public static File getJasmineITDirectory(MavenProject project) {
        return new File(project.getBuild().getDirectory(), "it-jasmine");
    }

    public static File getJasmineSourceDirectory(MavenProject project) {
        return new File(getJasmineDirectory(project), "src");
    }

    public static File getJasmineSpecDirectory(MavenProject project) {
        return new File(getJasmineDirectory(project), "spec");
    }

    public static File getJasmineITSourceDirectory(MavenProject project) {
        return new File(getJasmineITDirectory(project), "src");
    }

    public static File getJasmineITSpecDirectory(MavenProject project) {
        return new File(getJasmineITDirectory(project), "spec");
    }

    public static void copyJunitReport(AbstractCoffeeMillMojo millMojo, File report, String classname) {
        // Copy the resulting junit report if exit
        if (report.isFile()) {
            try {
                String reportContent = FileUtils.readFileToString(report);
                if (classname != null) {
                    reportContent = reportContent.replace("classname=\"jasmine\"", "classname=\"" + classname + "\"");
                }
                File surefire = new File(millMojo.project.getBuild().getDirectory(), "surefire-reports");
                surefire.mkdirs();
                File newReport = new File(surefire, report.getName());
                FileUtils.write(newReport, reportContent);
            } catch (IOException e) {
                millMojo.getLog().error("Cannot write the surefire report", e);
            }
        }
    }

}