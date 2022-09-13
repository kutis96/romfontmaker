package in.spcct.romfontmaker;

import java.awt.image.BufferedImage;

public interface BitmapImage {

    Vec2D getSize();

    default int getWidth() {return getSize().x();}
    default int getHeight() {return getSize().y();}

    BitmapImage subImage(Vec2D offset, Vec2D size);

    float getPixelValue(Vec2D position);
    default boolean getPixelValueBool(Vec2D position) {return getPixelValue(position) >= 0.5f;}

    default BufferedImage toBufferedImage() {
        BufferedImage bufferedImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);

        for(int y = 0; y < getHeight(); y++) {
            for(int x = 0; x < getWidth(); x++) {
                float pixelValue = getPixelValue(new Vec2D(x, y));
                int value = (int)(pixelValue * 255);
                bufferedImage.setRGB(x, y, value | value << 8 | value << 16);
            }
        }

        return bufferedImage;
    }

    default BitmapImage invert() {
        BitmapImage parent = this;
        return new BitmapImage() {
            @Override
            public Vec2D getSize() {
                return parent.getSize();
            }

            @Override
            public BitmapImage subImage(Vec2D offset, Vec2D size) {
                return parent.subImage(offset, size).invert();
            }

            @Override
            public float getPixelValue(Vec2D position) {
                return 1f - parent.getPixelValue(position);
            }
        };
    }

    record NullImage (
            Vec2D size
    ) implements BitmapImage {
        @Override
        public Vec2D getSize() {
            return size;
        }

        @Override
        public BitmapImage subImage(Vec2D offset, Vec2D size) {
            return this;
        }

        @Override
        public float getPixelValue(Vec2D position) {
            return 0;
        }
    }

}
