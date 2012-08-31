package org.nano.coffee.roasting.mojos.reporting;

import org.apache.commons.io.FileUtils;
import org.apache.maven.doxia.sink.Sink;
import org.nano.coffee.roasting.mojos.AbstractReportingRoastingCoffeeMojo;
import org.nano.coffee.roasting.processors.JSLintProcessor;
import org.nano.coffee.roasting.processors.Processor;

import java.io.File;
import java.util.*;

/**
 * Builds the JSHint Report.
 *
 * @goal jslint-report
 * @phase site
 */
public class JSLintReportMojo extends AbstractReportingRoastingCoffeeMojo {

    @Override
    public void writeIntroduction() {
        Sink sink = getSink();
        sink.section1();
        sink.sectionTitle1();
        sink.text("JSLint");
        sink.sectionTitle1_();
        sink.section1_();

        sink.paragraph();
        sink.link( "http://www.jslint.com" );
        sink.text( "JSLint" );
        sink.link_();
        sink.text(" is a JavaScript program that looks for problems in JavaScript programs. It is a code quality" +
                " tool, and it gonna hurt you...\n" + "JSLint takes a JavaScript source and scans it. If it finds a " +
                "problem, it returns a message describing the problem and an approximate location within the source. " +
                "The problem is not necessarily a syntax error, although it often is. JSLint looks at some style " +
                "conventions as well as structural problems. It does not prove that your program is correct. It just " +
                "provides another set of eyes to help spot problems.\n" +
                "JSLint defines a professional subset of JavaScript, a stricter language than that defined by Third " +
                "Edition of the ECMAScript Programming Language Standard. The subset is related to recommendations " +
                "found in Code Conventions for the JavaScript Programming Language.\n" +
                "JavaScript is a sloppy language, but inside it there is an elegant, better language. JSLint helps you" +
                " to program in that better language and to avoid most of the slop. JSLint will reject programs that " +
                "browsers will accept because JSLint is concerned with the quality of your code and browsers are not. " +
                "You should accept all of JSLint's advice.");
    }

    @Override
    public Map<File, List<Processor.ProcessorWarning>> validate() {
        Map<File, List<Processor.ProcessorWarning>> results = new TreeMap<File, List<Processor.ProcessorWarning>>();
        Collection<File> files = FileUtils.listFiles(getWorkDirectory(), new String[]{"js"}, true);
        JSLintProcessor processor = new JSLintProcessor();
        processor.configure(this, null);
        for (File file : files) {
            results.put(file, processor.validate(file));
        }
        return results;
    }

    public String getOutputName() {
        return "jslint";
    }

    public String getName(Locale locale) {
        return "jslint";
    }

    public String getDescription(Locale locale) {
        return "JSLint Report of the project";
    }
}
