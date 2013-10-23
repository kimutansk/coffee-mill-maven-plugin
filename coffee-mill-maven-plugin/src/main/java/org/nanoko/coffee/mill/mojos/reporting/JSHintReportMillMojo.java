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

import org.apache.commons.io.FileUtils;
import org.apache.maven.doxia.sink.Sink;
import org.nanoko.coffee.mill.mojos.AbstractReportingCoffeeMillMojo;
import org.nanoko.coffee.mill.processors.JSHintProcessor;
import org.nanoko.coffee.mill.processors.Processor;
import org.nanoko.coffee.mill.utils.OptionsHelper;

import java.io.File;
import java.util.*;

/**
 * Builds the JSHint Report.
 *
 * @goal jshint-report
 * @phase site
 */
public class JSHintReportMillMojo extends AbstractReportingCoffeeMillMojo {

    /**
     * @parameter
     */
    private JSHintOptions jshintOptions;

    @Override
    public void writeIntroduction() {
        Sink sink = getSink();
        sink.section1();
        sink.sectionTitle1();
        sink.text("JSHint");
        sink.sectionTitle1_();
        sink.section1_();

        sink.paragraph();
        sink.link( "http://www.jshint.com" );
        sink.text( "JSHint" );
        sink.link_();
        sink.text(" is a community-driven tool to detect errors and potential problems in JavaScript code and to " +
                "enforce your team's coding conventions.\n" +
                "It is very flexible so you can easily adjust it to your particular coding guidelines and the " +
                "environment you expect your code to execute in.");
    }

    @Override
    public Map<File, List<Processor.ProcessorWarning>> validate() {
        Map<File, List<Processor.ProcessorWarning>> results = new TreeMap<File, List<Processor.ProcessorWarning>>();
        Collection<File> files = FileUtils.listFiles(getJavaScriptDir(), new String[]{"js"}, true);
        JSHintProcessor processor = new JSHintProcessor();
        Map<String,Object> options = null;

        if(jshintOptions != null){
            options = new OptionsHelper.OptionsBuilder().set(JSHintProcessor.JSHINT_OPTIONS_KEY, jshintOptions.format()).build();
        }

        processor.configure(this, options);

        for (File file : files) {
            results.put(file, processor.validate(file));
        }
        return results;
    }

    public String getOutputName() {
        return "jshint";
    }

    public String getName(Locale locale) {
        return "jshint";
    }

    public String getDescription(Locale locale) {
        return "JSHint Report of the project";
    }
}
