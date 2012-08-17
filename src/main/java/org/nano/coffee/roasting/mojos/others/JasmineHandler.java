package org.nano.coffee.roasting.mojos.others;

import com.github.searls.jasmine.AbstractJasmineMojo;
import com.github.searls.jasmine.TestMojo;
import org.apache.maven.model.Dependency;
import org.codehaus.plexus.util.FileUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.nano.coffee.roasting.InjectionHelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: clement
 * Date: 16/08/12
 * Time: 12:57
 * To change this template use File | Settings | File Templates.
 */
public class JasmineHandler extends ResourceHandler {
    private String prefix;

    AbstractJasmineMojo jasmine;
    private JasmineRunnerGenerator createsManualRunner;
    private WatchMojo watchMojo;

    public JasmineHandler(WatchMojo mojo) {
        this.watchMojo = mojo;
        jasmine = new TestMojo();
        populateJasmineMojo(jasmine);
        createsManualRunner = new JasmineRunnerGenerator(mojo, jasmine);
    }

    private void createManualSpecRunnerIfNecessary(String target) throws IOException {
        if ("/jasmine".equals(target)) {
            createsManualRunner.create();
        }
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        createManualSpecRunnerIfNecessary(target);
        if ("/jasmine".equals(target)) {
            response.setContentType("text/html;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);
            baseRequest.setHandled(true);
            response.getWriter().println(createsManualRunner.getHtml());
        } else {
            super.handle(target, baseRequest, request, response);
        }

    }

    private void populateJasmineMojo(AbstractJasmineMojo mojo) {
        mojo.setLog(watchMojo.getLog());
        InjectionHelper.inject(mojo, AbstractJasmineMojo.class, "jsSrcDir",
                new File(watchMojo.project.getBasedir(), "src/main/coffee")); //TODO This should be configurable.
        InjectionHelper.inject(mojo, AbstractJasmineMojo.class, "jsTestSrcDir",
                new File(watchMojo.project.getBasedir(), "src/test/js")); //TODO This should be configurable.
        InjectionHelper.inject(mojo, AbstractJasmineMojo.class, "webDriverClassName",
                "org.openqa.selenium.htmlunit.HtmlUnitDriver"); //TODO This should be configurable.
        InjectionHelper.inject(mojo, AbstractJasmineMojo.class, "browserVersion",
                "FIREFOX_3"); //TODO This should be configurable.
        InjectionHelper.inject(mojo, AbstractJasmineMojo.class, "format",
                "documentation"); //TODO This should be configurable.
        InjectionHelper.inject(mojo, AbstractJasmineMojo.class, "jasmineTargetDir",
                new File(watchMojo.project.getBuild().getDirectory(), "it-jasmine"));
        InjectionHelper.inject(mojo, AbstractJasmineMojo.class, "specDirectoryName",
                "spec");
        InjectionHelper.inject(mojo, AbstractJasmineMojo.class, "srcDirectoryName",
                "src");
        InjectionHelper.inject(mojo, AbstractJasmineMojo.class, "manualSpecRunnerHtmlFileName",
                "ManualSpecRunner.html");
        InjectionHelper.inject(mojo, AbstractJasmineMojo.class, "specRunnerHtmlFileName",
                "SpecRunner.html");
        InjectionHelper.inject(mojo, AbstractJasmineMojo.class, "mavenProject",
                watchMojo.project);
        InjectionHelper.inject(mojo, AbstractJasmineMojo.class, "specRunnerTemplate",
                "DEFAULT");
        InjectionHelper.inject(mojo, AbstractJasmineMojo.class, "haltOnFailure",
                true);

        List<String> deps = new ArrayList<String>();
        for (Dependency dep : (Collection<Dependency>) watchMojo.project.getDependencies()) {
            if ("js".equals(dep.getType())) {
                String filename = dep.getArtifactId() + "-" + dep.getVersion() + ".js";
                if (dep.getClassifier() != null) {
                    filename = dep.getArtifactId() + "-" + dep.getVersion() + "-" + dep.getClassifier() + ".js";
                }
                File file = new File(watchMojo.project.getBasedir(), "target/web/" + filename);

                if (! file.exists()) {
                    watchMojo.getLog().error("Cannot preload " + dep.getArtifactId() + ":" + dep.getVersion() + " : "
                            + file
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

    private File getJasmineDirectory() {
        return new File(watchMojo.project.getBuild().getDirectory(), "jasmine");
    }
}
