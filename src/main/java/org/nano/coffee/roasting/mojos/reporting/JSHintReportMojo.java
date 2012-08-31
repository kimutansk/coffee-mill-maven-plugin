package org.nano.coffee.roasting.mojos.reporting;

import org.apache.commons.io.FileUtils;
import org.apache.maven.doxia.sink.Sink;
import org.nano.coffee.roasting.mojos.AbstractReportingRoastingCoffeeMojo;
import org.nano.coffee.roasting.processors.JSHintProcessor;
import org.nano.coffee.roasting.processors.Processor;

import java.io.File;
import java.util.*;

/**
 * Builds the JSHint Report.
 *
 * @goal jshint-report
 * @phase site
 */
public class JSHintReportMojo extends AbstractReportingRoastingCoffeeMojo {

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
        Collection<File> files = FileUtils.listFiles(getWorkDirectory(), new String[]{"js"}, true);
        JSHintProcessor processor = new JSHintProcessor();
        processor.configure(this, null);
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
