package org.nanoko.coffee.mill.mojos.packaging;

import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Build;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Checks the behavior of StylesheetsAggregatorMojo
 */
public class StylesheetsAggregatorMojoTest {

    /**
     * Checks the fix for Issue-1 : Null Pointer while aggregating CSS.
     *
     * We have a CSS file in target/www (imported dependency)
     * We don't have any other files anywhere in the project
     * The aggregation was failing while it should not.
     */
    @Test
    public void testWhenACSSIsImportedAndNoStylesheetsAreInTheProject() throws IOException, MojoExecutionException, MojoFailureException {
        String basedir = "target/test/testWhenACSSIsImportedAndNoStylesheetsAreInTheProject";
        StylesheetsAggregatorMojo mojo = new StylesheetsAggregatorMojo();

        mojo.stylesheetsDir = new File("src/test/resources/css_donotexist");
        mojo.javaScriptDir = new File("src/test/resources/js_donotexist");
        mojo.workDir = new File(basedir + "/www");

        mojo.workDir.mkdirs();
        // Copying a file in the work dir.
        FileUtils.copyFileToDirectory(new File("src/test/resources/stylesheets/clean.css"), mojo.workDir);

        mojo.project = mock(MavenProject.class);
        mojo.projectHelper = mock(MavenProjectHelper.class);

        mojo.setLog(new SystemStreamLog());
        Build build = mock(Build.class);
        Artifact artifact = mock(Artifact.class);

        when(mojo.project.getBasedir()).thenReturn(new File
                (basedir));
        when(mojo.project.getBuild()).thenReturn(build);
        when(mojo.project.getArtifact()).thenReturn(artifact);
        when(build.getDirectory()).thenReturn(basedir);
        when((build.getFinalName())).thenReturn("test");

       mojo.execute();
    }


}
