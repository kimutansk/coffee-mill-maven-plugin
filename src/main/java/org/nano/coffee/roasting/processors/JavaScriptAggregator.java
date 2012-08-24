package org.nano.coffee.roasting.processors;

import org.apache.maven.plugin.MojoExecutionException;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * Aggregates JavaScript files
 */
public class JavaScriptAggregator extends AggregatorProcessor {

    @Override
    public void separator(OutputStream out) throws IOException {
        out.write(';');
        out.write('\n');
    }
}
