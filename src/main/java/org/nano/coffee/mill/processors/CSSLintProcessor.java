package org.nano.coffee.mill.processors;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;
import org.mozilla.javascript.RhinoException;
import org.nano.coffee.mill.mojos.AbstractCoffeeMillMojo;
import org.nano.coffee.mill.utils.OptionsHelper;
import org.nano.coffee.mill.utils.RhinoLauncher;
import ro.isdc.wro.extensions.processor.support.csslint.CssLint;
import ro.isdc.wro.extensions.processor.support.csslint.CssLintError;
import ro.isdc.wro.extensions.script.RhinoUtils;
import ro.isdc.wro.util.WroUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Processor validating CSS files using jslint.
 */
public class CSSLintProcessor extends DefaultProcessor {

    private static final String DEFAULT_CSSLINT_JS = "csslint.min.js";
    private File source;


    public List<ProcessorWarning> validate(File file) throws ProcessorException {
        List warnings = new ArrayList<ProcessorWarning>();
        try {
            String data = FileUtils.readFileToString(file);
            data = WroUtil.toJSMultiLineString(data);
            final RhinoLauncher builder = initScriptBuilder();
            String script = String.format("var result = CSSLint.verify(%s,%s)", data,
                    "CSSLint.getRules()"); // All rules.
            builder.evaluate(script, "CSSLint.verify").toString();

            final boolean valid = Boolean.parseBoolean(builder.evaluate("result.messages.length == 0",
                    "checkNoErrors").toString());
            if (!valid) {
                final String json = builder.addJSON().evaluate("JSON.stringify(result.messages)",
                        "CssLint messages").toString();
                final Type type = new TypeToken<List<CssLintError>>() {
                }.getType();
                final List<CssLintError> errors = new Gson().fromJson(json, type);
                getLog().debug("Errors: " + errors);
                for (CssLintError error : errors) {
                    warnings.add(new ProcessorWarning(file, error.getLine(), error.getCol(), error.getEvidence(),
                            error.getType() + " : " + error.getMessage()));
                }
            }
            return warnings;
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
    public void configure(AbstractCoffeeMillMojo mojo, Map<String, Object> options) {
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
                List<ProcessorWarning> warnings = validate(file);
                for (ProcessorWarning warning : warnings) {
                    getLog().warn("In " + file.getName() + " @" + warning.line + ":" + warning.character + " -> " +
                            warning.evidence + " - " + warning.reason);
                }
            }
        }
    }

    @Override
    public void fileCreated(File file) throws ProcessorException {
        List<ProcessorWarning> warnings = validate(file);
        for (ProcessorWarning warning : warnings) {
            getLog().warn("In " + file.getName() + " @" + warning.line + ":" + warning.character + " -> " +
                    warning.evidence + " - " + warning.reason);
        }
    }

    @Override
    public void fileUpdated(File file) throws ProcessorException {
        fileCreated(file);
    }

    /**
     * Initialize script builder for evaluation.
     */
    private RhinoLauncher initScriptBuilder() {
        try {
            return RhinoLauncher.newChain().evaluateChain(getScriptAsStream(),
                    DEFAULT_CSSLINT_JS);
        } catch (final IOException ex) {
            throw new IllegalStateException("Failed reading init script", ex);
        }
    }
}
