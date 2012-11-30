package org.nanoko.coffee.mill.mojos;


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
}
