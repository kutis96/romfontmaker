package in.spcct.romfontmaker.impl;

import in.spcct.romfontmaker.BitmapImage;
import in.spcct.romfontmaker.Vec2D;

import java.awt.image.BufferedImage;

public class BufferedImageBitmapImage implements BitmapImage {

    private final BufferedImage backingImage;

    public BufferedImageBitmapImage(BufferedImage backingImage) {
        this.backingImage = backingImage;
    }

    @Override
    public Vec2D getSize() {
        return new Vec2D(backingImage.getWidth(), backingImage.getHeight());
    }

    @Override
    public BitmapImage subImage(Vec2D offset, Vec2D size) {
        //TODO: reuse current image instead
        return new BufferedImageBitmapImage(
                backingImage.getSubimage(offset.x(), offset.y(), size.x(), size.y())
        );
    }

    @Override
    public float getPixelValue(Vec2D position) {
        int rgb = backingImage.getRGB(position.x(), position.y());
        return rgbToValue(rgb);
    }

    private float rgbToValue(int rgb) {
        //TODO: Implement something smarter with color models and magic

        // shamelessly stolen from https://stackoverflow.com/a/21210977
        int red   = (rgb >>> 16) & 0xFF;
        int green = (rgb >>>  8) & 0xFF;
        int blue  = (rgb) & 0xFF;

        // calc luminance in range 0.0 to 1.0; using SRGB luminance constants
        return (red * 0.2126f + green * 0.7152f + blue * 0.0722f) / 255;
    }
}
