package org.nano.coffee.roasting.processors;

import java.io.File;
import java.util.Map;

public interface Processor {


    public void process(File input, Map<String, ?> options) throws ProcessorException;

    public void tearDown();

    public boolean accept(File file);

    class ProcessorException extends Exception {
        public ProcessorException(String message) {
            super(message);
        }
        public ProcessorException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
