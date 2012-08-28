package org.nano.coffee.roasting.mojos.reporting;

import org.apache.commons.io.FileUtils;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.reporting.MavenReportException;
import org.nano.coffee.roasting.mojos.AbstractReportingRoastingCoffeeMojo;
import org.nano.coffee.roasting.processors.CSSLintProcessor;
import org.nano.coffee.roasting.processors.Processor;
import org.nano.coffee.roasting.utils.OptionsHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * Builds the CSSLint Report.
 *
 * @goal csslint-report
 * @phase site
 */
public class CSSLintReportMojo extends AbstractReportingRoastingCoffeeMojo {

    @Override
    protected void executeReport(Locale locale) throws MavenReportException {
        // Collect files
        Collection<File> files = FileUtils.listFiles(getWorkDirectory(), new String[]{"css"}, true);
        // Execute csslint
        CSSLintProcessor processor = new CSSLintProcessor();
        processor.configure(this, new OptionsHelper.OptionsBuilder().set("directory", getWorkDirectory()).build());

        Sink sink = getSink();
        sink.head();
        sink.title();
        sink.text("CSSLint Report");
        sink.title_();
        sink.head_();

        sink.body();

        sink.section1();
        sink.sectionTitle1();
        sink.text("CSSLint");
        sink.sectionTitle1_();
        sink.section1_();

        sink.paragraph();
        sink.link( "http://csslint.net" );
        sink.text( "CSSLint" );
        sink.link_();
        sink.text(" CSS Lint is an open source CSS code quality tool originally written by Nicholas C. Zakas and Nicole " +
                "Sullivan. It was released in June 2011 at the Velocity conference.\n" +
                "A lint tool performs static analysis of source code and flags patterns that might be errors or " +
                "otherwise cause problems for the developer.");

        for (File file : files) {
            List<Processor.ProcessorWarning> warnings = new ArrayList<Processor.ProcessorWarning>();
            try {
                warnings = processor.validate(file);
            } catch (Processor.ProcessorException e) {
                getLog().error("Processor exception while CSSLinting " + file.getName(), e);
            }

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
        return "csslint";
    }

    public String getName(Locale locale) {
        return "csslint";
    }

    public String getDescription(Locale locale) {
        return "CSSLint Report of the project";
    }
}
