package org.nano.coffee.mill.processors;

import org.apache.commons.io.FileUtils;
import ro.isdc.wro.extensions.processor.support.linter.JsLint;
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
public class JSLintProcessor extends DefaultProcessor {

    @Override
    public void processAll() throws ProcessorException {
        getLog().info("Checking sources with JsLint");
        Collection<File> files = FileUtils.listFiles(millMojo.getWorkDirectory(), new String[]{"js"}, true);
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
        return file.isFile() && isFileContainedInDirectory(file, millMojo.getWorkDirectory());
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
        JsLint jslint = new JsLint();
        getLog().debug("JSLint-ing " + file.getAbsolutePath());
        try {
            jslint.validate(FileUtils.readFileToString(file));
        } catch (IOException e) {
            getLog().error("Can't analyze " + file.getAbsolutePath() + " with JSLint", e);
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
