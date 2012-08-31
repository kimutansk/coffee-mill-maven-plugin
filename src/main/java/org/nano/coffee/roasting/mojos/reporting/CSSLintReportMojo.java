package org.nano.coffee.roasting.mojos.reporting;

import org.apache.commons.io.FileUtils;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.reporting.MavenReportException;
import org.nano.coffee.roasting.mojos.AbstractReportingRoastingCoffeeMojo;
import org.nano.coffee.roasting.processors.CSSLintProcessor;
import org.nano.coffee.roasting.processors.JSHintProcessor;
import org.nano.coffee.roasting.processors.Processor;
import org.nano.coffee.roasting.utils.OptionsHelper;

import java.io.File;
import java.util.*;

/**
 * Builds the CSSLint Report.
 *
 * @goal csslint-report
 * @phase site
 */
public class CSSLintReportMojo extends AbstractReportingRoastingCoffeeMojo {

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
        Collection<File> files = FileUtils.listFiles(getWorkDirectory(), new String[]{"css"}, true);
        CSSLintProcessor processor = new CSSLintProcessor();
        processor.configure(this, new OptionsHelper.OptionsBuilder().set("directory", getWorkDirectory()).build());
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
