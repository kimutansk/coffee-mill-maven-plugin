package org.nanoko.coffee.mill.processors;

import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import org.apache.commons.io.FileUtils;
import org.nanoko.coffee.mill.mojos.AbstractCoffeeMillMojo;
import org.nanoko.coffee.mill.utils.OptionsHelper;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * A processor optimizing HTML pages using HTMLCompressor.
 */
public class HTMLCompressorProcessor extends DefaultProcessor {


    private boolean preserveLineBreak = true;
    private List<Pattern> preservePatterns = new ArrayList<Pattern>();
    private boolean removeComments = false;
    private boolean removeMultispaces = false;
    private boolean removeFormAttributes = false;
    private boolean removeHttpProtocol = false;
    private boolean removeHttpsProtocol = false;
    private boolean removeInputAttributes = false;
    private boolean removeIntertagSpaces = false;
    private boolean removeJavascriptProtocol = false;
    private boolean removeLinkAttributes = false;
    private boolean removeQuotes = false;
    private boolean removeScriptAttributes = false;
    private boolean removeStyleAttributes = false;
    private boolean simpleBooleanAttributes = false;
    private boolean simpleDocType = false;


    @Override
    public void configure(AbstractCoffeeMillMojo mojo, Map<String, Object> options) {
        super.configure(mojo, options);

        preserveLineBreak = OptionsHelper.getBoolean(options, "preserveLineBreak", false);
        removeComments = OptionsHelper.getBoolean(options, "removeComments", false);
        removeMultispaces = OptionsHelper.getBoolean(options, "removeMultispaces", false);
        removeFormAttributes = OptionsHelper.getBoolean(options, "removeFormAttributes", false);
        removeHttpProtocol = OptionsHelper.getBoolean(options, "removeHttpProtocol", false);
        removeHttpsProtocol = OptionsHelper.getBoolean(options, "removeHttpsProtocol", false);
        removeInputAttributes = OptionsHelper.getBoolean(options, "removeInputAttributes", false);
        removeIntertagSpaces = OptionsHelper.getBoolean(options, "removeIntertagSpaces", false);
        removeJavascriptProtocol = OptionsHelper.getBoolean(options, "removeJavascriptProtocol", false);
        removeLinkAttributes = OptionsHelper.getBoolean(options, "removeLinkAttributes", false);
        removeQuotes = OptionsHelper.getBoolean(options, "removeQuotes", false);
        removeScriptAttributes = OptionsHelper.getBoolean(options, "removeScriptAttributes", false);
        simpleBooleanAttributes = OptionsHelper.getBoolean(options, "simpleBooleanAttributes", false);
        removeStyleAttributes = OptionsHelper.getBoolean(options, "removeStyleAttributes", false);
        simpleDocType = OptionsHelper.getBoolean(options, "simpleDocType", false);

        if (options.containsKey("preservePatterns")) {
            preservePatterns =(List<Pattern>) options.get("preserverPatterns");
        }
    }

    /**
     * Iterates over project HTML and HTM files and compress them.
     *
     * @throws org.nanoko.coffee.mill.processors.Processor.ProcessorException
     */
    @Override
    public void processAll() throws ProcessorException {
        if (! mojo.assetsDir.exists()) {
            return;
        }

        Collection<File> files = FileUtils.listFiles(mojo.assetsDir, new String[]{"html", "htm"}, true);
        for (File file : files) {
            compress(file);
        }
    }

    @Override
    public boolean accept(File file) {
        return isFileContainedInDirectory(file, mojo.assetsDir)
                && (file.getName().endsWith(".html") || (file.getName().endsWith(".htm")));
    }

    @Override
    public void fileCreated(File file) throws ProcessorException {
        compress(file);
    }

    @Override
    public void fileUpdated(File file) throws ProcessorException {
        compress(file);
    }

    private void compress(File file) throws ProcessorException {
        HtmlCompressor htmlCompressor = new HtmlCompressor();

        htmlCompressor.setCompressCss(false);
        htmlCompressor.setCompressJavaScript(false);
        htmlCompressor.setEnabled(true);
        htmlCompressor.setGenerateStatistics(true);
        htmlCompressor.setPreserveLineBreaks(preserveLineBreak);
        htmlCompressor.setPreservePatterns(preservePatterns);
        htmlCompressor.setRemoveComments(removeComments);
        htmlCompressor.setRemoveMultiSpaces(removeMultispaces);
        htmlCompressor.setRemoveFormAttributes(removeFormAttributes);
        htmlCompressor.setRemoveHttpProtocol(removeHttpProtocol);
        htmlCompressor.setRemoveHttpsProtocol(removeHttpsProtocol);
        htmlCompressor.setRemoveInputAttributes(removeInputAttributes);
        htmlCompressor.setRemoveIntertagSpaces(removeIntertagSpaces);
        htmlCompressor.setRemoveJavaScriptProtocol(removeJavascriptProtocol);
        htmlCompressor.setRemoveLinkAttributes(removeLinkAttributes);
        htmlCompressor.setRemoveQuotes(removeQuotes);
        htmlCompressor.setRemoveScriptAttributes(removeScriptAttributes);
        htmlCompressor.setRemoveStyleAttributes(removeStyleAttributes);
        htmlCompressor.setSimpleBooleanAttributes(simpleBooleanAttributes);
        htmlCompressor.setSimpleDoctype(simpleDocType);

        try {
            String result = htmlCompressor.compress(FileUtils.readFileToString(file));
            File out = getOutputHtmlFile(file);
            out.getParentFile().mkdirs();
            FileUtils.write(out, result);
            writeStatistics(htmlCompressor, file);
        } catch(Exception e) {
            throw new ProcessorException(e.getMessage());
        }


        getLog().info("HTML compression completed.");
    }

