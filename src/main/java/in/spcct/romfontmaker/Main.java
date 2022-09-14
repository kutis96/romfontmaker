package in.spcct.romfontmaker;

import in.spcct.romfontmaker.impl.BufferedImageBitmapImage;
import in.spcct.romfontmaker.impl.SimpleImporter;
import in.spcct.romfontmaker.impl.TemplatedExporter;
import in.spcct.romfontmaker.tools.PreviewUtils;
import org.apache.commons.cli.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Main {

    record CommandlineConfig(
            String fontImagePath,
            int glyphWidth,
            int glyphHeight,
            boolean invertImage,
            int startingCodepoint,

            boolean doPreview,
            String previewPath,

            boolean doExport,
            String outputPath,
            String templatePath,
            String templateDir,

            int codesGreaterThan,
            int codesLesserThan,

            String romName
    ) {
    }

    private static CommandlineConfig parseCommandLine(String[] args) throws ParseException {
        //TODO: Come up with some slightly less repetitive and brittle for the config setup

        final String OPT_HELP = "h";
        final String OPT_FONT_IMAGE = "if";
        final String OPT_OUTPUT_FILE = "of";
        final String OPT_GLYPH_WIDTH = "gw";
        final String OPT_GLYPH_HEIGHT = "gh";
        final String OPT_INVERT_IMAGE = "inv";
        final String OPT_STARTING_CODEPOINT = "scpt";
        final String OPT_PREVIEW_PATH = "Pf";
        final String OPT_TEMPLATE_PATH = "t";
        final String OPT_TEMPLATE_DIR = "td";
        final String OPT_GREATER_CODES = "ogt";
        final String OPT_LESSER_CODES = "olt";
        final String OPT_OUTPUT_LABEL = "n";

        Options cliOptions = new Options();

        cliOptions.addOption(OPT_HELP, "help", false, "Prints something hopefully helpful");
        cliOptions.addRequiredOption(OPT_FONT_IMAGE, "font-image", true, "Image to generate the font ROM from");
        cliOptions.addRequiredOption(OPT_GLYPH_WIDTH, "glyph-width", true, "Glyph width");
        cliOptions.addRequiredOption(OPT_GLYPH_HEIGHT, "glyph-height", true, "Glyph height");
        cliOptions.addOption(OPT_INVERT_IMAGE, "Invert image file");
        cliOptions.addOption(OPT_STARTING_CODEPOINT, "starting-codepoint", true, "Starting codepoint (defaults to 0)");
        cliOptions.addOption(OPT_PREVIEW_PATH, "preview-file", true, "Preview text drawn with this font, sourced from this file");
        cliOptions.addOption(OPT_OUTPUT_FILE, "output-file", true, "Output file");
        cliOptions.addOption(OPT_TEMPLATE_PATH, "template", true, "Template name");
        cliOptions.addOption(OPT_TEMPLATE_DIR, "template-dir", true, "Template directory");
        cliOptions.addOption(OPT_GREATER_CODES, "only-codes-gt", true, "Export only codepoints greater than or equal to...");
        cliOptions.addOption(OPT_LESSER_CODES, "only-codes-lt", true, "Export only codepoints lower than or equal to...");
        cliOptions.addOption(OPT_OUTPUT_LABEL, "rom-name", true, "Name to call the output ROM in the source code");

        CommandLineParser commandLineParser = new DefaultParser();
        CommandLine commandLine = null;

        try {
            commandLine = commandLineParser.parse(cliOptions, args);
        } finally {
            if(commandLine == null || commandLine.hasOption(OPT_HELP)) {
                HelpFormatter helpFormatter = new HelpFormatter();
                helpFormatter.printHelp("romfontmaker", cliOptions);
            }
        }

        return new CommandlineConfig(
                commandLine.getOptionValue(OPT_FONT_IMAGE),
                Integer.parseInt(commandLine.getOptionValue(OPT_GLYPH_WIDTH)),
                Integer.parseInt(commandLine.getOptionValue(OPT_GLYPH_HEIGHT)),
                commandLine.hasOption(OPT_INVERT_IMAGE),
                Integer.parseInt(commandLine.getOptionValue(OPT_STARTING_CODEPOINT, "0")),
                commandLine.hasOption(OPT_PREVIEW_PATH),
                commandLine.getOptionValue(OPT_PREVIEW_PATH, ""),
                commandLine.hasOption(OPT_OUTPUT_FILE),
                commandLine.getOptionValue(OPT_OUTPUT_FILE),
                commandLine.getOptionValue(OPT_TEMPLATE_PATH, null),
                commandLine.getOptionValue(OPT_TEMPLATE_DIR, null),
                Integer.parseInt(commandLine.getOptionValue(OPT_GREATER_CODES, "0")),
                Integer.parseInt(commandLine.getOptionValue(OPT_LESSER_CODES, "" + Integer.MAX_VALUE)),
                commandLine.getOptionValue(OPT_OUTPUT_LABEL, "font")
        );
    }

    public static void main(String[] args) throws Exception {

        CommandlineConfig config = parseCommandLine(args);

        BufferedImage fontImage = ImageIO.read(new File(config.fontImagePath));

        BitmapImage fontImageBitmap = new BufferedImageBitmapImage(fontImage);

        FontImporter importer = new SimpleImporter();

        Map<String, String> internalConfig = new HashMap<>();
        internalConfig.put(SimpleImporter.CONFIG_STARTING_CODEPOINT, "" + config.startingCodepoint);
        internalConfig.put(SimpleImporter.CONFIG_GLYPH_WIDTH, "" + config.glyphWidth);
        internalConfig.put(SimpleImporter.CONFIG_GLYPH_HEIGHT, "" + config.glyphHeight);
        internalConfig.put(SimpleImporter.CONFIG_IMAGE_INVERT, "" + config.invertImage);

        Font font = importer.importFont(fontImageBitmap, internalConfig);

        if (config.doPreview) {
            String previewThis = Files.readString(Path.of(config.previewPath));
            JFrame frame = PreviewUtils.previewImage(
                    PreviewUtils.previewString(
                            font,
                            previewThis
                    )
            );
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        }

        if (config.doExport) {
            internalConfig.clear();

            Exporter exporter = new TemplatedExporter();

            internalConfig.put(TemplatedExporter.CONFIG_ROM_NAME, config.romName);
            internalConfig.put(TemplatedExporter.CONFIG_TEMPLATE_DIRECTORY, config.templateDir);
            internalConfig.put(TemplatedExporter.CONFIG_TEMPLATE, config.templatePath);

            try (OutputStream outputStream = new FileOutputStream(config.outputPath)) {
                exporter.export(
                        font.filter(
                                (character, glyph) -> character >= config.codesGreaterThan
                                        && character <= config.codesLesserThan
                        ),
                        outputStream,
                        internalConfig
                );
            }
        }

    }

}
