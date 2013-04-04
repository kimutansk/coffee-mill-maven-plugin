package org.nanoko.coffee.mill.processors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Utility class for capturing sass/scss/compass compilation errors in ruby.
 */
public class SassCompilationErrors implements Iterable<SassCompilationErrors.CompilationError> {
    final List<CompilationError> errors = new ArrayList<CompilationError>();

    public void add(String file, String message) {
        errors.add(new CompilationError(file, message));
    }

    public boolean hasErrors() {
        return errors.size() > 0;
    }

    public Iterator<CompilationError> iterator() {
        return errors.iterator();
    }

    public class CompilationError {
        final String filename;
        final String message;

        CompilationError(String filename, String message) {
            this.filename = filename;
            this.message = message;
        }
    }
}
