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

package org.nanoko.coffee.mill.mojos.compile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.nanoko.coffee.mill.mojos.AbstractCoffeeMillMojo;
import org.nanoko.coffee.mill.processors.Processor;
import org.nanoko.coffee.mill.processors.SassCompilationProcessor;
import org.nanoko.coffee.mill.utils.OptionsHelper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Compiles Sass files.
 *
 * @goal compile-sass
 */
public class SassCompilerMojo extends AbstractCoffeeMillMojo {

    SassCompilationProcessor processor;

    /**
     * Enables / Disables the Sass / Compass compilation.
     * @parameter default-value="false"
     */
    protected boolean skipSass;

    /**
     * Enables / Disables the Compass support.
     * @parameter default-value="true"
     */
    public boolean useCompass;



    public SassCompilerMojo() {
        processor = new SassCompilationProcessor();
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skipSass) {
            getLog().debug("The Sass / Compass compilation is disabled");
            return;
        }

        if (!stylesheetsDir.exists()) {
            getLog().debug("The stylesheet directory does not exist - skipping SASS compilation");
            return;
        }

        File frameworks;
        try {
            frameworks = expandCompassFrameworks(this);
        } catch (IOException e) {
            throw new MojoExecutionException("Cannot expand compass frameworks", e);
        }

        processor.configure(this, new OptionsHelper.OptionsBuilder()
                .set("frameworks", frameworks)
                .set("useCompass", useCompass)
                .build());

        try {
            processor.processAll();
        } catch (Processor.ProcessorException e) {
            throw new MojoFailureException("Sass compilation failed", e);
        }
    }

    public static File expandCompassFrameworks(AbstractCoffeeMillMojo mojo) throws IOException {
        File frameworksDest = new File(mojo.buildDirectory, "compass-frameworks");
        if (frameworksDest.exists()) {
            return frameworksDest;
        }

        File zipFile = null;
        for (Artifact artifact : mojo.pluginDependencies) {
            // Detect the compass-frameworks zip file.
            if (artifact.getArtifactId().equals("compass-gems")
                    && "frameworks".equals(artifact.getClassifier())) {
                zipFile = artifact.getFile();
            }
        }

        if (zipFile == null) {
            throw new IOException("Cannot find Compass frameworks in the plugin dependencies");
        }

        // Expand zip.
        ZipFile zip = new ZipFile(zipFile);
        Enumeration<? extends ZipEntry> entries = zip.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            File entryDestination = new File(frameworksDest, entry.getName());
            //noinspection ResultOfMethodCallIgnored
            entryDestination.getParentFile().mkdirs();
            if (entry.isDirectory()) {
                //noinspection ResultOfMethodCallIgnored
                entryDestination.mkdirs();
            } else {
                InputStream zis = zip.getInputStream(entry);
                byte[] bytes = IOUtils.toByteArray(zis);
                FileUtils.writeByteArrayToFile(entryDestination, bytes);
            }
        }
        mojo.getLog().info("Compass frameworks expended to " + frameworksDest.getAbsolutePath());
        return frameworksDest;
    }

    public SassCompilationProcessor getProcessor() {
        processor.configure(this, null);
        return processor;
    }
}
