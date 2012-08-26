package org.nano.coffee.roasting.processors;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;
import org.nano.coffee.roasting.mojos.compile.CoffeeScriptCompilerMojo;
import org.nano.coffee.roasting.mojos.compile.CoffeeScriptTestCompilerMojo;
import org.nano.coffee.roasting.mojos.compile.LessCompilerMojo;

import java.io.File;

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
