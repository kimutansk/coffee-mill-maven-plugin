package org.nano.coffee.mill.mojos.others;

import org.apache.commons.io.FileUtils;
import org.nano.coffee.mill.mojos.AbstractCoffeeMillMojo;
import org.nano.coffee.mill.processors.DefaultProcessor;
import org.nano.coffee.mill.processors.Processor;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * A processor copying final artifacts from reactor projects to the watched project.
 */
public class FinalArtifactProcessor extends DefaultProcessor {
    private WatchMojo watchedProjectMojo;
    private WatchMojo subProjectMojo;

    public FinalArtifactProcessor(WatchMojo watchedProject, WatchMojo subProject) {
        this.watchedProjectMojo = watchedProject;
        this.subProjectMojo = subProject;
    }

    public boolean accept(File file) {
        return
                isFileContainedInDirectory(file, subProjectMojo.getTarget())
                && file.getName().contains(subProjectMojo.project.getBuild().getFinalName());
    }

    public void fileCreated(File file) throws ProcessorException {
        if (file.getName().endsWith(".js")) {
            try {
                File output = new File(watchedProjectMojo.getLibDirectory(), subProjectMojo.project.getArtifactId()
                        + ".js");
                getLog().info("Copying " + file.getAbsolutePath() + " to " + output.getAbsolutePath());
                FileUtils.copyFile(file, output);
                return;
            } catch (IOException e) {
                throw new ProcessorException("Can't copy " + file.getAbsolutePath() + " to " + watchedProjectMojo
                        .getLibDirectory().getAbsolutePath(), e);
            }
        }

        if (file.getName().endsWith(".css")) {
            try {
                File output = new File(watchedProjectMojo.getWorkDirectory(), subProjectMojo.project.getArtifactId() + ".css");
                getLog().info("Copying " + file.getAbsolutePath() + " to " + output.getAbsolutePath());
                FileUtils.copyFileToDirectory(file, watchedProjectMojo.getWorkDirectory());
                return;
            } catch (IOException e) {
                throw new ProcessorException("Can't copy " + file.getAbsolutePath() + " to " + watchedProjectMojo
                        .getWorkDirectory().getAbsolutePath(), e);
            }
        }
    }

    public void fileUpdated(File file) throws ProcessorException {
        fileCreated(file);
    }

    public void fileDeleted(File file) throws ProcessorException {
        if (file.getName().endsWith("js")) {
            FileUtils.deleteQuietly(new File(watchedProjectMojo.getLibDirectory(), file.getName()));
            return;
        }
        if (file.getName().endsWith("css")) {
            FileUtils.deleteQuietly(new File(watchedProjectMojo.getWorkDirectory(), file.getName()));
            return;
        }
    }
}
