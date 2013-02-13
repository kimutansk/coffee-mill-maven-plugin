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

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;
import org.nanoko.coffee.mill.mojos.others.WatchMojo;

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
