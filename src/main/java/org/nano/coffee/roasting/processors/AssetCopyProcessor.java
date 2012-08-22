package org.nano.coffee.roasting.processors;

import org.apache.commons.io.FileUtils;
import org.nano.coffee.roasting.utils.OptionsHelper;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Just copy JavaScript files to an output directory.
 */
public class AssetCopyProcessor implements Processor {


    public void process(File input, Map<String, ?> options) throws ProcessorException {
        File assets = OptionsHelper.getDirectory(options, "assets", false);
        if (assets == null) {
            throw new ProcessorException("Cannot copy file - assets parameter missing");
        }

        File output = OptionsHelper.getDirectory(options, "output", true);
        if (output == null) {
            throw new ProcessorException("Cannot copy file - output parameter missing");
        }

        try {
            String path = input.getAbsolutePath();
            if (! path.contains(assets.getAbsolutePath())) {
                return;
            }
            String relativePath = path.substring(assets.getAbsolutePath().length());
            File f = new File(output, relativePath);
            if (f.getParentFile() != null) {
                f.getParentFile().mkdirs();
                FileUtils.copyFileToDirectory(input,  f.getParentFile());
            } else {
                throw new ProcessorException("Cannot copy file - parent directory not accessible for "
                        + input.getAbsolutePath());
            }

        } catch (IOException e) {
            throw new ProcessorException("Cannot copy file " + input.getName(), e);
        }

    }

    public void tearDown() {
        // Do nothing.
    }

    /**
     * Accept all but will manage only file from the asset folder.
     * @param file
     * @return
     */
    public boolean accept(File file) {
        return true;
    }

}
