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

import org.apache.commons.io.FileUtils;
import org.lesscss.LessCompiler;
import org.lesscss.LessException;
import org.lesscss.LessSource;
import org.nanoko.coffee.mill.mojos.AbstractCoffeeMillMojo;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * Processor handling Less to CSS compilation.
 * It handles <tt>.less</tt> files from the <tt>stylesheets</tt> directory.
 */
public class LessCompilationProcessor extends DefaultProcessor {


    private File source;
    private File destination;
    private LessCompiler lessCompiler;

    public void tearDown() {
        // Do nothing.
    }

    @Override
    public void configure(AbstractCoffeeMillMojo mojo, Map<String, Object> options) {
        super.configure(mojo, options);
        this.source = mojo.stylesheetsDir;
        this.destination = mojo.getWorkDirectory();

        this.lessCompiler = new LessCompiler();
    }

    public boolean accept(File file) {
        return isFileContainedInDirectory(file, source)  && file.getName().endsWith(".less")  && file.isFile();
    }


    @Override
    public void processAll() throws ProcessorException {
        if (! source.exists()) {
            return;
        }
        Collection<File> files = FileUtils.listFiles(source, new String[]{"less"}, true);
        for (File file : files) {
            if (file.isFile()) {
                compile(file);
            }
        }
    }

    private File getOutputCSSFile(File input) {
        String cssFileName = input.getName().substring(0, input.getName().length() - ".less".length()) + ".css";
        String path = input.getParentFile().getAbsolutePath().substring(source.getAbsolutePath().length());
        return new File(destination, path + "/" + cssFileName);
    }

    public void compile(File file) throws ProcessorException {
        File out = getOutputCSSFile(file);
        getLog().info("Compiling " + file.getAbsolutePath() + " to " + out.getAbsolutePath());
        try {
            LessSource lessSource = new LessSource(file);
            if (out.lastModified() < lessSource.getLastModifiedIncludingImports()) {
                getLog().info("Compiling LESS source: " + file + "...");
                lessCompiler.compile(lessSource, out, false);
            } else {
                getLog().info("Bypassing LESS source: " + file + " (not modified)");
            }
        } catch (IOException e) {
            throw new ProcessorException("Cannot initialize Less compilation", e);
        } catch (LessException e) {
            throw new ProcessorException("Compilation error in " + file, e);
        }
    }

    @Override
    public void fileCreated(File file) throws ProcessorException {
        compile(file);
    }

    @Override
    public void fileUpdated(File file) throws ProcessorException {
        compile(file);
    }

    @Override
    public void fileDeleted(File file) {
        File theFile = getOutputCSSFile(file);
        if (theFile.exists()) {
            theFile.delete();
        }
    }
}
