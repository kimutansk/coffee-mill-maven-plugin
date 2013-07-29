package org.nanoko.coffee.mill.utils.node;

import com.google.common.io.Files;
import com.jcraft.jsch.Logger;
import org.apache.commons.io.FileUtils;
import org.codehaus.plexus.archiver.tar.TarGZipUnArchiver;
import org.codehaus.plexus.archiver.zip.ZipUnArchiver;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.nanoko.coffee.mill.mojos.AbstractCoffeeMillMojo;
import org.nanoko.coffee.mill.utils.ExecUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * A class managing node and npm
 */
public class NodeManager {

    public static final String NODE_DIST = "http://nodejs.org/dist/v";
    public static final String NPM_DIST = "http://nodejs.org/dist/npm/npm-";
    private final AbstractCoffeeMillMojo mojo;
    private final File nodeDirectory;
    private final File npmDirectory;
    private final File nodePrefix;
    private final File nodeModulesDirectory;
    private final File nodeLibDirectory;
    private File nodeExecutable;

    public NodeManager(AbstractCoffeeMillMojo mojo) {
        this.nodeDirectory = new File(System.getProperty("user.home"), ".nanoko/node/" + mojo.getNodeVersion());
        this.npmDirectory = new File(nodeDirectory, "lib/node_modules/npm/");

        if (!nodeDirectory.exists()) {
            nodeDirectory.mkdirs();
        }

        this.mojo = mojo;
        if (ExecUtils.isWindows()) {
            this.nodeExecutable = new File(nodeDirectory + "/bin", "node.exe");
            this.nodePrefix = nodeExecutable.getParentFile();
            nodeLibDirectory = nodePrefix;
            nodeLibDirectory.mkdirs();
        } else {
            this.nodeExecutable = new File(nodeDirectory + "/bin", "node");
            this.nodePrefix = nodeExecutable.getParentFile().getParentFile();
            nodeLibDirectory = new File(nodePrefix, "lib");
            nodeLibDirectory.mkdirs();
        }

        nodeModulesDirectory = new File(nodeLibDirectory, "node_modules");


    }

    /**
     * Installs node in ~/.nanoko/node/$version.
     * The installation process is the following:
     * <ol>
     * <li>download node</li>
     * <li>expand node to the right location</li>
     * <li>download npm</li>
     * <li>expand npm</li>
     * </ol>
     * <p/>
     * Node and npm installation are divided to avoid facing npm corruption on node package.
     *
     * @throws IOException
     */
    public void installIfNotInstalled() throws IOException {
        if (!nodeExecutable.isFile()) {
            downloadAndInstallNode();
            downloadAndInstallNPM();
        } else {
            mojo.getLog().debug("Node executable : " + nodeExecutable.getAbsolutePath());
        }
    }

    public File getNodeExecutable() {
        return nodeExecutable;
    }

    public File getNodeModulesDirectory() {
        return nodeModulesDirectory;
    }

    private void downloadAndInstallNPM() throws IOException {
        URL url = new URL(NPM_DIST + mojo.getNPMVersion() + ".zip");
        File tmp = File.createTempFile("npm", ".zip");

        mojo.getLog().info("Downloading npm-" + mojo.getNPMVersion() + " from " + url.toExternalForm());
        FileUtils.copyURLToFile(url, tmp);
        mojo.getLog().info("npm downloaded - " + tmp.length() + " bytes");

        final ZipUnArchiver ua = new ZipUnArchiver();
        ua.enableLogging(new ConsoleLogger(Logger.INFO, "Unarchiver"));
        ua.setOverwrite(true);
        ua.setSourceFile(tmp);
        ua.setDestDirectory(nodeLibDirectory);
        mojo.getLog().info("Unzipping npm");
        try {
            ua.extract();
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    private void downloadAndInstallNode() throws IOException {
        URL url;
        String path;
        if (ExecUtils.isWindows()) {
            if (ExecUtils.is64bit()) {
                url = new URL(NODE_DIST + mojo.getNodeVersion() + "/x64/node.exe");
            } else {
                url = new URL(NODE_DIST + mojo.getNodeVersion() + "/node.exe");
            }
            // Manage download for windows.
            mojo.getLog().info("Downloading nodejs from " + url.toExternalForm());

            // Create the bin directory
            File bin = new File(nodeDirectory, "bin");
            bin.mkdirs();

            FileUtils.copyURLToFile(url, nodeExecutable);
            mojo.getLog().info(nodeExecutable.getAbsolutePath() + " was downloaded from " + url.toExternalForm());
            // Try to set the file executable.
            nodeExecutable.setExecutable(true);

            return;
        } else if (ExecUtils.isMac()) {
            if (!ExecUtils.is64bit()) {
                path = "node-v" + mojo.getNodeVersion() + "-darwin-x86";
                url = new URL(NODE_DIST + mojo.getNodeVersion() + "/node-v" + mojo.getNodeVersion() + "-darwin-x86" +
                        ".tar.gz");
            } else {
                path = "node-v" + mojo.getNodeVersion() + "-darwin-x64";
                url = new URL(NODE_DIST + mojo.getNodeVersion() + "/node-v" + mojo.getNodeVersion() + "-darwin-x64" +
                        ".tar.gz");
            }
        } else if (ExecUtils.isLinux()) {
            if (!ExecUtils.is64bit()) {
                path = "node-v" + mojo.getNodeVersion() + "-linux-x86";
                url = new URL(NODE_DIST + mojo.getNodeVersion() + "/node-v" + mojo.getNodeVersion() + "-linux-x86.tar.gz");
            } else {
                path = "node-v" + mojo.getNodeVersion() + "-linux-x64";
                url = new URL(NODE_DIST + mojo.getNodeVersion() + "/node-v" + mojo.getNodeVersion() + "-linux-x64.tar.gz");
            }
        } else {
            throw new UnsupportedOperationException("Operating system `" + System.getProperty("os.name") + "` not " +
                    "supported");
        }

        File tmp = File.createTempFile("nodejs", ".tar.gz");
        mojo.getLog().info("Downloading nodejs-" + mojo.getNodeVersion() + " from " + url.toExternalForm());
        FileUtils.copyURLToFile(url, tmp);
        mojo.getLog().info("nodejs downloaded - " + tmp.length() + " bytes");

        File tmpDir = Files.createTempDir();
        tmpDir.mkdirs();

        final TarGZipUnArchiver ua = new TarGZipUnArchiver();
        ua.enableLogging(new ConsoleLogger(Logger.INFO, "Unarchiver"));
        ua.setOverwrite(true);
        ua.setSourceFile(tmp);
        ua.setDestDirectory(tmpDir);
        mojo.getLog().info("Expanding nodejs");
        try {
            ua.extract();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        // Move files
        File test = new File(tmpDir, path);
        if (!test.isDirectory()) {
            throw new IllegalStateException("Cannot find expanded directory " + test.getAbsolutePath());
        }

        FileUtils.copyDirectory(test, nodeDirectory);

        // Check node executable
        if (!nodeExecutable.isFile()) {
            throw new IllegalStateException("Node executable not found after installation");
        } else {
            nodeExecutable.setExecutable(true);
        }

        // Delete the installed npm if any
        if (npmDirectory.isDirectory()) {
            FileUtils.deleteDirectory(npmDirectory);
        }

    }

}
