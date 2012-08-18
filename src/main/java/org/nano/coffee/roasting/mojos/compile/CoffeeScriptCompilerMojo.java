package org.nano.coffee.roasting.mojos.compile;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.ScriptableObject;
import org.nano.coffee.roasting.mojos.AbstractRoastingCoffeeMojo;
import org.nano.coffee.roasting.processors.CoffeeScriptCompilationProcessor;
import org.nano.coffee.roasting.processors.Processor;
import ro.isdc.wro.extensions.processor.support.coffeescript.CoffeeScript;
import ro.isdc.wro.extensions.script.RhinoScriptBuilder;
import ro.isdc.wro.util.StopWatch;
import ro.isdc.wro.util.WroUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Compiles CoffeeScript files.
 * CoffeeScript files are generally in the <tt>src/main/coffee</tt> directory. It can be configured using the
 * <tt>coffeeScriptDir</tt> parameter.
 * If the directory does not exist, the compilation is skipped.
 * @goal compile-coffeescript
 */
public class CoffeeScriptCompilerMojo extends AbstractRoastingCoffeeMojo {

    /**
     * Enables / Disables the coffeescript compilation.
     * Be aware that this property disables the compilation on both main sources and test sources.
     * @parameter default-value="false"
     */
    protected boolean skipCoffeeScriptCompilation;

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skipCoffeeScriptCompilation) {
            getLog().info("CoffeeScript compilation skipped");
            return;
        }

        if (! coffeeScriptDir.exists()) {
            getLog().info("CoffeeScript compilation skipped - " + coffeeScriptDir.getAbsolutePath() + " does not " +
                    "exist");
            return;
        }

        Collection<File> files = FileUtils.listFiles(coffeeScriptDir, new String[]{"coffee"}, true);
        for (File file : files) {
            compile(file);
        }
        getLog().info(files.size() + " CoffeeScript file(s) compiled");
    }

    private void compile(File file) throws MojoExecutionException {
        Processor coffeescriptProcessor = new CoffeeScriptCompilationProcessor();
        Map<String, File> options = new HashMap<String, File>();
        options.put("output", getWorkDirectory());
        getLog().info("Compiling " + file.getAbsolutePath());
        try {
            coffeescriptProcessor.process(file, options);
        } catch (Processor.ProcessorException e) {
            getLog().error("Error during coffeescript processing", e);
            throw new MojoExecutionException("Error during coffeescript processing", e);
        }
    }

}
