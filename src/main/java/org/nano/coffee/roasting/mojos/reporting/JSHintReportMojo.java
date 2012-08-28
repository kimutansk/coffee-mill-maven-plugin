package org.nano.coffee.roasting.mojos.reporting;

import org.apache.commons.io.FileUtils;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.reporting.MavenReportException;
import org.nano.coffee.roasting.mojos.AbstractReportingRoastingCoffeeMojo;
import org.nano.coffee.roasting.processors.JSHintProcessor;
import org.nano.coffee.roasting.processors.Processor;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * Builds the JSHint Report.
 *
 * @goal jshint-report
 * @phase site
 */
public class JSHintReportMojo extends AbstractReportingRoastingCoffeeMojo {

    @Override
    protected void executeReport(Locale locale) throws MavenReportException {
        // Collect files
        Collection<File> files = FileUtils.listFiles(getWorkDirectory(), new String[]{"js"}, true);
        // Execute jshint
        JSHintProcessor processor = new JSHintProcessor();
        processor.configure(this, null);

        Sink sink = getSink();
        sink.head();
        sink.title();
        sink.text("JSHint Report");
        sink.title_();
        sink.head_();

        sink.body();

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

        for (File file : files) {
            List<Processor.ProcessorWarning> warnings = processor.validate(file);
            sink.section2();
            sink.sectionTitle2();
            sink.text(file.getName());
            sink.sectionTitle2_();
            if (warnings.size() == 0) {
                sink.list();
                sink.listItem();
                sink.text("No warnings detected");
                sink.listItem_();
                sink.list_();
            } else {
                for (Processor.ProcessorWarning warning : warnings) {
                    sink.list();
                    sink.listItem();
                    sink.text(warning.line + ":" + warning.character + " -> " + warning.reason + " (" + warning
                            .evidence + ")");
                    sink.listItem_();
                    sink.list_();
                }
            }
            sink.section2_();
        }


        sink.body_();

        sink.flush();

        sink.close();
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
