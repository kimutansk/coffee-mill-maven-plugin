package org.nano.coffee.roasting.processors;

import org.apache.commons.io.FileUtils;
import org.mozilla.javascript.RhinoException;
import org.nano.coffee.roasting.utils.OptionsHelper;
import ro.isdc.wro.extensions.processor.support.coffeescript.CoffeeScript;
import ro.isdc.wro.extensions.script.RhinoScriptBuilder;
import ro.isdc.wro.util.WroUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Just copy JavaScript files to an output directory.
 */
public class JavaScriptFileCopyProcessor implements Processor {


    public void process(File input, Map<String, ?> options) throws ProcessorException {
        File output = OptionsHelper.getDirectory(options, "output", true);
        if (output == null) {
            throw new ProcessorException("Cannot copy file - output parameter missing");
        }

        try {
            FileUtils.copyFileToDirectory(input, output);
        } catch (IOException e) {
            throw new ProcessorException("Cannot copy file " + input.getName(), e);
        }

    }

    public void tearDown() {
        // Do nothing.
    }

    public boolean accept(File file) {
        return file.getName().endsWith(".js")  && file.isFile();
    }

}
