package org.nano.coffee.roasting.mojos.reporting;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.nano.coffee.roasting.utils.ExecUtils;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

/**
 * Builds the JSDoc API.
 * It uses JSDoc3 but <strong>requires</strong> to have the <tt>jsdoc</tt> executable in the path.
 *
 * @goal jsdoc
 * @phase site
 */
public class JsDocMojo extends AbstractMavenReport {

    /**
     * @parameter default-value="false"
     */
    protected boolean skipJSDOC;

    /**
     * Whether to include symbols tagged as private. Default is <code>false</code>.
     *
     * @parameter expression="false"
     */
    protected boolean jsdocIncludePrivate;

    /**
     * Directory where reports will go.
     *
     * @parameter expression="${project.reporting.outputDirectory}"
     * @required
     * @readonly
     */
    protected String outputDirectory;

    /**
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    /**
     * @component
     * @required
     * @readonly
     */
    protected Renderer siteRenderer;


    public void execute() throws MojoExecutionException {
        try {
            executeReport(null);
        } catch (MavenReportException e) {
            throw new MojoExecutionException("Cannot build JSDOC report", e);
        }
    }

    @Override
    protected Renderer getSiteRenderer() {
        return siteRenderer;
    }

    public String getName(Locale locale) {
        return "jsdoc";
    }

    public String getDescription(Locale locale) {
        return "Generate JSDOC report";
    }

    @Override
    protected String getOutputDirectory() {
        return this.outputDirectory + "/jsdoc";
    }

    @Override
    protected MavenProject getProject() {
        return project;
    }

    @Override
    protected void executeReport(Locale locale) throws MavenReportException {
        generateJSDOC();
    }

    private void generateJSDOC() throws MavenReportException {
        if (skipJSDOC) {
            getLog().info("JSDoc report generation skipped");
            return;
        }


        File jsdocExec = ExecUtils.findExecutableInPath("jsdoc");
        if (jsdocExec == null) {
            throw new MavenReportException("Cannot build jsdoc report - jsdoc not in the system path");
        } else {
            getLog().info("Invoking jsdoc : " + jsdocExec.getAbsolutePath());
            getLog().info("Output directory : " + getOutputDirectory());
        }

        File out = new File(getOutputDirectory());
        out.mkdirs();

        CommandLine cmdLine = CommandLine.parse(jsdocExec.getAbsolutePath());

        // Destination
        cmdLine.addArgument("--destination");
        cmdLine.addArgument(out.getAbsolutePath());

        if (jsdocIncludePrivate) {
            cmdLine.addArgument("--private");
        }

        File input = new File(project.getBuild().getDirectory(), project.getBuild().getFinalName() + ".js");
        if (! input.exists()) {
            throw new MavenReportException("Cannot find the project's artifact : " + input.getAbsolutePath());
        }
        cmdLine.addArgument(input.getAbsolutePath());


        DefaultExecutor executor = new DefaultExecutor();

        executor.setWorkingDirectory(project.getBasedir());
        executor.setExitValue(0);
        try {
            getLog().info("Executing " + cmdLine.toString());
            executor.execute(cmdLine);
        } catch (IOException e) {
            throw new MavenReportException("Error during jsdoc report generation", e);
        }


    }

    public String getOutputName() {
        return "jsdoc" + "/index";
    }

    /**
     * @see org.apache.maven.reporting.AbstractMavenReport#isExternalReport()
     */
    public boolean isExternalReport() {
        return true;
    }

}
