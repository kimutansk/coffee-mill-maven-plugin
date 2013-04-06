/*
 * Copyright 2013 OW2 Nanoko Project
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.nanoko.coffee.mill.processors;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.jruby.embed.LocalContextScope;
import org.jruby.embed.LocalVariableBehavior;
import org.jruby.embed.ScriptingContainer;
import org.nanoko.coffee.mill.mojos.AbstractCoffeeMillMojo;
import org.nanoko.coffee.mill.utils.OptionsHelper;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Processor handling Sass and Compass to CSS compilation.
 * It handles <tt>.scss</tt> files from the <tt>stylesheets</tt> directory.
 */
public class SassCompilationProcessor extends DefaultProcessor {


    protected Map<String, String> sassOptions = new HashMap<String, String>(ImmutableMap.of(
            "unix_newlines", "true",
            "cache", "true",
            "always_update", "true",
            "style", ":expanded"));
    /**
     * Enable the use of Compass style library mixins, this emulates the
     * {@code --compass} commandline option of Sass.
     */
    protected boolean useCompass;
    private File source;
    private File destination;
    private StringBuilder script;
    private ScriptingContainer jruby;
    private File frameworks;

    public void tearDown() {
        // Do nothing.
    }

    @Override
    public void configure(AbstractCoffeeMillMojo mojo, Map<String, Object> options) {
        super.configure(mojo, options);
        this.source = mojo.stylesheetsDir;
        this.destination = mojo.getWorkDirectory();
        this.useCompass = OptionsHelper.getBoolean(options, "useCompass", true);
        this.frameworks = OptionsHelper.getFile(options, "frameworks");

        this.script = new StringBuilder();
        buildSASSScript(script);
        getLog().debug(script.toString());

        getLog().info("Compiling SASS Templates");
        System.setProperty("org.jruby.embed.localcontext.scope", "threadsafe");

//        final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
//        this.jruby = scriptEngineManager.getEngineByName("jruby");
//        jruby.getContext().setAttribute("org.jruby.embed.sharing.variables", false, ScriptContext.ENGINE_SCOPE);

        jruby = new ScriptingContainer(LocalContextScope.THREADSAFE, LocalVariableBehavior.GLOBAL);

    }

    public boolean accept(File file) {
        return isFileContainedInDirectory(file, source) && file.getName().endsWith(".scss") && file.isFile();
    }

    @Override
    public void processAll() throws ProcessorException {
        if (!source.exists()) {
            return;
        }
        Collection<File> files = FileUtils.listFiles(source, new String[]{"scss"}, true);
        if (! files.isEmpty()) {
            compile();
        }
    }

    private File getOutputCSSFile(File input) {
        String cssFileName = input.getName().substring(0, input.getName().length() - ".scss".length()) + ".css";
        String path = input.getParentFile().getAbsolutePath().substring(source.getAbsolutePath().length());
        return new File(destination, path + "/" + cssFileName);
    }

    private void compile() throws ProcessorException {
        //Execute the SASS Compilation Ruby Script
        final SassCompilationErrors compilationErrors = (SassCompilationErrors) jruby.runScriptlet(script.toString());
        if (compilationErrors.hasErrors()) {
            for (SassCompilationErrors.CompilationError error : compilationErrors) {
                getLog().error("Compilation of template " + error.filename + " failed: " + error.message);
            }
            throw new ProcessorException("SASS compilation encountered errors (see above for details).");
        }
    }

    @Override
    public void fileCreated(File file) throws ProcessorException {
        compile();
    }

    @Override
    public void fileUpdated(File file) throws ProcessorException {
        compile();
    }

    @Override
    public void fileDeleted(File file) {
        File theFile = getOutputCSSFile(file);
        if (theFile.exists()) {
            theFile.delete();
        }
    }

    /**
     * Dump the Ruby script used to execute the Sass compilation.
     *
     * @param script the string builder where the script is written.
     */
    protected void buildSASSScript(final StringBuilder script) {
        script.append("require 'rubygems'\n");
        script.append("require 'sass/plugin'\n");
        script.append("require 'java'\n");

        if (this.useCompass) {
            getLog().info("Running with Compass enabled.");
            script.append("require 'compass'\n");
            script.append("require 'compass/exec'\n");
            script.append("Compass.add_project_configuration \n");
            this.sassOptions.put("load_paths", "Compass.configuration.sass_load_paths");
            // manually specify these paths
            script.append("Compass::Frameworks.register_directory('jar:'+ File.join(Compass.base_directory, 'frameworks/compass'))\n");
            script.append("Compass::Frameworks.register_directory('jar:'+ File.join(Compass.base_directory, 'frameworks/blueprint'))\n");
            script.append("Compass::Frameworks.register_directory('").append(FilenameUtils.separatorsToUnix(frameworks
                    .getAbsolutePath() + "/compass")).append("')").append("\n");
            script.append("Compass::Frameworks.register_directory('").append(FilenameUtils.separatorsToUnix(frameworks
                    .getAbsolutePath() + "/blueprint")).append("')").append("\n");
        }

        script.append("Sass::Plugin.options.merge!(\n");

        //If not explicitly set place the cache location in the target dir
        if (!this.sassOptions.containsKey("cache_location")) {
            final File sassCacheDir = new File(destination, "sass_cache");
            final String sassCacheDirStr = sassCacheDir.toString();
            this.sassOptions.put("cache_location", "'" + FilenameUtils.separatorsToUnix(sassCacheDirStr) + "'");
        }

        //Add the plugin configuration options
        for (final Iterator<Map.Entry<String, String>> entryItr = this.sassOptions.entrySet().iterator(); entryItr.hasNext(); ) {
            final Map.Entry<String, String> optEntry = entryItr.next();
            final String opt = optEntry.getKey();
            final String value = optEntry.getValue();
            script.append("    :").append(opt).append(" => ").append(value);
            if (entryItr.hasNext()) {
                script.append(",");
            }
            script.append("\n");
        }
        script.append(")\n");

        // set up compilation error reporting
        script.append("java_import ");
        script.append(SassCompilationErrors.class.getName());
        script.append("\n");
        script.append("$compilation_errors = SassCompilationErrors.new\n");
        script.append("Sass::Plugin.on_compilation_error {|error, template, css| $compilation_errors.add(template, error.message) }\n");

        //Add the SASS template locations
        getLog().info("Queuing SASS Template for compile: " + source.getAbsoluteFile());

        script.append("Sass::Plugin.add_template_location('")
                .append(FilenameUtils.separatorsToUnix(source.getAbsolutePath()))
                .append("', '")
                .append(FilenameUtils.separatorsToUnix(destination.getAbsolutePath()))
                .append("')\n");
        // make ruby give use some debugging info when requested
        if (getLog().isDebugEnabled()) {
            script.append("require 'pp'\npp Sass::Plugin.options\n");
            script.append("pp Compass::configuration\n");
        }

        script.append("Sass::Plugin.update_stylesheets\n");
        script.append("return $compilation_errors\n");
    }

}
