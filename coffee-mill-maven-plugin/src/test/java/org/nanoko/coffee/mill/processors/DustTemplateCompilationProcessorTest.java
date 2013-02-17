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

import org.apache.commons.io.FileUtils;
import org.apache.commons.vfs2.FileUtil;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;
import org.nanoko.coffee.mill.mojos.compile.JavaScriptCompilerMojo;

import java.io.File;
import java.io.IOException;

import static org.fest.assertions.Assertions.assertThat;

public class DustTemplateCompilationProcessorTest {

    @Test
    public void testDustCompilation() throws MojoExecutionException, MojoFailureException {
        JavaScriptCompilerMojo mojo = new JavaScriptCompilerMojo();
        mojo.javaScriptDir = new File("src/test/resources/js");
        mojo.workDir = new File("target/test/testDustCompilation-www");
        mojo.execute();
        File result = new File(mojo.workDir, "sample/templates/mytemplate.js");

        assertThat(result.isFile()).isTrue();

        //check the compiled template name is set to the file name
        //i.e mytemplate is this test
        try {
            assertThat(FileUtils.readFileToString(result)
                    .startsWith("(function(){dust.register(\"mytemplate\"")).isTrue();
        } catch (IOException e) {
            //we already have check that the file does exist
        }
    }

    @Test
    public void testWhenJavaScriptDirectoryDoesNotExist() throws MojoExecutionException,
            MojoFailureException {
        JavaScriptCompilerMojo mojo = new JavaScriptCompilerMojo();
        mojo.javaScriptDir = new File("src/test/resources/does_not_exist");
        mojo.workDir = new File("target/test/testDustCompilation-www");
        mojo.execute();
    }
}
