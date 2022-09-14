package in.spcct.romfontmaker;

import java.util.ArrayList;
import java.util.List;

public record Glyph(BitmapImage bitmapImage) {

    public enum BitOrdering {
        MSB,
        LSB
    }

    // actually used by templates, don't remove.
    public List<Integer> getByteColumns(String bitOrdering) {
        return getByteColumns(BitOrdering.valueOf(bitOrdering));
    }

    public List<Integer> getByteColumns(BitOrdering bitOrdering) {
        /*
         *   AAAAA < LSB/MSB first
         *   A....
         *   A....
         *   AAA..
         *   A....
         *   A....
         *   A....
         *   .....
         *   ^ first byte
         *    ^ second byte
         */

        List<Integer> result = new ArrayList<>();

        for (int yy = 0; yy < bitmapImage.getHeight(); yy += 8) {
            for (int xx = 0; xx < bitmapImage.getWidth(); xx++) {
                int buffer = 0;
                for (int y = 0; y < 8; y++) {
                    Vec2D pos = new Vec2D(xx, yy + y);
                    boolean pixValue = bitmapImage.getPixelValueBool(pos);
                    switch (bitOrdering) {
                        case LSB -> buffer = (buffer << 1) | (pixValue ? 1 : 0);
                        case MSB -> buffer = (buffer >> 1) | (pixValue ? (1 << 7) : 0);
                    }
                }
                result.add(buffer);
            }
        }

        return result;
    }

}
