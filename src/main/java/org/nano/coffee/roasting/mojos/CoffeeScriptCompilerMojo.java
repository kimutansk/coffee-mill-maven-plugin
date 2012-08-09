package org.nano.coffee.roasting.mojos;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.ScriptableObject;
import ro.isdc.wro.extensions.processor.support.coffeescript.CoffeeScript;
import ro.isdc.wro.extensions.script.RhinoScriptBuilder;
import ro.isdc.wro.util.StopWatch;
import ro.isdc.wro.util.WroUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

/**
 * Compiles CoffeeScript files.
 *
 * @goal compile-coffeescript
 */
public class CoffeeScriptCompilerMojo extends AbstractRoastingCoffeeMojo {


    public void execute() throws MojoExecutionException, MojoFailureException {
        if (! coffeeScriptDir.exists()) {
            return;
        }
        Collection<File> files = FileUtils.listFiles(coffeeScriptDir, new String[]{"coffee"}, true);
        for (File file : files) {
            compile(file);
        }
        getLog().info(files.size() + " CoffeeScript file(s) compiled");
    }

    private void compile(File file) throws MojoFailureException {
        getLog().info("Compiling " + file.getAbsolutePath());
        CoffeeScriptCompiler coffeeScript = new CoffeeScriptCompiler();
        String jsFileName = file.getName().substring(0, file.getName().length() - ".coffee".length()) + ".js";
        try {
            File out = new File(getWorkDirectory(), jsFileName);
            String output = coffeeScript.compile(FileUtils.readFileToString(file));
            FileUtils.write(out, output);
        } catch (RhinoException jse) {
                throw new MojoFailureException("Compilation Error in " + file.getName() + "@" + jse.lineNumber() +
                        " - " + jse.details());
        } catch (IOException e) {
            throw new MojoFailureException("Cannot compile " + file.getAbsolutePath(), e);
        }
    }

    class CoffeeScriptCompiler {
        private String[] options;
        private ScriptableObject scope;
        private static final String DEFAULT_COFFE_SCRIPT = "coffee-script.min.js";

        /**
         * Initialize script builder for evaluation.
         */
        private RhinoScriptBuilder initScriptBuilder() {
            try {
                RhinoScriptBuilder builder = null;
                if (scope == null) {
                    builder = RhinoScriptBuilder.newChain().evaluateChain(getCoffeeScriptAsStream(), DEFAULT_COFFE_SCRIPT);
                    scope = builder.getScope();
                } else {
                    builder = RhinoScriptBuilder.newChain(scope);
                }
                return builder;
            } catch (final IOException ex) {
                throw new IllegalStateException("Failed reading init script", ex);
            }
        }

        /**
         * Override this method to use a different version of CoffeeScript. This method is useful for upgrading coffeeScript
         * processor independently of wro4j.
         *
         * @return The stream of the CoffeeScript.
         */
        protected InputStream getCoffeeScriptAsStream() {
            return CoffeeScript.class.getResourceAsStream(DEFAULT_COFFE_SCRIPT);
        }

        public String compile(final String data) throws JavaScriptException {
            final StopWatch watch = new StopWatch();
            watch.start("init");
            try {
                final RhinoScriptBuilder builder = initScriptBuilder();
                watch.stop();
                watch.start("compile");
                final String compileScript = String.format("CoffeeScript.compile(%s, %s);", WroUtil.toJSMultiLineString(data),
                        buildOptions());
                final String result = (String) builder.evaluate(compileScript, "CoffeeScript.compile");
                return result;
            } catch (RhinoException e) {
                throw e;
            } finally {
                watch.stop();
                getLog().debug(watch.prettyPrint());
            }
        }

        /**
         * @return A javascript representation of the options. The result is a json object.
         */
        private String buildOptions() {
            final StringBuffer sb = new StringBuffer("{");
            if (options != null) {
                for (int i = 0; i < options.length; i++) {
                    sb.append(options[i] + ": true");
                    if (i < options.length - 1) {
                        sb.append(",");
                    }
                }
            }
            sb.append("}");
            return sb.toString();
        }
    }
}
