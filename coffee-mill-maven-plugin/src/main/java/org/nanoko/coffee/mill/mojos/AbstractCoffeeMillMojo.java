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

package org.nanoko.coffee.mill.mojos;


import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

import java.io.File;

public abstract class AbstractCoffeeMillMojo extends AbstractMojo {

    /**
     * The maven project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    public MavenProject project;

    /**
     * Maven ProjectHelper.
     *
     * @component
     * @readonly
     */
    public MavenProjectHelper projectHelper;


    /**
     * The plugin dependencies.
     * @parameter default-value="${plugin.artifacts}"
     */
    public java.util.List<Artifact> pluginDependencies;

    /**
     * Directory containing the build files.
     * @parameter expression="${project.build.directory}"
     */
    public File buildDirectory;

    /**
     * Base directory of the project.
     * @parameter expression="${basedir}"
     */
    public File baseDirectory;

    /**
     * Where are JavaScript files.
     *
     * @parameter default-value="src/main/js"
     */
    public File javaScriptDir;

    /**
     * Where are CoffeeScript files.
     *
     * @parameter default-value="src/main/coffee"
     */
    public File coffeeScriptDir;

    /**
     * Where are CoffeeScript files implementing tests.
     *
     * @parameter default-value="src/test/coffee"
     */
    public File coffeeScriptTestDir;

    /**
     * Where are JavaScript files implementing tests.
     *
     * @parameter default-value="src/test/js"
     */
    public File javaScriptTestDir;

    /**
     * Where are the assets.
     *
     * @parameter default-value="src/main/www"
     */
    public File assetsDir;

    /**
     * Where are LESS, CSS and SASS/SCSS files.
     *
     * @parameter default-value="src/main/stylesheets"
     */
    public File stylesheetsDir;

    /**
     * Where are the output files written.
     *
     * @parameter default-value="target/www"
     */
    public File workDir;

    /**
     * Where are the output test files written.
     *
     * @parameter default-value="target/www-test"
     */
    public File workTestDir;

    /**
     * Where are the dependencies copies.
     *
     * @parameter default-value="target/libs"
     */
    public File libDir;


    public File getTarget() {
        return new File(project.getBuild().getDirectory());
    }

    public File getWorkDirectory() {
        workDir.mkdirs();
        return workDir;
    }

    public File getWorkTestDirectory() {
        workTestDir.mkdirs();
        return workTestDir;
    }

    public File getLibDirectory() {
        return libDir;
    }

    public File getJavaScriptDir() {
        return javaScriptDir;
    }

    public File getStylesheetsDir() {
        return stylesheetsDir;
    }
}
