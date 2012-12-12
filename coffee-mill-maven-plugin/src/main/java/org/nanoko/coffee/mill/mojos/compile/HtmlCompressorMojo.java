package org.nanoko.coffee.mill.mojos.compile;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.nanoko.coffee.mill.mojos.AbstractCoffeeMillMojo;
import org.nanoko.coffee.mill.processors.HTMLCompressorProcessor;
import org.nanoko.coffee.mill.processors.Processor;
import org.nanoko.coffee.mill.utils.OptionsHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Compress HTML pages using HtmlCompressor.
 * @goal compress-html
 */
public class HtmlCompressorMojo extends AbstractCoffeeMillMojo {

    /**
     * Enables / disables the HTML Compression
     * The compression is disabled by default. Once enabled, don't forget to set up the compression options.
     * @parameter default-value=true
     */
    public Boolean skipHtmlCompressor = true;

    /**
     * If set to false keeps HTML comments (default is true)
     *
     * @parameter default-value="true"
     */
    public Boolean htmlCompressionRemoveComments = true;

    /**
     * If set to false keeps line breaks (default is true)
     *
     * @parameter default-value="true"
     */
    public Boolean htmlCompressionPreserveLineBreak = true;

    /**
     * If sets to false keeps multiple whitespace characters (default is true)
     *
     * @parameter default-value="true"
     */
    public Boolean htmlCompressionRemoveMultiSpaces = true;

    /**
     * Removes iter-tag whitespace characters
     *
     * @parameter default-value="false"
     */
    public Boolean htmlCompressionRemoveIntertagSpaces = false;

    /**
     * Removes unnecessary tag attribute quotes
     *
     * @parameter default-value="false"
     */
    public Boolean htmlCompressionRemoveQuotes = false;

    /**
     * Simplifies existing doctype
     *
     * @parameter default-value="false"
     */
    public Boolean htmlCompressionSimpleDoctype = false;

    /**
     * Removes optional attributes from script tags
     *
     * @parameter default-value="false"
     */
    public Boolean htmlCompressionRemoveScriptAttributes = false;

    /**
     * Removes optional attributes from style tags
     *
     * @parameter default-value="false"
     */
    public Boolean htmlCompressionRemoveStyleAttributes = false;

    /**
     * Removes optional attributes from link tags
     *
     * @parameter default-value="false"
     */
    public Boolean htmlCompressionRemoveLinkAttributes = false;

    /**
     * Removes optional attributes from form tags
     *
     * @parameter default-value="false"
     */
    public Boolean htmlCompressionRemoveFormAttributes = false;

    /**
     * Removes optional attributes from input tags
     *
     * @parameter default-value="false"
     */
    public Boolean htmlCompressionRemoveInputAttributes = false;

    /**
     * Removes values from boolean tag attributes
     *
     * @parameter default-value="false"
     */
    public Boolean htmlCompressionSimpleBooleanAttributes = false;

    /**
     * Removes "javascript:" from inline event handlers
     *
     * @parameter expression="${htmlcompressor.removeJavaScriptProtocol}" default-value="false"
     */
    public Boolean htmlCompressionRemoveJavaScriptProtocol = false;

    /**
     * Replaces "http://" with "//" inside tag attributes
     *
     * @parameter default-value="false"
     */
    public Boolean htmlCompressionRemoveHttpProtocol = false;

    /**
     * Replace "https://" with "//" inside tag attributes
     *
     * @parameter default-value="false"
     */
    public Boolean htmlCompressionRemoveHttpsProtocol = false;

    /**
     * Predefined patterns for most often used custom preservation rules: PHP_TAG_PATTERN and
     * SERVER_SCRIPT_TAG_PATTERN.
     *
     * @parameter
     */
    public String[] htmlCompressionPredefinedPreservePatterns;

    /**
     * Preserve patterns
     *
     * @parameter
     */
    public String[] htmlCompressionPreservePatterns;

    /**
     * HTML compression statistics
     *
     * @parameter default-value="true"
     */
    public Boolean htmlCompressionGenerateStatistics = true;


    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skipHtmlCompressor) {
            return;
        }

        HTMLCompressorProcessor processor = new HTMLCompressorProcessor();

        Map<String, Object> options = new OptionsHelper.OptionsBuilder()
                .set("preserveLineBreak", htmlCompressionPreserveLineBreak)
                .set("removeComments", htmlCompressionRemoveComments)
                .set("removeMultispaces", htmlCompressionRemoveMultiSpaces)
                .set("removeFormAttributes", htmlCompressionRemoveFormAttributes)
                .set("removeHttpProtocol", htmlCompressionRemoveHttpProtocol)
                .set("removeHttpsProtocol", htmlCompressionRemoveHttpsProtocol)
                .set("removeInputAttributes", htmlCompressionRemoveInputAttributes)
                .set("removeIntertagSpaces", htmlCompressionRemoveIntertagSpaces)
                .set("removeJavascriptProtocol", htmlCompressionRemoveJavaScriptProtocol)
                .set("removeLinkAttributes", htmlCompressionRemoveLinkAttributes)
                .set("removeQuotes", htmlCompressionRemoveQuotes)
                .set("removeScriptAttributes", htmlCompressionRemoveScriptAttributes)
                .set("simpleBooleanAttributes", htmlCompressionSimpleBooleanAttributes)
                .set("removeStyleAttributes", htmlCompressionRemoveStyleAttributes)
                .set("simpleDocType", htmlCompressionSimpleDoctype)
                .build();

        // Preserve file Patterns
        List<Pattern> patterns = new ArrayList<Pattern>();
        boolean phpTagPatternAdded = false;
        boolean serverScriptTagPatternAdded = false;
        if (htmlCompressionPredefinedPreservePatterns != null) {
            for (String pattern : htmlCompressionPredefinedPreservePatterns) {
                if (!phpTagPatternAdded && pattern.equalsIgnoreCase("PHP_TAG_PATTERN")) {
                    patterns.add(com.googlecode.htmlcompressor.compressor.HtmlCompressor.PHP_TAG_PATTERN);
                    phpTagPatternAdded = true;
                } else if (!serverScriptTagPatternAdded && pattern.equalsIgnoreCase("SERVER_SCRIPT_TAG_PATTERN")) {
                    patterns.add(com.googlecode.htmlcompressor.compressor.HtmlCompressor.SERVER_SCRIPT_TAG_PATTERN);
                    serverScriptTagPatternAdded = true;
                }
            }
        }
        if (htmlCompressionPreservePatterns != null) {
            for (String preservePatternString : htmlCompressionPreservePatterns) {
                if (!preservePatternString.isEmpty()) {
                    try {
                        patterns.add(Pattern.compile(preservePatternString));
                    } catch (PatternSyntaxException e) {
                        throw new MojoExecutionException(e.getMessage());
                    }
                }
            }
        }
        options.put("preservePatterns", patterns);
        processor.configure(this, options);

        try {
            processor.processAll();
        } catch (Processor.ProcessorException e) {
            throw new MojoExecutionException("Can't compress HTML files", e);
        }
    }

}
