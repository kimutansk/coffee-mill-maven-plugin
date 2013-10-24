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

package org.nanoko.coffee.mill.mojos.reporting;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.maven.doxia.sink.Sink;
import org.nanoko.coffee.mill.mojos.AbstractReportingCoffeeMillMojo;
import org.nanoko.coffee.mill.processors.CSSLintProcessor;
import org.nanoko.coffee.mill.processors.Processor;
import org.nanoko.coffee.mill.utils.OptionsHelper;

/**
 * Builds the CSSLint Report.
 *
 * @goal csslint-report
 * @phase site
 */
public class CSSLintReportMillMojo extends AbstractReportingCoffeeMillMojo {

    @Override
    public void writeIntroduction() {
        Sink sink = getSink();
        sink.section1();
        sink.sectionTitle1();
        sink.text("CSSLint");
        sink.sectionTitle1_();
        sink.section1_();

        sink.paragraph();
        sink.link( "http://csslint.net" );
        sink.text( "CSSLint" );
        sink.link_();
        sink.text(" is an open source CSS code quality tool originally written by Nicholas C. Zakas and Nicole " +
                "Sullivan. It was released in June 2011 at the Velocity conference.\n" +
                "A lint tool performs static analysis of source code and flags patterns that might be errors or " +
                "otherwise cause problems for the developer.");
    }

    @Override
    public Map<File, List<Processor.ProcessorWarning>> validate() throws Processor.ProcessorException {
        Map<File, List<Processor.ProcessorWarning>> results = new TreeMap<File, List<Processor.ProcessorWarning>>();
        Collection<File> files = FileUtils.listFiles(getStylesheetsDir(), new String[]{"css"}, true);
        CSSLintProcessor processor = new CSSLintProcessor();
        processor.configure(this, new OptionsHelper.OptionsBuilder().set("directory", getStylesheetsDir()).build());
        for (File file : files) {
            results.put(file, processor.validate(file));
        }
        return results;
    }

    public String getOutputName() {
        return "csslint";
    }

    public String getName(Locale locale) {
        return "csslint";
    }

    public String getDescription(Locale locale) {
        return "CSSLint Report of the project";
    }
}
