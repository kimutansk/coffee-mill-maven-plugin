package org.nano.coffee.mill.processors;

import org.apache.commons.io.FileUtils;
import org.mozilla.javascript.RhinoException;
import org.nano.coffee.mill.mojos.AbstractCoffeeMillMojo;
import org.nano.coffee.mill.utils.RhinoLauncher;
import ro.isdc.wro.extensions.processor.support.less.LessCss;
import ro.isdc.wro.extensions.script.RhinoUtils;
import ro.isdc.wro.util.WroUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

/**
 * Processor handling Less to CSS compilation.
 * It handles <tt>.less</tt> files from the <tt>stylesheets</tt> directory.
 */
public class LessCompilationProcessor extends DefaultProcessor {


    private File source;
    private File destination;

    public void tearDown() {
        // Do nothing.
    }

    @Override
    public void configure(AbstractCoffeeMillMojo mojo, Map<String, Object> options) {
        super.configure(mojo, options);
        this.source = mojo.stylesheetsDir;
        this.destination = mojo.getWorkDirectory();
    }

    public boolean accept(File file) {
        return isFileContainedInDirectory(file, source)  && file.getName().endsWith(".less")  && file.isFile();
    }


    @Override
    public void processAll() throws ProcessorException {
        if (! source.exists()) {
            return;
        }
        Collection<File> files = FileUtils.listFiles(source, new String[]{"less"}, true);
        for (File file : files) {
            if (file.isFile()) {
                compile(file);
            }
        }
    }

    private File getOutputCSSFile(File input) {
        String cssFileName = input.getName().substring(0, input.getName().length() - ".less".length()) + ".css";
        String path = input.getParentFile().getAbsolutePath().substring(source.getAbsolutePath().length());
        return new File(destination, path + "/" + cssFileName);
    }

    private void compile(File file) throws ProcessorException {
        File out = getOutputCSSFile(file);
        getLog().info("Compiling " + file.getAbsolutePath() + " to " + out.getAbsolutePath());
        try {
            String output = less(FileUtils.readFileToString(file));
            FileUtils.write(out, output);
        } catch (IOException e) {
            throw new ProcessorException("Cannot compile " + file.getAbsolutePath(), e);
        }
    }

    @Override
    public void fileCreated(File file) throws ProcessorException {
        compile(file);
    }

    @Override
    public void fileUpdated(File file) throws ProcessorException {
        compile(file);
    }

    @Override
    public void fileDeleted(File file) {
        File theFile = getOutputCSSFile(file);
        if (theFile.exists()) {
            theFile.delete();
        }
    }

    public static final String DEFAULT_LESS_JS = "/less/less-1.3.0.js";
    private static final String SCRIPT_RUN = "run.js";
    private static final String SCRIPT_INIT = "init.js";

    /**
     * Initialize script builder for evaluation.
     */
    private RhinoLauncher initScriptBuilder() {
        try {
            RhinoLauncher builder = null;
            final InputStream initStream = getInitScriptAsStream();
            final InputStream runStream = getRunScriptAsStream();
            builder = RhinoLauncher.newClientSideAwareChain().evaluateChain(initStream, SCRIPT_INIT).evaluateChain(
                    getScriptAsStream(), DEFAULT_LESS_JS).evaluateChain(runStream, SCRIPT_RUN);
            return builder;
        } catch (final Exception e) {
            throw new IllegalStateException("Initialization of the Less processing failed", e);
        }
    }

    /**
     * @return the stream of the script responsible for invoking the less transformation javascript code.
     */
    private InputStream getRunScriptAsStream() {
        //TODO use our own Less file.
        return LessCss.class.getResourceAsStream(SCRIPT_RUN);
    }

    /**
     * @return the stream of the script responsible for initializing less.
     */
    private InputStream getInitScriptAsStream() {
        //TODO use our own Less file.
        return LessCss.class.getResourceAsStream(SCRIPT_INIT);
    }

    /**
     * @return stream of the less.js script.
     */
    private InputStream getScriptAsStream() {
        //TODO use our own Less file.
        return LessCss.class.getResourceAsStream(DEFAULT_LESS_JS);
    }


    /**
     * @param data css content to process.
     * @return processed css content.
     */
    public String less(final String data) throws ProcessorException {
        final RhinoLauncher builder = initScriptBuilder();
        try {
            final String execute = "lessIt(" + WroUtil.toJSMultiLineString(data) + ");";
            final Object result = builder.evaluate(execute, "lessIt");
            return String.valueOf(result);
        } catch (final RhinoException e) {
            throw new ProcessorException("Less compilation failed - " + RhinoUtils.createExceptionMessage(e));
        }
    }
}
