package org.nano.coffee.roasting.mojos.packaging;

import com.google.javascript.jscomp.*;
import com.google.javascript.jscomp.Compiler;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;
import org.nano.coffee.roasting.mojos.AbstractRoastingCoffeeMojo;
import org.nano.coffee.roasting.processors.JavaScriptAggregator;

import java.io.*;
import java.util.List;

/**
 * Minify JavaScript sources.
 * It can use Google Closure or YUI Compressor.
 * The minified file is attached to the project using the <tt>min</tt> classifier,
 * except if the <tt>attachMinifiedJavaScript</tt> parameter is set to <tt>false</tt>
 * @goal minify-javascript
 */
public class JavaScriptMinifierMojo extends AbstractRoastingCoffeeMojo {

    /**
     * @parameter default-value="false"
     */
    protected boolean skipMinification;

    /**
     * @parameter default-value="true"
     */
    protected boolean attachMinifiedJavaScript;

    /**
     * The JavaScript minifier to use among GOOGLE_CLOSURE (default) and YUI_COMPRESSOR.
     * @parameter default-value="GOOGLE_CLOSURE"
     */
    protected Minifier minifier;

    public enum Minifier {
        GOOGLE_CLOSURE,
        YUI_COMPRESSOR
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skipMinification) {
            getLog().debug("JavaScript Minification skipped");
            return;

        }
        if (! project.getArtifact().getFile().exists()  || ! project.getArtifact().getFile().getName().endsWith("" +
                ".js")) {
            throw new MojoExecutionException("Cannot minify the project artifact - either the file does not exist or " +
                    "is not a JavaScript file");
        }

        File output = new File("target", project.getBuild().getFinalName() + "-min.js");

        if (Minifier.GOOGLE_CLOSURE.equals(minifier)) {
            doGoogleCompression(project.getArtifact().getFile(), output);
        } else if (Minifier.YUI_COMPRESSOR.equals(minifier)) {
            doYUICompression(project.getArtifact().getFile(), output);
        }

        if (! output.isFile()) {
            throw new MojoFailureException("The minified file "+ output.getAbsolutePath() + " does not exist - check " +
                    "log.");
        }

        if (attachMinifiedJavaScript) {
            projectHelper.attachArtifact(project, output, "min");
        }
    }

    private void doGoogleCompression(File file, File output) throws MojoExecutionException {
        getLog().info("Compressing " + file.getName() + " using Google Closure");
        final com.google.javascript.jscomp.Compiler compiler = new Compiler();
        CompilerOptions options = newCompilerOptions();

        final JSSourceFile[] input = new JSSourceFile[] {
                JSSourceFile.fromFile(file)
        };
        JSSourceFile[] externs = new JSSourceFile[] {};

        final Result result = compiler.compile(externs, input, options);
        if (result.success) {
            try {
                FileUtils.write(output, compiler.toSource());
            } catch (IOException e) {
                throw new MojoExecutionException("Cannot write minified file", e);
            }
        } else {
            for (JSError error : result.errors) {
                getLog().error(error.sourceName + ":" + error.lineNumber + " - " + error.description);
            }
            throw new MojoExecutionException("The minification failed - check log");
        }

    }

    private void doYUICompression(File file, File output) throws MojoExecutionException {
        getLog().info("Compressing " + file.getName() + " using YUI Compressor");
        YUIErrorReporter reporter = new YUIErrorReporter();
        try {
            final JavaScriptCompressor compressor = new JavaScriptCompressor(new FileReader(file),
                reporter);
            FileWriter writer = new FileWriter(output);
            // TODO Params.
            boolean verbose = true;
            boolean munge = true;
            boolean preserveAllSemiColons = true;
            boolean disableOptimizations = false;
            int linebreakpos = -1;
            compressor.compress(writer, linebreakpos, munge, verbose, preserveAllSemiColons, disableOptimizations);
            writer.close();
            if (reporter.errorFound) {
                throw new MojoExecutionException("Error during minification - Check log");
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Error during minification", e);
        }
    }


    /**
     * @return default {@link CompilerOptions} object to be used by compressor.
     */
    protected CompilerOptions newCompilerOptions() {
        final CompilerOptions options = new CompilerOptions();
        /**
         * According to John Lenz from the Closure Compiler project, if you are using the Compiler API directly, you
         * should specify a CodingConvention. {@link http://code.google.com/p/wro4j/issues/detail?id=155}
         */
        options.setCodingConvention(new ClosureCodingConvention());
        //set it to warning, otherwise compiler will fail
        options.setWarningLevel(DiagnosticGroups.CHECK_VARIABLES,
                CheckLevel.WARNING);
        return options;
    }

    /**
     * Error reporter.
     */
    private final class YUIErrorReporter
            implements ErrorReporter {

        boolean errorFound = false;

        public void warning(final String message, final String sourceName, final int line, final String lineSource,
                            final int lineOffset) {
            if (line < 0) {
                getLog().warn(sourceName +  " -> " + message);
            } else {
                getLog().warn(sourceName + ":" + line + " -> " + message);
            }
        }


        public void error(final String message, final String sourceName, final int line, final String lineSource,
                          final int lineOffset) {
            if (line < 0) {
                getLog().error(sourceName +  " -> " + message);
            } else {
                getLog().error(sourceName + ":" + line + " -> " + message);
            }

            errorFound = true;
        }


        public EvaluatorException runtimeError(final String message, final String sourceName, final int line,
                                               final String lineSource, final int lineOffset) {
            error(message, sourceName, line, lineSource, lineOffset);
            return new EvaluatorException(message);
        }
    }


}
