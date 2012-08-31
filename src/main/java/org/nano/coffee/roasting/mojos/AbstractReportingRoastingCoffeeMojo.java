package org.nano.coffee.roasting.mojos;


import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.site.decoration.DecorationModel;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.doxia.siterenderer.RendererException;
import org.apache.maven.doxia.siterenderer.SiteRenderingContext;
import org.apache.maven.doxia.siterenderer.sink.SiteRendererSink;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.apache.maven.reporting.sink.SinkFactory;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.WriterFactory;
import org.nano.coffee.roasting.processors.Processor;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public abstract class AbstractReportingRoastingCoffeeMojo extends AbstractRoastingCoffeeMojo implements MavenReport {

    /**
     * The maven project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    public MavenProject project;

    /**
     * Maven ProjectHelper.
     *
     * @component
     * @readonly
     */
    public MavenProjectHelper projectHelper;

    /**
     * Directory containing the build files.
     *
     * @parameter expression="${project.build.directory}"
     */
    public File buildDirectory;

    /**
     * Base directory of the project.
     *
     * @parameter expression="${basedir}"
     */
    public File baseDirectory;

    /**
     * Where are JavaScript files.
     *
     * @parameter default-value="src/main/js"
     */
    public File javaScriptDir;

    /**
     * Where are CoffeeScript files.
     *
     * @parameter default-value="src/main/coffee"
     */
    public File coffeeScriptDir;

    /**
     * Where are CoffeeScript files implementing tests.
     *
     * @parameter default-value="src/test/coffee"
     */
    public File coffeeScriptTestDir;

    /**
     * Where are JavaScript files implementing tests.
     *
     * @parameter default-value="src/test/js"
     */
    public File javaScriptTestDir;

    /**
     * Where are the assets.
     *
     * @parameter default-value="src/main/www"
     */
    public File assetsDir;

    /**
     * Where are LESS, CSS and SASS/SCSS files.
     *
     * @parameter default-value="src/main/stylesheets"
     */
    public File stylesheetsDir;

    /**
     * Where are the output files written.
     *
     * @parameter default-value="target/www"
     */
    public File workDir;

    /**
     * Where are the output test files written.
     *
     * @parameter default-value="target/www-test"
     */
    public File workTestDir;

    /**
     * Where are the dependencies copies.
     *
     * @parameter default-value="target/libs"
     */
    public File libDir;

    /**
     * @component
     * @required
     * @readonly
     */
    protected Renderer siteRenderer;

    /**
     * Directory where reports will go.
     *
     * @parameter expression="${project.reporting.outputDirectory}"
     * @required
     * @readonly
     */
    private String outputDirectory;


    public File getTarget() {
        return new File(project.getBuild().getDirectory());
    }

    public File getWorkDirectory() {
        workDir.mkdirs();
        return workDir;
    }

    public File getWorkTestDirectory() {
        workTestDir.mkdirs();
        return workTestDir;
    }

    public File getLibDirectory() {
        return libDir;
    }

    protected MavenProject getProject() {
        return project;
    }

    protected Renderer getSiteRenderer() {
        return siteRenderer;
    }

    public boolean canGenerateReport() {
        return true;
    }

    protected String getOutputDirectory() {
        return outputDirectory;
    }

    /**
     * The current sink to use
     */
    private Sink sink;

    private Locale locale = Locale.ENGLISH;

    /**
     * The current report output directory to use
     */
    private File reportOutputDirectory;

    /**
     * This method is called when the report generation is invoked directly as a standalone Mojo.
     *
     * @throws org.apache.maven.plugin.MojoExecutionException
     *          if an error uccurs when generating the report
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    public void execute()
            throws MojoExecutionException {
        Writer writer = null;
        try {
            File outputDirectory = new File(getOutputDirectory());

            String filename = getOutputName() + ".html";

            SiteRenderingContext context = new SiteRenderingContext();
            context.setDecoration(new DecorationModel());
            context.setTemplateName("org/apache/maven/doxia/siterenderer/resources/default-site.vm");
            context.setLocale(locale);

            SiteRendererSink sink = SinkFactory.createSink(outputDirectory, filename);

            generate(sink, Locale.getDefault());

            // TODO: add back when skinning support is in the site renderer
//            getSiteRenderer().copyResources( outputDirectory, "maven" );

            File outputHtml = new File(outputDirectory, filename);
            outputHtml.getParentFile().mkdirs();

            writer = WriterFactory.newXmlWriter(outputHtml);

            getSiteRenderer().generateDocument(writer, sink, context);
        } catch (MavenReportException e) {
            throw new MojoExecutionException("An error has occurred in " + getName(locale) + " report generation.",
                    e);
        } catch (RendererException e) {
            throw new MojoExecutionException("An error has occurred in " + getName(Locale.ENGLISH)
                    + " report generation.", e);
        } catch (IOException e) {
            throw new MojoExecutionException("An error has occurred in " + getName(Locale.ENGLISH)
                    + " report generation.", e);
        } finally {
            IOUtil.close(writer);
        }
    }

    /**
     * This method is called when the report generation is invoked by maven-site-plugin.
     *
     * @see org.apache.maven.reporting.MavenReport#generate(org.codehaus.doxia.sink.Sink, java.util.Locale)
     */
    public void generate(org.codehaus.doxia.sink.Sink sink, Locale locale)
            throws MavenReportException {
        if (sink == null) {
            throw new MavenReportException("You must specify a sink.");
        }

        this.sink = sink;

        executeReport(locale);

        closeReport();
    }

    protected void closeReport() {
        getSink().close();
    }

    /**
     * {@inheritDoc}
     */
    public String getCategoryName() {
        return CATEGORY_PROJECT_REPORTS;
    }

    /**
     * {@inheritDoc}
     */
    public File getReportOutputDirectory() {
        if (reportOutputDirectory == null) {
            reportOutputDirectory = new File(getOutputDirectory());
        }
        return reportOutputDirectory;
    }

    /**
     * {@inheritDoc}
     */
    public void setReportOutputDirectory(File reportOutputDirectory) {
        this.reportOutputDirectory = reportOutputDirectory;
    }

    /**
     * @return the sink used
     */
    public Sink getSink() {
        return sink;
    }

    /**
     * @return <tt>false</tt> by default.
     * @see org.apache.maven.reporting.MavenReport#isExternalReport()
     */
    public boolean isExternalReport() {
        return false;
    }

    public abstract void writeIntroduction();

    public abstract Map<File, List<Processor.ProcessorWarning>> validate() throws Processor.ProcessorException;

    public void executeReport(Locale locale) throws MavenReportException {
        Map<File, List<Processor.ProcessorWarning>> results = null;
        try {
            results = validate();
        } catch (Processor.ProcessorException e) {
            throw new MavenReportException("Can't build report", e);
        }

        Sink sink = getSink();
        sink.head();
        sink.title();
        sink.text(getName(locale));
        sink.title_();
        sink.head_();

        sink.body();

        writeIntroduction();

        sink.section2();
        sink.sectionTitle2();
        sink.text("Summary");
        sink.sectionTitle2_();
        sink.table();
        sink.tableRow();
        sink.tableHeaderCell();
        sink.text("File");
        sink.tableHeaderCell_();
        sink.tableHeaderCell();
        sink.text("Warnings");
        sink.tableHeaderCell_();
        sink.tableRow_();

        for (Map.Entry<File, List<Processor.ProcessorWarning>> entry : results.entrySet()) {
            sink.tableRow();
            sink.tableCell();
            sink.link(entry.getKey().getName());
            sink.text(entry.getKey().getName());
            sink.link_();
            sink.tableCell_();
            sink.tableCell();
            sink.text("" + entry.getValue().size());
            sink.tableCell_();
            sink.tableRow_();
        }
        sink.table_();
        sink.section2_();


        for (Map.Entry<File, List<Processor.ProcessorWarning>> entry : results.entrySet()) {
            if (entry.getValue().size() > 0) {
                sink.section2();
                sink.sectionTitle2();
                sink.text(entry.getKey().getName());
                sink.sectionTitle2_();

                sink.table();
                sink.tableRow();
                sink.tableHeaderCell();
                sink.text("Position");
                sink.tableHeaderCell_();
                sink.tableHeaderCell();
                sink.text("Reason");
                sink.tableHeaderCell_();
                sink.tableHeaderCell();
                sink.text("Evidence");
                sink.tableHeaderCell_();
                sink.tableRow_();

                for (Processor.ProcessorWarning warning : entry.getValue()) {
                    sink.tableRow();
                    sink.tableCell();
                    sink.text(warning.line + ":" + warning.character);
                    sink.tableCell_();
                    sink.tableCell();
                    sink.text(warning.evidence);
                    sink.tableCell_();
                    sink.tableCell();
                    sink.text(warning.reason);
                    sink.tableCell_();
                    sink.tableRow_();
                }
                sink.table_();
                sink.section2_();
            }
        }
        sink.body_();
        sink.flush();
        sink.close();

    }

}
