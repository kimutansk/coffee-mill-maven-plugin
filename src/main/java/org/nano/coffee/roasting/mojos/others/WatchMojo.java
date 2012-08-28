package org.nano.coffee.roasting.mojos.others;

import org.apache.commons.vfs2.*;
import org.apache.commons.vfs2.impl.DefaultFileMonitor;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.nano.coffee.roasting.mojos.AbstractRoastingCoffeeMojo;
import org.nano.coffee.roasting.processors.*;
import org.nano.coffee.roasting.utils.OptionsHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This mojo watches the file change in the source directories and process them automatically.
 * To work correctly, launch <tt>mvn clean test</tt> first. This will resolve and prepare all required file.
 * Then <tt>mvn org.nano.coffee-roasting:coffee-roasting-maven-plugin:watch</tt> will starts the <i>watch</i> mode.
 * @goal watch
 */
public class WatchMojo extends AbstractRoastingCoffeeMojo implements FileListener {

    /**
     * @parameter default-value="true"
     */
    protected boolean watchCoffeeScript;

    /**
     * @parameter default-value="true"
     */
    protected boolean watchLess;

    /**
     * @parameter default-value="true"
     */
    protected boolean watchDoAggregate;

    /**
     * @parameter default-value="true"
     */
    protected boolean watchValidateJS;

    /**
     * @parameter default-value="true"
     */
    protected boolean watchValidateCSS;

    /**
     * @parameter default-value="true"
     */
    protected boolean watchRunServer;

    /**
     * @parameter default-value="8234"
     */
    protected int watchJettyServerPort;

    /**
     * @parameter
     */
    List<String> javascriptAggregation;

    /**
     * @parameter
     */
    protected List<String> cssAggregation;

    /**
     * The Jetty Server
     */
    protected Server server;
    /**
     * The processors
     */
    protected List<Processor> processors;


    public void execute() throws MojoExecutionException, MojoFailureException {

        computeProcessors();
        try {
            setupMonitor();
        } catch (FileSystemException e) {
            throw new MojoExecutionException("Cannot set the file monitor on the source folder", e);
        }

        String MESSAGE = "You're running the watch mode. All modified files will be processed " +
                "automatically. \n" +
                "If the jetty server is enabled, they will also be served from http://localhost:" +
                watchJettyServerPort + "/. \n" +
                "The jasmine runner is available from http://localhost:" + watchJettyServerPort + "/jasmine. \n" +
                "To leave the watch mode, just hit CTRL+C.\n";
        getLog().info(MESSAGE);

        for (Processor processor : processors) {
            try {
                processor.processAll();
            } catch (Processor.ProcessorException e) {
                getLog().error("", e);
            }
        }

        if (watchRunServer) {
            try {
                server = new Server();
                addConnectorToServer();
                addHandlersToServer();
                startServer();
            } catch (Exception e){
                throw new MojoExecutionException("Cannot run the jetty server", e);
            }
        } else {
            try {
                Thread.sleep(1000000000); // Pretty long
            } catch (InterruptedException e) { /* ignore */ }
        }
    }

