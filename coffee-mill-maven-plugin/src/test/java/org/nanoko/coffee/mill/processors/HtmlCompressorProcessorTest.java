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

package org.nanoko.coffee.mill.processors;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;
import org.nanoko.coffee.mill.mojos.compile.HtmlCompressorMojo;
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
        mojo.htmlCompressionPreserveLineBreak = false;
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
