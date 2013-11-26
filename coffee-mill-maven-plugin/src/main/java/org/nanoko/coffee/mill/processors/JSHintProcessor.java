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
import org.codehaus.plexus.util.StringUtils;

import ro.isdc.wro.extensions.processor.support.linter.JsHint;
import ro.isdc.wro.extensions.processor.support.linter.LinterError;
import ro.isdc.wro.extensions.processor.support.linter.LinterException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Validates a JS file using JSHint
 */
public class JSHintProcessor extends DefaultProcessor {
    public static final String JSHINT_OPTIONS_KEY = "options";

    @Override
    public void processAll() throws ProcessorException {
        getLog().info("Checking sources with JsHint");
        Collection<File> files = FileUtils.listFiles(mojo.getWorkDirectory(), new String[]{"js"}, true);
        for (File file : files) {
            List<ProcessorWarning> warnings = validate(file);
            getLog().info("Found " + warnings.size() + " issues in " + file.getAbsolutePath());
            for (ProcessorWarning warning: warnings) {
                getLog().warn("In " + warning.file.getName() + " @" + warning.line + ":" + warning.character
                        + " -> " + warning.evidence + " - " + warning.reason);
            }
        }
    }

    @Override
    public boolean accept(File file) {
        return file.isFile() && isFileContainedInDirectory(file, mojo.getWorkDirectory());
    }

    @Override
    public void fileCreated(File file) throws ProcessorException {
        validate(file);
    }

    @Override
    public void fileUpdated(File file) throws ProcessorException {
        validate(file);
    }

    public List<ProcessorWarning> validate(File file) {
        List<ProcessorWarning> warnings = new ArrayList<ProcessorWarning>();
        JsHint jshint = new JsHint();
        if(this.options.containsKey(JSHINT_OPTIONS_KEY)){
            jshint.setOptions((String[])this.options.get(JSHINT_OPTIONS_KEY));
        }
        getLog().debug("JSHint-ing " + file.getAbsolutePath() + ", Encoding " + this.mojo.javaScriptEncoding);
        try {
        	if(StringUtils.isNotBlank(this.mojo.javaScriptEncoding)) {
        		jshint.validate(FileUtils.readFileToString(file, this.mojo.javaScriptEncoding));
        	} else {
        		jshint.validate(FileUtils.readFileToString(file));
        	}
        } catch (IOException e) {
            getLog().error("Can't analyze " + file.getAbsolutePath() + " with JSHint", e);
        } catch (LinterException e) {
            if (!e.getErrors().isEmpty()) {
                for (LinterError exp : e.getErrors()) {
                    if (exp == null) {
                        continue;
                    }
                    ProcessorWarning warning = new ProcessorWarning(file, exp.getLine(), exp.getCharacter(),
                            exp.getEvidence(), exp.getReason());
                    warnings.add(warning);
                }
            }
        }
        return warnings;
    }
}
