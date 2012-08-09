package org.nano.coffee.roasting.processors;

import org.apache.maven.plugin.MojoExecutionException;

import java.io.*;
import java.util.List;

/**
 * Aggregates JavaScript files
 */
public class JavaScriptAggregator extends AggregatorProcessor {

    private File work;
    private List<String> names;
    private File output;

    public JavaScriptAggregator(File workDirectory, File output, List<String> names) {
        this.work = workDirectory;
        this.names = names;
        this.output = output;
    }

    public void process() throws MojoExecutionException {
        List<File> files = computeFileList(names, work, "js", true);
        try {
            aggregate(files, output);
        } catch (FileNotFoundException e) {
            throw new MojoExecutionException("Cannot build aggregate file", e);
        }
    }


    @Override
    public void separator(OutputStream out) throws IOException {
        out.write(';');
        out.write('\n');
    }
}
