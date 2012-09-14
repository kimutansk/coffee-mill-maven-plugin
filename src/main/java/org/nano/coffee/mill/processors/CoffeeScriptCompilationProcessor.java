package org.nano.coffee.mill.processors;

import org.apache.commons.io.FileUtils;
import org.mozilla.javascript.RhinoException;
import org.nano.coffee.mill.mojos.AbstractCoffeeMillMojo;
import org.nano.coffee.mill.utils.OptionsHelper;
import ro.isdc.wro.extensions.processor.support.coffeescript.CoffeeScript;
import ro.isdc.wro.extensions.script.RhinoScriptBuilder;
import ro.isdc.wro.util.WroUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

/**
 * Processor handling CoffeeScript to JavaScript compilation.
 * It handles <tt>.coffee</tt> files.
 */
public class CoffeeScriptCompilationProcessor extends DefaultProcessor {


    private File source;
    private File destination;

    public void tearDown() {
        // Do nothing.
    }

    @Override
    public void configure(AbstractCoffeeMillMojo millMojo, Map<String, Object> options) {
        super.configure(millMojo, options);
        if (OptionsHelper.getBoolean(options, "test", false)) {
            this.source = millMojo.coffeeScriptTestDir;
            this.destination = millMojo.getWorkTestDirectory();
        } else {
            this.source = millMojo.coffeeScriptDir;
            this.destination = millMojo.getWorkDirectory();
        }
    }

    public boolean accept(File file) {
        return isFileContainedInDirectory(file, source)  && file.getName().endsWith(".coffee")  && file.isFile();
    }


    @Override
    public void processAll() throws ProcessorException {
        if (! source.exists()) {
            return;
        }
        Collection<File> files = FileUtils.listFiles(source, new String[]{"coffee"}, true);
        for (File file : files) {
            if (file.isFile()) {
                compile(file);
            }
        }
    }

    private File getOutputJSFile(File input) {
        String jsFileName = input.getName().substring(0, input.getName().length() - ".coffee".length()) + ".js";
        String path = input.getParentFile().getAbsolutePath().substring(source.getAbsolutePath().length());
        File theFile = new File(destination, path + "/" + jsFileName);
        return theFile;
    }

    private void compile(File file) throws ProcessorException {
        File out = getOutputJSFile(file);
        getLog().info("Compiling " + file.getAbsolutePath() + " to " + out.getAbsolutePath());
        try {
            final String data = FileUtils.readFileToString(file);
            final RhinoScriptBuilder builder = initScriptBuilder();
            final String compileScript = String.format("CoffeeScript.compile(%s, %s);",
                    WroUtil.toJSMultiLineString(data), // TODO Extract method in a helper class.
                    "{}"); // No options
            final String result = (String) builder.evaluate(compileScript, "CoffeeScript.compile");
            FileUtils.write(out, result);
        } catch (RhinoException jse) {
            throw new ProcessorException("Compilation Error in " + file.getName() + "@" + jse.lineNumber() +
                    " - " + jse.details());
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
        File theFile = getOutputJSFile(file);
        if (theFile.exists()) {
            theFile.delete();
        }
    }

    private static final String DEFAULT_COFFEE_SCRIPT = "coffee-script.min.js";

    /**
     * Initialize script builder for evaluation.
     */
    private RhinoScriptBuilder initScriptBuilder() {
        try {
            return RhinoScriptBuilder.newChain().evaluateChain(getCoffeeScriptAsStream(),
                        DEFAULT_COFFEE_SCRIPT);
        } catch (final IOException ex) {
            throw new IllegalStateException("Failed reading init script", ex);
        }
    }

    protected InputStream getCoffeeScriptAsStream() {
        //TODO Change the coffeescript version.
        return CoffeeScript.class.getResourceAsStream(DEFAULT_COFFEE_SCRIPT);
    }
}
