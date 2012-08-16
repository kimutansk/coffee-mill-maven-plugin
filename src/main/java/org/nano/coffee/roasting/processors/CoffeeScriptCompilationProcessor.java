package org.nano.coffee.roasting.processors;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.ScriptableObject;
import org.nano.coffee.roasting.utils.OptionsHelper;
import ro.isdc.wro.extensions.processor.support.coffeescript.CoffeeScript;
import ro.isdc.wro.extensions.script.RhinoScriptBuilder;
import ro.isdc.wro.util.StopWatch;
import ro.isdc.wro.util.WroUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Processor handling CoffeeScript to JavaScript compilation.
 * It handles <tt>.coffee</tt> files.
 */
public class CoffeeScriptCompilationProcessor implements Processor {


    public void process(File input, Map<String, ?> options) throws ProcessorException {
        File output = OptionsHelper.getDirectory(options, "output", true);
        if (output == null) {
            throw new ProcessorException("Output Parameter missing or invalid");
        }

        String jsFileName = input.getName().substring(0, input.getName().length() - ".coffee".length()) + ".js";

        try {
            final String data = FileUtils.readFileToString(input);
            final File out = new File(output, jsFileName);
            final RhinoScriptBuilder builder = initScriptBuilder();
            final String compileScript = String.format("CoffeeScript.compile(%s, %s);",
                    WroUtil.toJSMultiLineString(data), // TODO Extract method in a helper class.
                    "{}"); // No options
            final String result = (String) builder.evaluate(compileScript, "CoffeeScript.compile");
            FileUtils.write(out, result);
        } catch (RhinoException jse) {
            throw new ProcessorException("Compilation Error in " + input.getName() + "@" + jse.lineNumber() +
                    " - " + jse.details());
        } catch (IOException e) {
            throw new ProcessorException("Cannot compile " + input.getAbsolutePath(), e);
        }

    }

    public void tearDown() {
        // Do nothing.
    }

    public boolean accept(File file) {
        return file.getName().endsWith(".coffee")  && file.isFile();
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
