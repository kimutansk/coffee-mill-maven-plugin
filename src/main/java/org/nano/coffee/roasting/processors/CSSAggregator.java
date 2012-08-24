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

    public void separator(OutputStream out) throws IOException {
        out.write('\n');
    }
}
