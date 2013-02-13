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
import org.nanoko.coffee.mill.mojos.compile.JavaScriptCompilerMojo;

import java.io.File;

import static org.fest.assertions.Assertions.assertThat;

public class DustTemplateCompilationProcessorTest {

    @Test
    public void testDustCompilation() throws MojoExecutionException, MojoFailureException {
        JavaScriptCompilerMojo mojo = new JavaScriptCompilerMojo();
        mojo.javaScriptDir = new File("src/test/resources/js");
        mojo.workDir = new File("target/test/testDustCompilation-www");
        mojo.execute();

        assertThat(new File(mojo.workDir, "sample/templates/mytemplate.js").isFile()).isTrue();
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
