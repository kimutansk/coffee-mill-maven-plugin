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
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.nanoko.coffee.mill.mojos.AbstractCoffeeMillMojo;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Just copy CSS files to an output directory.
 */
public class CSSFileCopyProcessor extends DefaultProcessor {


    private File source;
    private File destination;

    @Override
    public void configure(AbstractCoffeeMillMojo mojo, Map<String, Object> options) {
        super.configure(mojo, options);
        this.source = mojo.stylesheetsDir;
        this.destination = mojo.getWorkDirectory();
    }

    public void processAll() throws ProcessorException {
        if (source.exists()) {
            copyCSSFiles();
        }
    }

    private void copyCSSFiles() throws ProcessorException {
        getLog().info("Copying " + source.getAbsolutePath() + " to " + destination.getAbsolutePath());
        // Create a filter for ".css" files
        IOFileFilter cssSuffixFilter = FileFilterUtils.suffixFileFilter(".css");
        IOFileFilter csssFiles = FileFilterUtils.and(FileFileFilter.FILE, cssSuffixFilter);

        // Create a filter for either directories or ".css" files
        IOFileFilter filter = FileFilterUtils.or(DirectoryFileFilter.DIRECTORY, csssFiles);

        // Copy using the filter
        try {
            FileUtils.copyDirectory(source, destination, filter);
        } catch (IOException e) {
            throw new ProcessorException("Cannot copy CSS files", e);
        }
    }

    public void tearDown() {
        // Do nothing.
    }

    public boolean accept(File file) {
        return isFileContainedInDirectory(file, source)  && file.isFile()  &&  file.getName().endsWith(".css");
    }

    public void fileCreated(File file) throws ProcessorException {
        getLog().info("Copying " + file.getAbsolutePath() + " to " + destination.getAbsolutePath());
        copyFileToDir(file, source, destination);
    }

    public void fileUpdated(File file) throws ProcessorException {
        getLog().info("Copying " + file.getAbsolutePath() + " to " + destination.getAbsolutePath());
        copyFileToDir(file, source, destination);
    }

    public void fileDeleted(File file) {
        File rel = computeRelativeFile(file, source, destination);
        if (rel.isFile()) {
            getLog().info("Deleting " + rel.getAbsolutePath());
            rel.delete();
        }
    }

}
