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
import org.nano.coffee.roasting.utils.OptionsHelper;
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
 * CoffeeScript files are generally in the <tt>src/test/coffee</tt> directory. It can be configured using the
 * <tt>coffeeScriptTestDir</tt> parameter.
 * If the directory does not exist, the compilation is skipped.
 *
 * @goal test-compile-coffeescript
 */
public class CoffeeScriptTestCompilerMojo extends AbstractRoastingCoffeeMojo {

    /**
     * Enables / Disables the coffeescript compilation.
     * Be aware that this property disables the compilation on both main sources and test sources.
     * @parameter default-value="false"
     */
    protected boolean skipCoffeeScriptCompilation;

    /**
     * Enables / Disables the coffeescript test compilation.
     * Be aware that this property disables the compilation of test sources only.
     * @parameter default-value="false"
     */
    protected boolean skipCoffeeScriptTestCompilation;


    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skipCoffeeScriptCompilation  || skipCoffeeScriptTestCompilation) {
            getLog().info("CoffeeScript test compilation skipped");
            return;
        }

        if (! coffeeScriptTestDir.exists()) {
            return;
        }

        CoffeeScriptCompilationProcessor processor = new CoffeeScriptCompilationProcessor();
        processor.configure(this, new OptionsHelper.OptionsBuilder().set("test", true).build());
        try {
            processor.processAll();
        } catch (Processor.ProcessorException e) {
            throw new MojoExecutionException("", e);
        }
    }

}