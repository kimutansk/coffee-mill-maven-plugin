/*
 * Copyright 2013 OW2 Nanoko Project
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.nanoko.coffee.mill.utils;

import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.util.HashMap;
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

    public static boolean getBoolean(Map<String, Object> options, String name, boolean defaultValue) {
        if (options == null) {
            return defaultValue;
        }

        Object value = options.get(name);
        if (value == null  || ! (value instanceof Boolean)) {
            return defaultValue;
        } else {
            return (Boolean) value;
        }
    }

    public static int getInteger(Map<String, Object> options, String name, int defaultValue) {
        if (options == null) {
            return defaultValue;
        }

        Object value = options.get(name);
        if (value == null  || ! (value instanceof Integer)) {
            return defaultValue;
        } else {
            return (Integer) value;
        }
    }

    public static class OptionsBuilder {
        Map<String, Object> options = new HashMap<String, Object>();

        public OptionsBuilder set(String key, Object object) {
            options.put(key, object);
            return this;
        }

        public Map<String, Object> build() {
            return options;
        }
    }
}
