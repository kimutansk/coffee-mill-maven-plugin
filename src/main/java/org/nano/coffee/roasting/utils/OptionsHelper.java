package org.nano.coffee.roasting.utils;

import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.util.Map;

/**
 * Helper methods to handle processors options.
 */
public class OptionsHelper {

    public static String getString(Map<String, ?> option, String name) {
        Object value = option.get(name);
        if (value == null) {
            return null;
        }
        return value.toString();
    }


    public static File getFile(Map<String, ?> option, String name) {
        Object value = option.get(name);
        if (value == null) {
            return null;
        }
        if (value instanceof File) {
            return (File) value;
        } else if (value instanceof String) {
            return new File((String) value);
        }
        return null;
    }

    public static File getDirectory(Map<String, ?> option, String name, boolean create) {
        Object value = option.get(name);
        if (value == null) {
            return null;
        }
        if (value instanceof File) {
            if (((File) value).isDirectory()) {
                return (File) value;
            } else {
                if (create) {
                    ((File) value).mkdirs();
                }
                return (File) value;
            }
        } else if (value instanceof String) {
            File file = new File((String) value);
            if (create) {
                file.mkdirs();
            }
            return file;
        }
        return null;
    }

    public static Log getLogger(Map<String, ?> options, String name) {
        Object value = options.get(name);
        if (value != null  && value instanceof Log) {
            return (Log) value;
        }
        return null;
    }
}
