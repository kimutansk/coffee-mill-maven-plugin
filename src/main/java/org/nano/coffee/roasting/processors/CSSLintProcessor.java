package org.nano.coffee.roasting.processors;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.mozilla.javascript.RhinoException;
import org.nano.coffee.roasting.utils.OptionsHelper;
import ro.isdc.wro.WroRuntimeException;
import ro.isdc.wro.extensions.processor.support.coffeescript.CoffeeScript;
import ro.isdc.wro.extensions.processor.support.csslint.CssLint;
import ro.isdc.wro.extensions.processor.support.csslint.CssLintError;
import ro.isdc.wro.extensions.processor.support.csslint.CssLintException;
import ro.isdc.wro.extensions.script.RhinoScriptBuilder;
import ro.isdc.wro.extensions.script.RhinoUtils;
import ro.isdc.wro.util.StopWatch;
import ro.isdc.wro.util.WroUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Processor validating CSS files using jslint.
 */
public class CSSLintProcessor implements Processor {

    private static final String DEFAULT_CSSLINT_JS = "csslint.min.js";

    public void process(File input, Map<String, ?> options) throws ProcessorException {
        File output = OptionsHelper.getDirectory(options, "output", true);
        if (output == null) {
            throw new ProcessorException("Output Parameter missing or invalid");
        }

        Log logger = OptionsHelper.getLogger(options, "logger");
        if (logger == null) {
            throw new ProcessorException("Logger parameter missing");
        }

        try {
            validate(FileUtils.readFileToString(input), logger);
        } catch (IOException e) {
            throw new ProcessorException("Can't check " + input.getAbsolutePath(), e);
        } catch (CssLintException e) {
            for (CssLintError exp : e.getErrors()) {
                logger.warn("In " + input.getName() + " at " + exp.getLine() + ":" + exp.getCol()
                        + " - "
                        + exp.getType() + " - " + exp.getMessage() + " (" + exp.getEvidence() + ")");
            }
        }
    }

    public void validate(final String data, Log logger) throws CssLintException, ProcessorException {
        try {
            final RhinoScriptBuilder builder = initScriptBuilder();
            String script = String.format("var result = CSSLint.verify(%s,%s).messages", data,
                    "{}"); // No option
            builder.evaluate(script, "CSSLint.verify").toString();
            final boolean valid = Boolean.parseBoolean(builder.evaluate("result.length == 0", "checkNoErrors").toString());
            if (!valid) {
                final String json = builder.addJSON().evaluate("JSON.stringify(result)", "CssLint messages").toString();
                final Type type = new TypeToken<List<CssLintError>>() {
                }.getType();
                final List<CssLintError> errors = new Gson().fromJson(json, type);
                logger.debug("Errors: " + errors);
                throw new CssLintException().setErrors(errors); // TODO Change exception.
            }
        } catch (final RhinoException e) {
            throw new ProcessorException(RhinoUtils.createExceptionMessage(e), e);    // TODO Extract Rhino Utils.
        }
    }

    protected InputStream getScriptAsStream() {
        //TODO Extract csslint.
        return CssLint.class.getResourceAsStream(DEFAULT_CSSLINT_JS);
    }

    public void tearDown() {
        // Do nothing.
    }

    public boolean accept(File file) {
        return file.getName().endsWith(".css") && file.isFile();
    }

    /**
     * Initialize script builder for evaluation.
     */
    private RhinoScriptBuilder initScriptBuilder() {
        try {
            return RhinoScriptBuilder.newChain().evaluateChain(getScriptAsStream(),
                    DEFAULT_CSSLINT_JS);
        } catch (final IOException ex) {
            throw new IllegalStateException("Failed reading init script", ex);
        }
    }
}
