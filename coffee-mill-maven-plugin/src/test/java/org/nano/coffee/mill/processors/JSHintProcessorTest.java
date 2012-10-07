package org.nano.coffee.mill.processors;

import org.junit.Test;
import org.nano.coffee.mill.mojos.compile.JavaScriptCompilerMojo;

import java.io.File;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Tests the behavior of the JSHintProcessor
 */
public class JSHintProcessorTest {

    @Test
    public void testJSHint() throws Processor.ProcessorException {
        JavaScriptCompilerMojo mojo = new JavaScriptCompilerMojo();
        mojo.javaScriptDir = new File("src/test/resources/js");
        mojo.workDir = mojo.javaScriptDir;

        JSHintProcessor processor = new JSHintProcessor();
        processor.configure(mojo, null);

        processor.processAll();

        assertThat(processor.validate(new File(mojo.javaScriptDir, "sample/test.js")).size()).isEqualTo(1);
    }
}
