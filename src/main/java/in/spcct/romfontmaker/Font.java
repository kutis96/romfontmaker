package in.spcct.romfontmaker;

import java.util.Map;

public interface Font {

    Vec2D getGlyphDimensions();

    default Glyph getNullGlyph() {
        return new Glyph(
                new BitmapImage.NullImage(getGlyphDimensions())
        );
    }

    Map<Character, Glyph> getFontGlyphs();


    record SimpleFont (
            Vec2D glyphDimensions,
            Map<Character, Glyph> glyphMap
    ) implements Font {
        @Override
        public Vec2D getGlyphDimensions() {
            return glyphDimensions;
        }

        @Override
        public Map<Character, Glyph> getFontGlyphs() {
            return glyphMap;
        }
    }

}
