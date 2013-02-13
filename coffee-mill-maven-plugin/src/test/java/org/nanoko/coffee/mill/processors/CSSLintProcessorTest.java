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

import org.junit.Test;
import org.nanoko.coffee.mill.mojos.compile.CSSCompilerMojo;

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