    private List<Processor> computeProcessors() {
        processors = new ArrayList<Processor>();
        // Always added

        // Asset Copy
        Processor processor = new CopyAssetProcessor();
        processor.configure(this, null);
        processors.add(processor);

        // Copy JS Main + Test
        processor = new JavaScriptFileCopyProcessor();
        processor.configure(this, new OptionsHelper.OptionsBuilder().set("test", false).build());
        processors.add(processor);
        processor = new JavaScriptFileCopyProcessor();
        processor.configure(this, new OptionsHelper.OptionsBuilder().set("test", true).build());
        processors.add(processor);

        // Copy CSS
        processor = new CSSFileCopyProcessor();
        processor.configure(this, null);
        processors.add(processor);

        // Less
        if (watchLess) {
            processor = new LessCompilationProcessor();
            processor.configure(this, null);
            processors.add(processor);
        }

        // CoffeeScript
        if (watchCoffeeScript) {
            processor = new CoffeeScriptCompilationProcessor();
            processor.configure(this, new OptionsHelper.OptionsBuilder().set("test", false).build());
            processors.add(processor);

            processor = new CoffeeScriptCompilationProcessor();
            processor.configure(this, new OptionsHelper.OptionsBuilder().set("test", true).build());
            processors.add(processor);
        }

        // JS and CSS Aggregation
        if (watchDoAggregate) {
            processor = new JavaScriptAggregator();
            Map<String, Object> options = new HashMap<String, Object>();
            File output = new File(getWorkDirectory(), project.getBuild().getFinalName() + ".js");
            options.put("output", output);
            options.put("names", javascriptAggregation);
            options.put("extension", "js");
            processor.configure(this, options);
            processors.add(processor);

            processor = new CSSAggregator();
            output = new File(getWorkDirectory(), project.getBuild().getFinalName() + ".css");
            options = new HashMap<String, Object>();
            options.put("output", output);
            options.put("names", cssAggregation);
            options.put("extension", "css");
            processor.configure(this, options);
            processors.add(processor);
        }

        // CSSLint, JSLint and JSHint validation
        if (watchValidateJS) {
            processor = new JSHintProcessor();
            processor.configure(this, null);
            processors.add(processor);

            processor = new JSHintProcessor();
            processor.configure(this, null);
            processors.add(processor);
        }
        if (watchValidateCSS) {
            processor = new CSSLintProcessor();
            processor.configure(this, new OptionsHelper.OptionsBuilder().set("directory", getWorkDirectory()).build());
            processors.add(processor);
        }

        return processors;
    }


    private void setupMonitor() throws FileSystemException {
        File baseDir = project.getBasedir();
        getLog().info("Set up file monitor on " + baseDir);
        FileSystemManager fsManager = VFS.getManager();
        FileObject dir = fsManager.resolveFile(baseDir.getAbsolutePath());

        DefaultFileMonitor fm = new DefaultFileMonitor(this);
        fm.setRecursive(true);
        fm.addFile(dir);
        fm.start();
    }

    private void addConnectorToServer() {
        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(watchJettyServerPort);
        server.addConnector(connector);
    }

    private void addHandlersToServer() {
        HandlerList list = new HandlerList();
        list.addHandler(new DirectoryHandler(getWorkDirectory()));
        list.addHandler(new DirectoryHandler(getLibDirectory()));
        list.addHandler(new DirectoryHandler(getWorkTestDirectory()));
        list.addHandler(new JasmineHandler(this));
        server.setHandler(list);
    }

    private void startServer() throws Exception {
        server.start();
        server.join();
    }

    public void fileCreated(FileChangeEvent event) throws Exception {
        getLog().info("New file found " + event.getFile().getName().getBaseName());
        boolean processed = false;
        String path = event.getFile().getName().getPath();
        File theFile = new File(path);
        for (Processor processor : processors) {
            if (processor.accept(theFile)) {
                processed = true;
                processor.fileCreated(theFile);
            }
        }

        if (! processed) {
            getLog().info("Nothing to do for " + event.getFile().getName().getBaseName());
        }
    }

    public void fileDeleted(FileChangeEvent event) throws Exception {
        getLog().info("File " + event.getFile().getName().getBaseName() + " deleted");
        boolean processed = false;
        String path = event.getFile().getName().getPath();
        File theFile = new File(path);
        for (Processor processor : processors) {
            if (processor.accept(theFile)) {
                processed = true;
                processor.fileDeleted(theFile);
            }
        }

        if (! processed) {
            getLog().info("Nothing to do for " + event.getFile().getName().getBaseName());
        }
    }

    public void fileChanged(FileChangeEvent event) throws Exception {
        getLog().info("File changed: " + event.getFile().getName().getBaseName());
        boolean processed = false;
        String path = event.getFile().getName().getPath();
        File theFile = new File(path);
        for (Processor processor : processors) {
            if (processor.accept(theFile)) {
                processed = true;
                processor.fileUpdated(theFile);
            }
        }

        if (! processed) {
            getLog().info("Nothing to do for " + event.getFile().getName().getBaseName());
        }
    }
}
