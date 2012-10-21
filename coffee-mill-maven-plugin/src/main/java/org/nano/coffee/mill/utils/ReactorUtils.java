package org.nano.coffee.mill.utils;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;
import org.nano.coffee.mill.mojos.others.WatchMojo;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods to handle reactor options.
 */
public class ReactorUtils {

    public static Plugin getCoffeeMillPlugin(MavenProject project) {
        List<Plugin> plugins = project.getBuild().getPlugins();
        for (Plugin plugin : plugins) {
            System.out.println(plugin.getArtifactId());
            if ("coffee-mill-maven-plugin".equals(plugin.getArtifactId())) {
                return plugin;
            }
        }
        return null;
    }

    public static void addWatcherToSession(WatchMojo watcher, MavenSession session) {
        List<WatchMojo> watchers = (List<WatchMojo>) session.getExecutionProperties().get("watchers");
        if (watchers == null) {
            watchers = new ArrayList<WatchMojo>();
            session.getExecutionProperties().put("watchers", watchers);
        }
        watchers.add(watcher);
    }

    public static List<WatchMojo> getWatchersFromSession(MavenSession session) {
        List<WatchMojo> watchers = (List<WatchMojo>) session.getExecutionProperties().get("watchers");
        if (watchers == null) {
            return new ArrayList<WatchMojo>();
        } else {
            return watchers;
        }
    }

}
