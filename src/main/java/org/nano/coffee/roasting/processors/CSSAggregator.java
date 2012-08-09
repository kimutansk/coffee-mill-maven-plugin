package org.nano.coffee.roasting.processors;

import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Aggregates JavaScript files
 */
public class CSSAggregator extends AggregatorProcessor {

    private File work;
    private List<String> names;
    private File output;

    public CSSAggregator(File workDirectory, File output, List<String> names) {
        this.work = workDirectory;
        this.names = names;
        this.output = output;
    }

    public void process() throws MojoExecutionException {
        List<File> files = computeFileList(names, work, "css", true);
        try {
            aggregate(files, output);
        } catch (FileNotFoundException e) {
            throw new MojoExecutionException("Cannot build aggregate file", e);
        }
    }

    public void separator(OutputStream out) throws IOException {
        out.write('\n');
    }
}
