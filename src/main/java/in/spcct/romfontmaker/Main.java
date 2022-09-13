package in.spcct.romfontmaker;

import in.spcct.romfontmaker.impl.BufferedImageBitmapImage;
import in.spcct.romfontmaker.impl.SimpleImporter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Main {

    // shamelessly stolen from https://stackoverflow.com/a/1065014
    static class ImagePanel extends JComponent {
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

    public static void main(String[] args) throws Exception {

        BufferedImage bufferedImage = ImageIO.read(
                new File("C:\\Users\\Spacecat\\Pictures\\font-6x8.png")
//                new File("C:\\Users\\Spacecat\\Pictures\\font-spcct16.png")
//                new File("C:\\Users\\Spacecat\\Pictures\\2pixels.png")
        );

        BitmapImage bitmapImage = new BufferedImageBitmapImage(bufferedImage);

        FontImporter importer = new SimpleImporter();

        Map<String, String> config = new HashMap<>();
        config.put(SimpleImporter.CONFIG_STARTING_CODEPOINT, "0");
        config.put(SimpleImporter.CONFIG_GLYPH_HEIGHT, "8");
        config.put(SimpleImporter.CONFIG_GLYPH_WIDTH, "6");

//        config.put(SimpleImporter.CONFIG_GLYPH_HEIGHT, "16");
//        config.put(SimpleImporter.CONFIG_GLYPH_WIDTH, "8");
        config.put(SimpleImporter.CONFIG_IMAGE_INVERT, "true");

        Font font = importer.importFont(bitmapImage, config);

        JFrame frame = previewImage(
                previewString(
                        font,
                        """
                                This is totally working! Somehow.
                                Test:123.456 78,90
                                More test text stuffs? Yes please!
                                void main(int argc, char* argv) {
                                    printf("Hello, Nyaaanne! :3");
                                }
                                """
                )
        );

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

    }

}
