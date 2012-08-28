package org.nano.coffee.roasting.processors;

import org.junit.Test;
import org.nano.coffee.roasting.mojos.compile.CSSCompilerMojo;
import org.nano.coffee.roasting.mojos.compile.JavaScriptCompilerMojo;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Tests the behavior of the CSSLint Processor
 */
public class CSSLintProcessorTest {

    @Test
    public void testCSSLint() throws Processor.ProcessorException {
        CSSCompilerMojo mojo = new CSSCompilerMojo();
        mojo.stylesheetsDir = new File("src/test/resources/stylesheets");
        mojo.workDir = mojo.stylesheetsDir;

        CSSLintProcessor processor = new CSSLintProcessor();
        Map<String, Object> options = new HashMap<String, Object>();
        options.put("directory", mojo.stylesheetsDir);
        processor.configure(mojo, options);

        processor.processAll();

        assertThat(processor.validate(new File(mojo.stylesheetsDir, "stuff.css")).size()).isEqualTo(2);
        assertThat(processor.validate(new File(mojo.stylesheetsDir, "clean.css")).size()).isEqualTo(0);
    }
}
