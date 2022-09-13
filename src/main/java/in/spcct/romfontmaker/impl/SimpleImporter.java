package in.spcct.romfontmaker.impl;

import in.spcct.romfontmaker.*;

import java.util.HashMap;
import java.util.Map;

public class SimpleImporter implements FontImporter {

    public static final String CONFIG_GLYPH_WIDTH = "glyph-width";
    public static final String CONFIG_GLYPH_HEIGHT = "glyph-height";
    public static final String CONFIG_IMAGE_INVERT = "invert";

    public static final String CONFIG_STARTING_CODEPOINT = "starting-codepoint";

    @Override
    public Font importFont(BitmapImage image, Map<String, String> config) {

        ImporterProper importer = new ImporterProper(
                new Vec2D(
                        Integer.parseInt(config.getOrDefault(CONFIG_GLYPH_WIDTH, "8")),
                        Integer.parseInt(config.getOrDefault(CONFIG_GLYPH_HEIGHT, "16"))
                ),
                Boolean.parseBoolean(config.getOrDefault(CONFIG_IMAGE_INVERT, "false")),
                (char) Integer.parseInt(config.getOrDefault(CONFIG_STARTING_CODEPOINT, "0"))
        );

        return importer.importFont(image);
    }

    private record ImporterProper(
            Vec2D glyphSize,
            boolean invert,
            char startingCodepoint
    ) {
        private Vec2D glyphOffset(Vec2D imageSize, int n) {
            // "X-first, Y-second" addressing
            // TODO: Make configurable.

            int widthInGlyphs = imageSize.x() / glyphSize().x();
            int x = (n * glyphSize.x()) % imageSize.x();
            int y = ((n / widthInGlyphs) * glyphSize.y());

            return new Vec2D(x, y);
        }

        private int numberOfGlyphs(Vec2D imageSize) {
            return (imageSize.x() / glyphSize.x()) * (imageSize.y() / glyphSize.y());
        }

        public Font importFont(BitmapImage inputImage) {

            BitmapImage bitmapImage = invert ? inputImage.invert() : inputImage;

            Map<Character, Glyph> glyphMap = new HashMap<>();

            Vec2D imageSize = bitmapImage.getSize();
            int numberOfGlyphs = numberOfGlyphs(imageSize);

            for (int i = 0; i < numberOfGlyphs; i++) {
                Glyph glyph = new Glyph(
                        bitmapImage.subImage(
                                glyphOffset(imageSize, i),
                                glyphSize
                        )
                );
                glyphMap.put((char) (startingCodepoint + i), glyph);
            }

            return new Font.SimpleFont(
                    glyphSize,
                    glyphMap
            );
        }
    }

}
