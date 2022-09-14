package in.spcct.romfontmaker.tools;

import in.spcct.romfontmaker.Font;
import in.spcct.romfontmaker.Glyph;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class PreviewUtils {

    // shamelessly stolen from https://stackoverflow.com/a/1065014
    public static class ImagePanel extends JComponent {
        private final Image image;

        public ImagePanel(Image image) {
            this.image = image;
            setPreferredSize(new Dimension(image.getWidth(null), image.getHeight(null)));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(image, 0, 0, this);
        }
    }

    public static JFrame previewImage(BufferedImage previewImage) {
        JFrame frame = new JFrame("Preview");
        frame.setContentPane(new ImagePanel(previewImage));
        frame.pack();
        return frame;
    }

    public static BufferedImage previewString(Font font, String what) {
        String[] lines = what.split("(\r?\n)|\r");

        if (lines.length == 0)
            return null;

        int maxCharsPerLine = Arrays.stream(lines).map(String::length).mapToInt(Integer::intValue).max().getAsInt();

        BufferedImage returnImage = new BufferedImage(
                maxCharsPerLine * font.getGlyphDimensions().x(),
                lines.length * font.getGlyphDimensions().y(),
                BufferedImage.TYPE_INT_RGB
        );

        Graphics g = returnImage.getGraphics();

        for (int y = 0; y < lines.length; y++) {
            String line = lines[y];
            for (int x = 0; x < line.length(); x++) {
                Glyph glyph = font.getFontGlyphs().getOrDefault(
                        line.charAt(x),
                        font.getNullGlyph()
                );

                g.drawImage(
                        glyph.bitmapImage().toBufferedImage(),
                        x * font.getGlyphDimensions().x(),
                        y * font.getGlyphDimensions().y(),
                        null
                );
            }
        }

        return returnImage;

    }

}
