package org.nano.coffee.roasting.processors;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.logging.Log;
import org.nano.coffee.roasting.mojos.AbstractRoastingCoffeeMojo;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Default Implementation of Processor
 */
public class DefaultProcessor implements Processor {

    protected AbstractRoastingCoffeeMojo mojo;
    protected Map<String, Object> options;

    public void configure(AbstractRoastingCoffeeMojo mojo, Map<String, Object> options) {
        this.mojo = mojo;
        if (options == null) {
            this.options = new HashMap<String, Object>();
        } else {
            this.options = options;
        }
    }

    public void processAll() throws ProcessorException {
        // Do nothing by default
        mojo.getLog().info("Processing triggered on " + this.getClass().getName() + " - do nothing by default");
    }

    public void tearDown() {
        this.mojo = null;
    }

    public boolean accept(File file) {
        return false;
    }

    public void fileCreated(File file) throws ProcessorException {
        // Do nothing
    }

    public void fileUpdated(File file) throws ProcessorException {
        // Do nothing
    }

    public void fileDeleted(File file) throws ProcessorException {
        // Do nothing
    }

    public Log getLog() {
        return mojo.getLog();
    }

    public static boolean isFileContainedInDirectory(File file, File dir) {
        return file.exists() && file.getAbsolutePath().startsWith(dir.getAbsolutePath());
    }

    /**
     * Copy the file <tt>file</tt> to the directory <tt>dir</tt>, keeping the structure relative to <tt>rel</tt>
     * @throws ProcessorException
     */
    public static void copyFileToDir(File file, File rel, File dir) throws ProcessorException {
        try {
            File out = computeRelativeFile(file, rel, dir);
            if (out.getParentFile() != null) {
                out.getParentFile().mkdirs();
                FileUtils.copyFileToDirectory(file, out.getParentFile());
            } else {
                throw new ProcessorException("Cannot copy file - parent directory not accessible for "
                        + file.getAbsolutePath());
            }
        } catch (IOException e) {
            throw new ProcessorException("Cannot copy file " + file.getName(), e);
        }
    }

    /**
     * Gets a File object representing a File in the directory <tt>dir</tt> which has the same path as the file
     * <tt>file</tt> from the directory <tt>rel</tt>.
     * @param file
     * @param rel
     * @param dir
     * @return
     */
    public static File computeRelativeFile(File file, File rel, File dir) {
        String path = file.getAbsolutePath();
        String relativePath = path.substring(rel.getAbsolutePath().length());
        return new File(dir, relativePath);
    }
}
