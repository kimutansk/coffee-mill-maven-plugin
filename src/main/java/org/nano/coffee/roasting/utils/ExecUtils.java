package org.nano.coffee.roasting.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Some helper methods related to command execution.
 */
public class ExecUtils {

    public static File findExecutableInPath(String exec) {
        // Build candidates
        List<String> candidates = new ArrayList<String>();
        candidates.add(exec);
        // Windows:
        candidates.add(exec + ".exe");
        candidates.add(exec + ".bat");
        candidates.add(exec + ".cmd");
        // Linux / Unix / MacOsX
        candidates.add(exec + ".sh");
        candidates.add(exec + ".bash");

        String systemPath = System.getenv("PATH");

        // Fast failure if we don't have the PATH defined.
        if (systemPath == null) {
            return null;
        }

        String[] pathDirs = systemPath.split(File.pathSeparator);

        for (String pathDir : pathDirs) {
            for (String candidate : candidates) {
                File file = new File(pathDir, candidate);
                if (file.isFile()) {
                    return file;
                }
            }
        }

        // Search not successful.
        return null;
    }

}
