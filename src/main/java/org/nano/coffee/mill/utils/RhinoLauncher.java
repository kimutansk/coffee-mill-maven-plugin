package org.nano.coffee.mill.utils;


import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.tools.ToolErrorReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.isdc.wro.extensions.script.RhinoUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class RhinoLauncher {
    private static final Logger LOG = LoggerFactory.getLogger(RhinoLauncher.class);
    private final ScriptableObject scope;


    private RhinoLauncher() {
        this(null);
    }


    private RhinoLauncher(final ScriptableObject scope) {
        this.scope = createContext(scope);
    }


    private Context getContext() {
        initContext();
        return Context.getCurrentContext();
    }

    /**
     * @return the context
     */
    public ScriptableObject getScope() {
        return this.scope;
    }


    /**
     * Initialize the context.
     */
    private ScriptableObject createContext(final ScriptableObject initialScope) {
        Context context = getContext();
        context.setOptimizationLevel(-1);
        // TODO redirect errors from System.err to LOG.error()
        context.setErrorReporter(new ToolErrorReporter(false));
        context.setLanguageVersion(Context.VERSION_1_8);
        InputStream script = null;
        final ScriptableObject scope = (ScriptableObject) context.initStandardObjects(initialScope);
        try {
            script = getClass().getResourceAsStream("/rhino/commons.js");
            context.evaluateReader(scope, new InputStreamReader(script), "commons.js", 1, null);
        } catch (final IOException e) {
            throw new RuntimeException("Problem while evaluationg commons script.", e);
        } finally {
            IOUtils.closeQuietly(script);
        }
        return scope;
    }

    /**
     * Add a client side environment to the script context (client-side aware).
     *
     * @return {@link RhinoLauncher} used to chain evaluation of the scripts.
     * @throws IOException
     */
    public RhinoLauncher addClientSideEnvironment() {
        try {
            final String SCRIPT_ENV = "/rhino/env.rhino.min.js";
            final InputStream script = getClass().getResourceAsStream(SCRIPT_ENV);
            evaluateChain(script, SCRIPT_ENV);
            return this;
        } catch (final IOException e) {
            throw new RuntimeException("Couldn't initialize env.rhino script", e);
        }
    }


    public RhinoLauncher addJSON() {
        try {
            final String SCRIPT_ENV = "/rhino/json2.min.js";
            final InputStream script = getClass().getResourceAsStream(SCRIPT_ENV);
            evaluateChain(script, SCRIPT_ENV);
            return this;
        } catch (final IOException e) {
            throw new RuntimeException("Couldn't initialize json2.min.js script", e);
        }
    }

    /**
     * Add an equivalent to the <code>require</code> method from node in for Rhino.
     *
     * @return {@link RhinoLauncher} used to chain evaluation of the scripts.
     * @throws IOException
     */
    public RhinoLauncher addRequire(String streamroot) {
        try {
            final String SCRIPT_ENV = "/rhino/rhino.require.js";
            if (streamroot != null) {
                evaluate("var streamroot=" + streamroot + "; print(\"Stream Root \" + streamroot);", SCRIPT_ENV);
                ScriptableObject.putProperty(getScope(), "streamloader", new ScriptLoader());
            }
            final InputStream script = getClass().getResourceAsStream(SCRIPT_ENV);
            evaluateChain(script, SCRIPT_ENV);
            return this;
        } catch (final IOException e) {
            throw new RuntimeException("Couldn't initialize env.rhino script", e);
        }
    }


    /**
     * Evaluates a script and return {@link RhinoLauncher} for a chained script evaluation.
     *
     * @param stream     {@link InputStream} of the script to evaluate.
     * @param sourceName the name of the evaluated script.
     * @return {@link RhinoLauncher} chain with required script evaluated.
     * @throws IOException if the script couldn't be retrieved.
     */
    public RhinoLauncher evaluateChain(final InputStream stream, final String sourceName)
            throws IOException {
        Validate.notNull(stream);
        try {
            getContext().evaluateReader(scope, new InputStreamReader(stream), sourceName, 1, null);
            return this;
        } catch (final RuntimeException e) {
            LOG.error("Exception caught", e);
            if (e instanceof RhinoException) {
                LOG.error("RhinoException: " + RhinoUtils.createExceptionMessage((RhinoException) e));
            }
            throw e;
        } finally {
            stream.close();
        }
    }

    /**
     * Makes sure the context is properly initialized.
     */
    private void initContext() {
        if (Context.getCurrentContext() == null) {
            Context.enter();
        }
    }


    /**
     * Evaluates a script and return {@link RhinoLauncher} for a chained script evaluation.
     *
     * @param script     the string representation of the script to evaluate.
     * @param sourceName the name of the evaluated script.
     * @return evaluated object.
     * @throws IOException if the script couldn't be retrieved.
     */
    public RhinoLauncher evaluateChain(final String script, final String sourceName) {
        Validate.notNull(script);
        getContext().evaluateString(scope, script, sourceName, 1, null);
        return this;
    }


    /**
     * Evaluates a script from a reader.
     *
     * @param reader     {@link java.io.Reader} of the script to evaluate.
     * @param sourceName the name of the evaluated script.
     * @return evaluated object.
     * @throws IOException if the script couldn't be retrieved.
     */
    public Object evaluate(final Reader reader, final String sourceName)
            throws IOException {
        Validate.notNull(reader);
        try {
            return evaluate(IOUtils.toString(reader), sourceName);
        } finally {
            reader.close();
        }
    }


    /**
     * Evaluates a script.
     *
     * @param script     string representation of the script to evaluate.
     * @param sourceName the name of the evaluated script.
     * @return evaluated object.
     * @throws IOException if the script couldn't be retrieved.
     */
    public Object evaluate(final String script, final String sourceName) {
        Validate.notNull(script);
        // make sure we have a context associated with current thread
        try {
            return getContext().evaluateString(scope, script, sourceName, 1, null);
        } catch (final JavaScriptException e) {
            LOG.error("JavaScriptException occured: " + e.getMessage());
            throw e;
        } finally {
            // Rhino throws an exception when trying to exit twice. Make sure we don't get any exception
            if (Context.getCurrentContext() != null) {
                Context.exit();
            }
        }
    }

    /**
     * @return default {@link RhinoLauncher} for script evaluation chaining.
     */
    public static RhinoLauncher newChain() {
        return new RhinoLauncher();
    }


    public static RhinoLauncher newChain(final ScriptableObject scope) {
        return new RhinoLauncher(scope);
    }

    /**
     * Transforms a java multi-line string into javascript multi-line string. This technique was found at
     * {@link http://stackoverflow.com/questions/805107/multiline-strings-in-javascript/}
     *
     * @param data a string containing new lines.
     * @return a string which being evaluated on the client-side will be treated as a correct multi-line string.
     */
    public static String toJSMultiLineString(final String data) {
        final String[] lines = data.split("\n");
        final StringBuffer result = new StringBuffer("[");
        if (lines.length == 0) {
            result.append("\"\"");
        }
        for (int i = 0; i < lines.length; i++) {
            final String line = lines[i];
            result.append("\"");
            result.append(line.replace("\\", "\\\\").replace("\"", "\\\"").replaceAll("\\r|\\n", ""));
            // this is used to force a single line to have at least one new line (otherwise cssLint fails).
            if (lines.length == 1) {
                result.append("\\n");
            }
            result.append("\"");
            if (i < lines.length - 1) {
                result.append(",");
            }
        }
        result.append("].join(\"\\n\")");
        return result.toString();
    }


    /**
     * @return default {@link RhinoLauncher} for script evaluation chaining.
     */
    public static RhinoLauncher newClientSideAwareChain() {
        return new RhinoLauncher().addClientSideEnvironment();
    }

    public static class ScriptLoader {
        public static String load(String streamName) {
            System.out.println("Loading " + streamName);
            InputStream stream = ScriptLoader.class.getResourceAsStream(streamName);
            if (stream == null) {
                return null;
            } else {
                try {
                    String s = IOUtils.toString(stream);
                    System.out.println("Loading " + streamName + "..." + s.length());
                    return s;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                } finally {
                    IOUtils.closeQuietly(stream);
                }

            }
        }
    }
}

