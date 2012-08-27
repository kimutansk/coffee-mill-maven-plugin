package org.nano.coffee.roasting.processors;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;
import org.nano.coffee.roasting.mojos.processResources.CopyAssetsMojo;

import java.io.File;

import static org.fest.assertions.Assertions.assertThat;

public class CopyAssetProcessorTest {

    @Test
    public void testAssetCopy() throws MojoExecutionException, MojoFailureException {
        CopyAssetsMojo mojo = new CopyAssetsMojo();
        mojo.assetsDir = new File("src/test/resources/assets");
        mojo.workDir = new File("target/test/testAssetCopy-www");
        mojo.execute();

        assertThat(new File(mojo.workDir, "index.html").isFile()).isTrue();
        assertThat(new File(mojo.workDir, "img").isDirectory()).isTrue();
        assertThat(new File(mojo.workDir, "img/demo.png").exists()).isTrue();
    }

    @Test
    public void testAssetCopyWhenAssetDoesNotExist() throws MojoExecutionException, MojoFailureException {
        CopyAssetsMojo mojo = new CopyAssetsMojo();
        mojo.assetsDir = new File("src/test/resources/assets_donotexist");
        mojo.workDir = new File("target/test/testAssetCopyWhenAssetDoesNotExist-www");
        mojo.execute();

        assertThat(mojo.workDir.list()).isEmpty();
    }

}
