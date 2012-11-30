package org.nanoko.coffee.mill.mojos.others;

import com.github.searls.jasmine.AbstractJasmineMojo;
import com.github.searls.jasmine.io.scripts.AbstractScriptResolver;
import com.github.searls.jasmine.runner.ReporterType;
import com.github.searls.jasmine.runner.SpecRunnerHtmlGenerator;
import com.github.searls.jasmine.runner.SpecRunnerHtmlGeneratorFactory;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Generates the JAsmine Runner served by the _watch_ server.
 * This class is pretty close of the Manual Generator Creation of Jasmine but customize the script urls
 */
public class JasmineRunnerGenerator {

    private WatchMojo mojo;

    private Log log;
    private AbstractJasmineMojo jasmineMojo;

    public JasmineRunnerGenerator(WatchMojo mojo, AbstractJasmineMojo jasmineMojo) {
        log = mojo.getLog();
        this.mojo = mojo;
        this.jasmineMojo = jasmineMojo;
    }

    public String getHtml() throws IOException {
        File jasmineDir = new File(mojo.project.getBuild().getDirectory(), "jasmine");
        File runnerDestination = new File(jasmineDir, "watch-jasmine-runner.html");
        if (! runnerDestination.exists()) {
            create();
        }
        return FileUtils.readFileToString(runnerDestination);

    }

    public void create() throws IOException {
        File jasmineDir = new File(mojo.project.getBuild().getDirectory(), "jasmine");
        File runnerDestination = new File(jasmineDir, "watch-jasmine-runner.html");

        AbstractScriptResolver resolver = new WatchScripResolver();
        SpecRunnerHtmlGenerator generator = new SpecRunnerHtmlGeneratorFactory().create(ReporterType.HtmlReporter,
                jasmineMojo, resolver);

        String newRunnerHtml = generator.generate();
        if(newRunnerDiffersFromOldRunner(runnerDestination, newRunnerHtml)) {
            saveRunner(runnerDestination, newRunnerHtml);
        } else {
            log.info("Skipping spec runner generation, because an identical spec runner already exists.");
        }
    }

    private String existingRunner(File destination) throws IOException {
        String existingRunner = null;
        try {
            if(destination.exists()) {
                existingRunner = FileUtils.readFileToString(destination);
            }
        } catch(Exception e) {
            log.warn("An error occurred while trying to open an existing manual spec runner. Continuing.");
        }
        return existingRunner;
    }

    private boolean newRunnerDiffersFromOldRunner(File runnerDestination, String newRunner) throws IOException {
        return !StringUtils.equals(newRunner, existingRunner(runnerDestination));
    }

    private void saveRunner(File runnerDestination, String newRunner) throws IOException {
        FileUtils.writeStringToFile(runnerDestination, newRunner, "UTF-8");
    }

    private class WatchScripResolver extends AbstractScriptResolver {
        @Override
        public Set<String> getAllScripts() {
            Set<String> set = new LinkedHashSet<String>();

            // Preload
            if (jasmineMojo.getPreloadSources() != null) {
                // All dependencies are in the web folder.
                List<String> preloadedNames = jasmineMojo.getPreloadSources();
                for (String s : preloadedNames) {
                    set.add("/" + s);
                }
            }

            // Sources
            if (mojo.javascriptAggregation != null) {
                for (String s : mojo.javascriptAggregation) {
                    set.add("/" + s);
                }
            } else {
                for (File f : FileUtils.listFiles(mojo.getWorkDirectory(), new String[] {"js"}, true)) {
                    String path = f.getAbsolutePath().substring(mojo.getWorkDirectory().getAbsolutePath().length());
                    if (path.startsWith("/")) {
                        set.add(path);
                    } else {
                        set.add("/" + path);
                    }
                }
            }

            // Specs
            for (File f : FileUtils.listFiles(mojo.getWorkTestDirectory(), new String[] {"js"}, true)) {
                String path = f.getAbsolutePath().substring(mojo.getWorkTestDirectory().getAbsolutePath().length());
                if (path.startsWith("/")) {
                    set.add(path);
                } else {
                    set.add("/" + path);
                }
            }

            return set;
        }

        public void resolveScripts() throws IOException { }
    }





}
