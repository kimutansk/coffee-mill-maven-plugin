package org.nano.coffee.roasting.processors;

import org.apache.commons.io.FileUtils;
import org.nano.coffee.roasting.mojos.AbstractRoastingCoffeeMojo;
import org.nano.coffee.roasting.utils.OptionsHelper;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * A processor copying assets to the work directory.
 */
public class CopyAssetProcessor extends DefaultProcessor {


    private File assetsDir;
    private File workDir;

    public void configure(AbstractRoastingCoffeeMojo mojo, Map<String, Object> options) {
        super.configure(mojo, options);
        this.assetsDir = mojo.assetsDir;
        this.workDir = mojo.getWorkDirectory();
    }

    public void processAll() throws ProcessorException {
        if (! assetsDir.exists()) {
            return;
        }

        try {
            getLog().info("Copying " + assetsDir.getAbsolutePath() + " to " + workDir.getAbsolutePath());
            FileUtils.copyDirectory(assetsDir, workDir);
        } catch (IOException e) {
            throw new ProcessorException("Cannot copy assets to the work directory", e);
        }
    }

    public void tearDown() {
        // Do nothing.
    }

    /**
     * Accepts files from the asset folder
     */
    public boolean accept(File file) {
        return isFileContainedInDirectory(file, assetsDir);
    }

    public void fileCreated(File file) throws ProcessorException {
        getLog().info("Copying " + file.getName() + " to " + workDir.getAbsolutePath());
        copyFileToDir(file, assetsDir, workDir);
    }

    public void fileUpdated(File file) throws ProcessorException {
        getLog().info("Copying " + file.getName() + " to " + workDir.getAbsolutePath());
        copyFileToDir(file, assetsDir, workDir);
    }

    public void fileDeleted(File file) {
        File target = computeRelativeFile(file, assetsDir, workDir);
        if (target.isFile()) {
            getLog().info("Deleting " + target.getAbsolutePath());
            target.delete();
        }
    }

}
