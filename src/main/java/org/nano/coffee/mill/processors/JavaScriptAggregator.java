package org.nano.coffee.mill.processors;

import java.io.*;

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
