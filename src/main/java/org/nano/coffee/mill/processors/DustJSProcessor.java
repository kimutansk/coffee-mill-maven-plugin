package org.nano.coffee.mill.processors;

import org.apache.commons.io.FileUtils;
import org.nano.coffee.mill.mojos.AbstractCoffeeMillMojo;
import org.nano.coffee.mill.utils.RhinoLauncher;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

/**
 * Compiles dust templates.
 * Dust templates are in the javascript directory using the .dust extension.
 * Notice it used the LinkedIn fork of dust.js, more info <a href="http://linkedin.github.com/dustjs/">here</a>.
 */
public class DustJSProcessor extends DefaultProcessor {

    public static final String DUST_SCRIPT = "/dust/dust-full-1.0.0.js";

    private File source;
    private File destination;

    @Override
    public void configure(AbstractCoffeeMillMojo mojo, Map<String, Object> options) {
        super.configure(mojo, options);
        this.source = mojo.javaScriptDir;
        this.destination = mojo.getWorkDirectory();
    }

    @Override
    public void processAll() throws ProcessorException {
        getLog().info("Compiling dust templates");
        Collection<File> files = FileUtils.listFiles(source, new String[]{"dust"}, true);
        for (File file : files) {
            dust(file);
        }
    }

    private void dust(File input) throws ProcessorException {
        try {
            File output = getOutputJSFile(input);
            RhinoLauncher launcher = initScriptBuilder();
            String content = FileUtils.readFileToString(input);
            String compileScript =
                    String.format("%s(%s);", "dust.compile", RhinoLauncher.toJSMultiLineString(content));
            String result = (String) launcher.evaluate(compileScript, "dust.compile");
            FileUtils.write(output, result);
        } catch (IOException e) {
            getLog().error("Dust compilation failed - was not able to compile " + input.getAbsolutePath(), e);
            throw new ProcessorException("Dust compilation failed - was not able to compile " + input
                    .getAbsolutePath(), e);
        }
    }

    @Override
    public boolean accept(File file) {
        return file.isFile() && isFileContainedInDirectory(file, mojo.javaScriptDir)
                && file.getName().endsWith(".dust");
    }

    @Override
    public void fileCreated(File file) throws ProcessorException {
        dust(file);
    }

    @Override
    public void fileUpdated(File file) throws ProcessorException {
        dust(file);
    }

    @Override
    public void fileDeleted(File file) {
        File theFile = getOutputJSFile(file);
        if (theFile.exists()) {
            theFile.delete();
        }
    }

    private File getOutputJSFile(File input) {
        String jsFileName = input.getName().substring(0, input.getName().length() - ".dust".length()) + ".js";
        String path = input.getParentFile().getAbsolutePath().substring(source.getAbsolutePath().length());
        return new File(destination, path + "/" + jsFileName);
    }

    /**
     * Initialize script builder for evaluation.
     */
    private RhinoLauncher initScriptBuilder() {
        try {
            RhinoLauncher builder = null;
            final InputStream script = getScriptAsStream();
            builder = RhinoLauncher.newClientSideAwareChain().evaluateChain(script, DUST_SCRIPT);
            return builder;
        } catch (final Exception e) {
            throw new IllegalStateException("Initialization of the Dust processing failed", e);
        }
    }

    public InputStream getScriptAsStream() {
        return getClass().getResourceAsStream(DUST_SCRIPT);
    }
}
