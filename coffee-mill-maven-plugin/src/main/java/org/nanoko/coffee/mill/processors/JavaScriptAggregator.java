package org.nanoko.coffee.mill.processors;

import java.io.IOException;
import java.io.OutputStream;

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
