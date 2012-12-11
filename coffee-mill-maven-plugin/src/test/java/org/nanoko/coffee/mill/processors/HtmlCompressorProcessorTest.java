package org.nanoko.coffee.mill.processors;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;
import org.nanoko.coffee.mill.mojos.packaging.HtmlCompressorMojo;
import org.nanoko.coffee.mill.mojos.processResources.CopyAssetsMojo;

import java.io.File;

import static org.junit.Assert.assertTrue;

public class HtmlCompressorProcessorTest {

    @Test
    public void testHTMLCompression() throws MojoExecutionException, MojoFailureException {
        CopyAssetsMojo mojo1 = new CopyAssetsMojo();
        mojo1.assetsDir = new File("src/test/resources/assets");
        mojo1.workDir = new File("target/test/testHTMLCompression-www");
        mojo1.execute();

        HtmlCompressorMojo mojo = new HtmlCompressorMojo();
        mojo.assetsDir = new File("src/test/resources/assets");
        mojo.workDir = new File("target/test/testHTMLCompression-www");

        mojo.skipHtmlCompressor = false;
        mojo.htmlCompressionpPreserveLineBreak = false;
        mojo.htmlCompressionGenerateStatistics = true;
        mojo.htmlCompressionRemoveComments = true;
        mojo.htmlCompressionRemoveFormAttributes = true;
        mojo.htmlCompressionRemoveHttpProtocol = true;
        mojo.htmlCompressionRemoveHttpsProtocol = true;
        mojo.htmlCompressionRemoveInputAttributes = true;
        mojo.htmlCompressionRemoveIntertagSpaces = true;
        mojo.htmlCompressionRemoveJavaScriptProtocol = true;
        mojo.htmlCompressionRemoveLinkAttributes = true;
        mojo.htmlCompressionRemoveMultiSpaces = true;
        mojo.htmlCompressionRemoveQuotes = true;
        mojo.htmlCompressionRemoveScriptAttributes = true;
        mojo.htmlCompressionRemoveStyleAttributes = true;
        mojo.htmlCompressionSimpleBooleanAttributes = true;
        mojo.htmlCompressionSimpleDoctype = true;

        File file = new File(mojo.assetsDir, "lemonde/le-monde.html");
        long size = file.length();

        mojo.execute();

        file = new File(mojo.workDir, "lemonde/le-monde.html");
        long newSize = file.length();

        // Optimization, so the new size is smaller.
        assertTrue(newSize < size);
    }

}
