package org.nano.coffee.roasting.processors;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.mozilla.javascript.RhinoException;
import org.nano.coffee.roasting.mojos.AbstractRoastingCoffeeMojo;
import org.nano.coffee.roasting.utils.OptionsHelper;
import ro.isdc.wro.WroRuntimeException;
import ro.isdc.wro.extensions.processor.support.coffeescript.CoffeeScript;
import ro.isdc.wro.extensions.processor.support.csslint.CssLint;
import ro.isdc.wro.extensions.processor.support.csslint.CssLintError;
import ro.isdc.wro.extensions.processor.support.csslint.CssLintException;
import ro.isdc.wro.extensions.processor.support.linter.LinterError;
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
public class CSSLintProcessor extends DefaultProcessor {

    private static final String DEFAULT_CSSLINT_JS = "csslint.min.js";
    private File source;


    public void validate(File file) throws CssLintException, ProcessorException {
        try {
            String data = FileUtils.readFileToString(file);
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
                getLog().debug("Errors: " + errors);
                throw new CssLintException().setErrors(errors); // TODO Change exception.
            }
        } catch (final RhinoException e) {
            throw new ProcessorException(RhinoUtils.createExceptionMessage(e), e);    // TODO Extract Rhino Utils.
        } catch (IOException e) {
            throw new ProcessorException("Can't read CSS file " + file.getAbsolutePath(), e);
        }
    }

    protected InputStream getScriptAsStream() {
        //TODO Extract csslint.
        return CssLint.class.getResourceAsStream(DEFAULT_CSSLINT_JS);
    }

    public void tearDown() {
        // Do nothing.
    }

    @Override
    public void configure(AbstractRoastingCoffeeMojo mojo, Map<String, Object> options) {
        super.configure(mojo, options);
        this.source = OptionsHelper.getDirectory(options, "directory", false);
    }

    /**
     * Accepts CSS files from
     *
     * @param file
     * @return
     */
    public boolean accept(File file) {
        return isFileContainedInDirectory(file, source) && file.getName().endsWith(".css") && file.isFile();
    }

    @Override
    public void processAll() throws ProcessorException {
        Collection<File> files = FileUtils.listFiles(source, new String[]{"css"}, true);
        for (File file : files) {
            if (file.isFile()) {
                try {
                    validate(file);
                } catch (CssLintException e) {
                    if (!e.getErrors().isEmpty()) {
                        for (CssLintError exp : e.getErrors()) {
                            if (exp == null) {
                                continue;
                            }
                            getLog().warn("In " + file.getName() + " at " + exp.getLine() + " - " + exp.getType()
                                    + " - "
                                    + exp.getEvidence() + " - " + exp.getRule().getName() + " (" + exp.getRule()
                                    .getDesc() + ")");
                        }
                    }
                }
            }
        }
    }

    @Override
    public void fileCreated(File file) throws ProcessorException {
        try {
            validate(file);
        } catch (CssLintException e) {
            if (!e.getErrors().isEmpty()) {
                for (CssLintError exp : e.getErrors()) {
                    if (exp == null) {
                        continue;
                    }
                    getLog().warn("In " + file.getName() + " at " + exp.getLine() + " - " + exp.getType()
                            + " - "
                            + exp.getEvidence() + " - " + exp.getRule().getName() + " (" + exp.getRule()
                            .getDesc() + ")");
                }
            }
        }
    }

    @Override
    public void fileUpdated(File file) throws ProcessorException {
        fileCreated(file);
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
