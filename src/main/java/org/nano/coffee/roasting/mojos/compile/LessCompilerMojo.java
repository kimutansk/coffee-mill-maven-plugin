package org.nano.coffee.roasting.mojos.compile;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.nano.coffee.roasting.mojos.AbstractRoastingCoffeeMojo;
import ro.isdc.wro.extensions.processor.support.less.LessCss;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * Compiles Less files.
 *
 * @goal compile-less
 *
 */
public class LessCompilerMojo extends AbstractRoastingCoffeeMojo {


    public void execute() throws MojoExecutionException, MojoFailureException {
        if (! stylesheetsDir.exists()) {
            getLog().debug("The stylesheet directory does not exist - skipping LESS compilation");
            return;
        }

        Collection<File> files = FileUtils.listFiles(stylesheetsDir, new String[] {"less"}, true);
        for (File file : files) {
            compile(file);
        }
        getLog().info(files.size() + " Less file(s) compiled");
    }

    private void compile(File file) throws MojoFailureException {
        getLog().info("Compiling " + file.getAbsolutePath());
        LessCss compiler = new LessCss();
        String cssFileName = file.getName().substring(0, file.getName().length() - ".less".length()) + ".css";
        try {
            File out = new File(getWorkDirectory(), cssFileName);
            String output = compiler.less(FileUtils.readFileToString(file));
            FileUtils.write(out, output);
        } catch (IOException e) {
            throw new MojoFailureException("Cannot compile " + file.getAbsolutePath(), e);
        }
    }

}
