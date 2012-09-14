package org.nano.coffee.mill.processors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.nano.coffee.mill.mojos.AbstractCoffeeMillMojo;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Map;

/**
 * A processor copying assets to the work directory.
 */
public class CopyAssetProcessor extends DefaultProcessor {


    private File assetsDir;
    private File workDir;

    private static  FileFilter defaultExcludeFilter = new NotFileFilter(
        new IOFileFilter() {
            public boolean accept(File file) {
                return accept(file.getParentFile(), file.getName());
            }

            public boolean accept(File dir, String name) {
                File file = new File(dir, name);
                return
                        name.endsWith("~") // **/*~
                                || name.startsWith("#")  && name.endsWith("#") // **/#*#
                                || name.startsWith(".#") // **/.#
                                || name.startsWith("%")  && name.endsWith("%") // **/%*%
                                || name.startsWith("._") // **/._*

                                || name.equals("CVS")  && file.isDirectory() // CVS dir
                                || file.getAbsolutePath().contains("/CVS/") // Any CVS files
                                || name.equals(".cvsignore")

                                || name.equals("RCS")  && file.isDirectory() // RCS dir
                                || file.getAbsolutePath().contains("/RCS/") // Any RCS files

                                || name.equals("SCCS")  && file.isDirectory() // SCCS dir
                                || file.getAbsolutePath().contains("/SCCS/") // Any SCCS files

                                || name.equals("vssver.scc")  // VSServer

                                || name.equals("project.pj")  // MKS

                                || name.equals(".svn")  && file.isDirectory() // SVN dir
                                || file.getAbsolutePath().contains("/.svn/") // Any SVN files

                                || name.equals(".bzr")  && file.isDirectory() // Bazaar dir
                                || file.getAbsolutePath().contains("/.bzr/") // Any Bazaar files

                                || name.equals(".arch-ids")  && file.isDirectory() // GNU
                                || file.getAbsolutePath().contains("/.arch-ids/") // GNU

                                || name.equals(".DS_Store") // Mac

                                || name.equals(".hg")  && file.isDirectory() // Mercurial
                                || file.getAbsolutePath().contains("/.hg/") // Any Mercurial files

                                || name.equals(".git")  && file.isDirectory() // Git
                                || name.equals(".gitignore")
                                || name.equals(".gitattributes")
                                || file.getAbsolutePath().contains("/.git/")

                                || name.equals("BitKeeper")  && file.isDirectory() // BitKeeper
                                || file.getAbsolutePath().contains("/BitKeeper/")
                                || name.equals("ChangeSet")  && file.isDirectory()
                                || file.getAbsolutePath().contains("/ChangeSet/");
            }
        });


    public void configure(AbstractCoffeeMillMojo millMojo, Map<String, Object> options) {
        super.configure(millMojo, options);
        this.assetsDir = millMojo.assetsDir;
        this.workDir = millMojo.getWorkDirectory();
    }

    public void processAll() throws ProcessorException {
        if (! assetsDir.exists()) {
            return;
        }

        try {
            getLog().info("Copying " + assetsDir.getAbsolutePath() + " to " + workDir.getAbsolutePath());
            FileUtils.copyDirectory(assetsDir, workDir, defaultExcludeFilter, true);
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
