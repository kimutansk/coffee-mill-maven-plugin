package org.nanoko.coffee.mill.processors;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Aggregates JavaScript files
 */
public class CSSAggregator extends AggregatorProcessor {

    public void separator(OutputStream out) throws IOException {
        out.write('\n');
    }
}
