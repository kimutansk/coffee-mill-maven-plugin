package org.nano.coffee.roasting.mojos;


import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

import java.io.File;

public abstract class AbstractRoastingCoffeeMojo extends AbstractMojo {

    /**
     * The maven project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    /**
     * Maven ProjectHelper.
     *
     * @component
     * @readonly
     */
    protected MavenProjectHelper projectHelper;

    /**
     * Where are JavaScript files.
     *
     * @parameter default-value="src/main/js"
     */
    protected File javaScriptDir;

    /**
     * Where are CoffeeScript files.
     *
     * @parameter default-value="src/main/coffee"
     */
    protected File coffeeScriptDir;

    /**
     * Where are CoffeeScript files implementing tests.
     *
     * @parameter default-value="src/test/coffee"
     */
    protected File coffeeScriptTestDir;

    /**
     * Where are JavaScript files implementing tests.
     *
     * @parameter default-value="src/test/js"
     */
    protected File javaScriptTestDir;

    /**
     * Where are LESS, CSS and SASS/SCSS files.
     *
     * @parameter default-value="src/main/stylesheets"
     */
    protected File stylesheetsDir;


    public static String TARGET_DIR_NAME = "target";

    public File getTarget() {
        return new File(project.getBasedir(), TARGET_DIR_NAME);
    }

    public File getWorkDirectory() {
        File work = new File(getTarget(), "work");
        work.mkdirs();
        return work;
    }

    public File getWorkTestDirectory() {
        File work = new File(getTarget(), "work-test");
        work.mkdirs();
        return work;
    }
}
