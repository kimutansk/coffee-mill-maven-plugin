package org.nano.coffee.mill.processors;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;
import org.nano.coffee.mill.mojos.compile.LessCompilerMojo;

import java.io.File;
import java.io.IOException;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.Fail.fail;

public class LessCompilationProcessorTest {

    @Test
    public void testLessCompilation() throws MojoExecutionException, MojoFailureException {
        LessCompilerMojo mojo = new LessCompilerMojo();
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
        mojo.workDir = new File("target/test/testLessCompilation-www");
        mojo.execute();

        assertThat(new File(mojo.workDir, "forum.css").isFile()).isTrue();

        String content = FileUtils.readFileToString(new File(mojo.workDir, "forum.css"));
        assertThat(content).doesNotContain("#NaNbbaaNaN00NaN00NaN00NaN00NaN");
    }

    @Test
    public void testInvalidLessFile() {
        LessCompilerMojo mojo = new LessCompilerMojo();
        mojo.stylesheetsDir = new File("src/test/resources/stylesheets");
        mojo.workDir = new File("target/test/testLessCompilation-www");

        LessCompilationProcessor processor = mojo.getProcessor();
        try {
            processor.less("this is not less");
            fail("Less compilation should have failed");
        } catch (Processor.ProcessorException e) {
            // OK.
        }
    }
}
