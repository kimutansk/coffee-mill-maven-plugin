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
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;
import org.nanoko.coffee.mill.mojos.compile.LessCompilerMojo;

import java.io.File;
import java.io.IOException;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.Fail.fail;

public class LessCompilationProcessorTest {

    @Test
    public void testLessCompilation() throws MojoExecutionException, MojoFailureException {
        LessCompilerMojo mojo = new LessCompilerMojo();
        mojo.nodeVersion = "0.10.13";
        mojo.npmVersion = "1.3.6";
        mojo.stylesheetsDir = new File("src/test/resources/stylesheets");
        mojo.workDir = new File("target/test/testLessCompilation-www");
        mojo.execute();

        assertThat(new File(mojo.workDir, "style.css").isFile()).isTrue();
        assertThat(new File(mojo.workDir, "site").isDirectory()).isTrue();
        assertThat(new File(mojo.workDir, "site/site.css").isFile()).isTrue();
    }

    /**
     * Checks that the less compiler does not generate '#NaNbbaaNaN00NaN00NaN00NaN00NaN'
     */
    @Test
    public void testWeirdBugInLessCompilation() throws MojoExecutionException, MojoFailureException, IOException {

        LessCompilerMojo mojo = new LessCompilerMojo();
        mojo.stylesheetsDir = new File("src/test/resources/stylesheets");
        mojo.workDir = new File("target/test/testLessCompilation-www2");
        mojo.nodeVersion = "0.10.13";
        mojo.npmVersion = "1.3.6";
        mojo.execute();

        assertThat(new File(mojo.workDir, "forum.css").isFile()).isTrue();

        String content = FileUtils.readFileToString(new File(mojo.workDir, "forum.css"));
        assertThat(content).doesNotContain("#NaNbbaaNaN00NaN00NaN00NaN00NaN");
    }

    @Test
    public void testInvalidLessFile() throws IOException {
        LessCompilerMojo mojo = new LessCompilerMojo();
        mojo.stylesheetsDir = new File("target/test/junk");
        mojo.workDir = new File("target/test/testLessCompilation-www");
        mojo.nodeVersion = "0.10.13";
        mojo.npmVersion = "1.3.6";

        File error = new File(mojo.stylesheetsDir, "error.less");
        FileUtils.write(error, "this is not less");

        LessCompilationProcessor processor = mojo.getProcessor();
        try {
            processor.compile(error);
            fail("Less compilation should have failed");
        } catch (Processor.ProcessorException e) {
            System.out.println(e.getMessage());
            // OK.
        }
    }
}
