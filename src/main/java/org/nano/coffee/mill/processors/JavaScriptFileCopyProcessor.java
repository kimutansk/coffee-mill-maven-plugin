package org.nano.coffee.mill.processors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.nano.coffee.mill.mojos.AbstractCoffeeMillMojo;
import org.nano.coffee.mill.utils.OptionsHelper;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Just copy JavaScript files to an output directory.
 */
public class JavaScriptFileCopyProcessor extends DefaultProcessor {


    private File source;
    private File destination;

    @Override
    public void configure(AbstractCoffeeMillMojo millMojo, Map<String, Object> options) {
        super.configure(millMojo, options);
        if (OptionsHelper.getBoolean(options, "test", false)) {
            this.source = millMojo.javaScriptTestDir;
            this.destination = millMojo.getWorkTestDirectory();
        } else {
            this.source = millMojo.javaScriptDir;
            this.destination = millMojo.getWorkDirectory();
        }
    }

    public void processAll() throws ProcessorException {
        if (source.exists()) {
            copyJavascriptFiles();
        }
    }

    private void copyJavascriptFiles() throws ProcessorException {
        getLog().info("Copying " + source.getAbsolutePath() + " to " + destination.getAbsolutePath());
        // Create a filter for ".js" files
        IOFileFilter jsSuffixFilter = FileFilterUtils.suffixFileFilter(".js");
        IOFileFilter jsFiles = FileFilterUtils.and(FileFileFilter.FILE, jsSuffixFilter);

        // Create a filter for either directories or ".js" files
        IOFileFilter filter = FileFilterUtils.or(DirectoryFileFilter.DIRECTORY, jsFiles);

        // Copy using the filter
        try {
            FileUtils.copyDirectory(source, destination, filter);
        } catch (IOException e) {
            throw new ProcessorException("Cannot copy JavaScript files", e);
        }
    }

    public void tearDown() {
        // Do nothing.
    }

    public boolean accept(File file) {
        return isFileContainedInDirectory(file, source)  && file.isFile()  &&  file.getName().endsWith(".js");
    }

    public void fileCreated(File file) throws ProcessorException {
        getLog().info("Copying " + file.getAbsolutePath() + " to " + destination.getAbsolutePath());
        copyFileToDir(file, source, destination);
    }

    public void fileUpdated(File file) throws ProcessorException {
        getLog().info("Copying " + file.getAbsolutePath() + " to " + destination.getAbsolutePath());
        copyFileToDir(file, source, destination);
    }

    public void fileDeleted(File file) {
        File rel = computeRelativeFile(file, source, destination);
        if (rel.isFile()) {
            getLog().info("Deleting " + rel.getAbsolutePath());
            rel.delete();
        }
    }

}