    private void writeStatistics(HtmlCompressor htmlCompressor, File file) {
        boolean si = true;

        int origFilesizeBytes = htmlCompressor.getStatistics().getOriginalMetrics().getFilesize();
        String origFilesize = humanReadableByteCount(origFilesizeBytes, si);
        String origEmptyChars = String.valueOf(htmlCompressor.getStatistics().getOriginalMetrics().getEmptyChars());
        String origInlineEventSize = humanReadableByteCount(htmlCompressor.getStatistics().getOriginalMetrics().getInlineEventSize(), si);
        String origInlineScriptSize =humanReadableByteCount(htmlCompressor.getStatistics().getOriginalMetrics().getInlineScriptSize(), si);
        String origInlineStyleSize =humanReadableByteCount(htmlCompressor.getStatistics().getOriginalMetrics().getInlineStyleSize(), si);

        int compFilesizeBytes = htmlCompressor.getStatistics().getCompressedMetrics().getFilesize();
        String compFilesize =humanReadableByteCount(compFilesizeBytes, si);
        String compEmptyChars = String.valueOf(htmlCompressor.getStatistics().getCompressedMetrics().getEmptyChars());
        String compInlineEventSize =humanReadableByteCount(htmlCompressor.getStatistics().getCompressedMetrics().getInlineEventSize(), si);
        String compInlineScriptSize =humanReadableByteCount(htmlCompressor.getStatistics().getCompressedMetrics().getInlineScriptSize(), si);
        String compInlineStyleSize =humanReadableByteCount(htmlCompressor.getStatistics().getCompressedMetrics().getInlineStyleSize(), si);

        String elapsedTime = getElapsedHMSTime(htmlCompressor.getStatistics().getTime());
        String preservedSize =humanReadableByteCount(htmlCompressor.getStatistics().getPreservedSize(), si);
        Float compressionRatio = new Float(compFilesizeBytes) / new Float(origFilesizeBytes);
        Float spaceSavings = new Float(1) - compressionRatio;

        String format = "%-30s%-30s%-30s%-2s";
        NumberFormat formatter = new DecimalFormat("#0.00");
        String eol = "\n";
        String hr = "+-----------------------------+-----------------------------+-----------------------------+";
        StringBuilder sb = new StringBuilder(file.getName() + " - HTML compression statistics:").append(eol);
        sb.append(hr).append(eol);
        sb.append(String.format(format, "| Category", "| Original", "| Compressed", "|")).append(eol);
        sb.append(hr).append(eol);
        sb.append(String.format(format, "| Filesize", "| " + origFilesize, "| " + compFilesize, "|")).append(eol);
        sb.append(String.format(format, "| Empty Chars", "| " + origEmptyChars, "| " + compEmptyChars, "|")).append(eol);
        sb.append(String.format(format, "| Script Size", "| " + origInlineScriptSize, "| " + compInlineScriptSize, "|")).append(eol);
        sb.append(String.format(format, "| Style Size", "| " + origInlineStyleSize, "| " + compInlineStyleSize, "|")).append(eol);
        sb.append(String.format(format, "| Event Handler Size", "| " + origInlineEventSize, "| " + compInlineEventSize, "|")).append(eol);
        sb.append(hr).append(eol);
        sb.append(String.format("%-90s%-2s",
                String.format("| Time: %s, Preserved: %s, Compression Ratio: %s, Savings: %s%%",
                        elapsedTime, preservedSize, formatter.format(compressionRatio), formatter.format(spaceSavings*100)),
                "|")).append(eol);
        sb.append(hr).append(eol);

        String statistics = sb.toString();
        getLog().info(statistics);
    }

    private File getOutputHtmlFile(File input) {
        String path = input.getParentFile().getAbsolutePath().substring(mojo.assetsDir.getAbsolutePath().length());
        return new File(mojo.getWorkDirectory(), path + "/" + input.getName());
    }

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static String getElapsedHMSTime(long elapsedTime) {
        String format = String.format("%%0%dd", 2);
        elapsedTime = elapsedTime / 1000;
        String seconds = String.format(format, elapsedTime % 60);
        String minutes = String.format(format, (elapsedTime % 3600) / 60);
        String hours = String.format(format, elapsedTime / 3600);
        return hours + ":" + minutes + ":" + seconds;
    }
}
