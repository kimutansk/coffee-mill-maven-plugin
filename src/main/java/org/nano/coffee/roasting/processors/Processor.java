package org.nano.coffee.roasting.processors;

import org.nano.coffee.roasting.mojos.AbstractRoastingCoffeeMojo;

import java.io.File;
import java.util.Map;

public interface Processor {


    public void configure(AbstractRoastingCoffeeMojo mojo, Map<String, Object> options);

    public void processAll() throws ProcessorException;

    public void tearDown();

    public boolean accept(File file);

    public void fileCreated(File file) throws ProcessorException;

    public void fileUpdated(File file) throws ProcessorException;

    public void fileDeleted(File file) throws ProcessorException;

    class ProcessorException extends Exception {
        public ProcessorException(String message) {
            super(message);
        }
        public ProcessorException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
