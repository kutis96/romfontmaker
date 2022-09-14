package in.spcct.romfontmaker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public interface Font {

    Vec2D getGlyphDimensions();

    default Glyph getNullGlyph() {
        return new Glyph(
                new BitmapImage.NullImage(getGlyphDimensions())
        );
    }

    Map<Character, Glyph> getFontGlyphs();

    // actually used by templates, don't remove.
    default List<Character> getOrderedGlyphKeys() {
        return getFontGlyphs().keySet().stream().sorted().toList();
    }

    Font filter(BiFunction<Character, Glyph, Boolean> filter);

    record SimpleFont(
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

        @Override
        public Font filter(BiFunction<Character, Glyph, Boolean> filter) {
            Map<Character, Glyph> newMap = new HashMap<>();
            glyphMap.forEach((c, g) -> {
                if(filter.apply(c, g))
                    newMap.put(c, g);
            });
            return new SimpleFont(glyphDimensions, newMap);
        }
    }

}
