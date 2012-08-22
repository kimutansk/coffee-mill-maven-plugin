package org.nano.coffee.roasting.mojos;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.nano.coffee.roasting.mojos.AbstractRoastingCoffeeMojo;
import ro.isdc.wro.extensions.processor.support.linter.JsHint;
import ro.isdc.wro.extensions.processor.support.linter.JsLint;
import ro.isdc.wro.extensions.processor.support.linter.LinterError;
import ro.isdc.wro.extensions.processor.support.linter.LinterException;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * Copy src/main/www and src/main/resources to the www directory
 *
 * @goal copy-assets
 *
 */
public class CopyAssetsMojo extends AbstractRoastingCoffeeMojo {


    public void execute() throws MojoExecutionException, MojoFailureException {
        copyAssets();
    }

    private void copyAssets() throws MojoFailureException {
        // Copy using the filter
        try {
            FileUtils.copyDirectory(assetsDir, getWorkDirectory());
            System.out.println("BOOH");
        } catch (IOException e) {
            throw new MojoFailureException("", e);
        }
    }

}
