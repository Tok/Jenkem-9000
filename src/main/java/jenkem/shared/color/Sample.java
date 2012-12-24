package jenkem.shared.color;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the RGBcolors of four pixels from the provided image.
 * TL | TR
 * ---+---
 * BL | BR
 */
public final class Sample {
    private static final int MAP_CAPACITY = 12;
    private static final int TOTAL_PERCENT = 100;
    public static final int MAX_RGB = 255;
    public static final int HALF_RGB = 127;

    public enum Col { RED, GREEN, BLUE; }
    public enum Ydir { TOP, BOT; }
    public enum Xdir { LEFT, RIGHT; }

    private final Map<String, Integer> values = new HashMap<String, Integer>(MAP_CAPACITY);

    public static final class SampleKey {
        private final Col col;
        private final Ydir ydir;
        private final Xdir xdir;

        private SampleKey(final Col col, final Ydir ydir, final Xdir xdir) {
            this.col = col;
            this.ydir = ydir;
            this.xdir = xdir;
        }

        public static String getKey(final Col col, final Ydir ydir, final Xdir xdir) {
            return new SampleKey(col, ydir, xdir).toString();
        }

        @Override public String toString() { return col.name() + ydir.name() + xdir.name(); }
    }

    public static Sample getInstance(final Map<String, Integer[]> imageRgb, final int x,
            final int y, final int contrast, final int brightness, final int width) {
        return new Sample(imageRgb, x, y, contrast, brightness, width);
    }

    private Sample(final Map<String, Integer[]> imageRgb, final int x, final int y,
            final int contrast, final int brightness, final int width) {
        for (final Col col : Col.values()) {
            values.put(SampleKey.getKey(col, Ydir.TOP, Xdir.LEFT),
                    takeColor(imageRgb, col, x, y, contrast, brightness));
            values.put(SampleKey.getKey(col, Ydir.BOT, Xdir.LEFT),
                    takeColor(imageRgb, col, x, y + 1, contrast, brightness));
            if (width >= x + 1) {
                values.put(SampleKey.getKey(col, Ydir.TOP, Xdir.RIGHT),
                        takeColor(imageRgb, col, x + 1, y, contrast, brightness));
                values.put(SampleKey.getKey(col, Ydir.BOT, Xdir.RIGHT),
                        takeColor(imageRgb, col, x + 1, y + 1, contrast, brightness));
            } else { // fallback by using the pixel to the left
                values.put(SampleKey.getKey(col, Ydir.TOP, Xdir.RIGHT),
                        takeColor(imageRgb, col, x, y, contrast, brightness));
                values.put(SampleKey.getKey(col, Ydir.BOT, Xdir.RIGHT),
                        takeColor(imageRgb, col, x, y + 1, contrast, brightness));
            }
        }
    }

    public int get(final Col col, final Ydir yDir, final Xdir xDir) {
        final String key = SampleKey.getKey(col, yDir, xDir);
        return values.containsKey(key) ? values.get(key) : 0;
    }

    public int get(final Col col, final Xdir xDir) {
        final String firstKey = SampleKey.getKey(col, Ydir.TOP, xDir);
        final String secondKey = SampleKey.getKey(col, Ydir.BOT, xDir);
        return getVal(firstKey, secondKey);
    }

    public int get(final Col col, final Ydir yDir) {
        final String firstKey = SampleKey.getKey(col, yDir, Xdir.LEFT);
        final String secondKey = SampleKey.getKey(col, yDir, Xdir.RIGHT);
        return getVal(firstKey, secondKey);
    }

    private int getVal(final String firstKey, final String secondKey) {
        if (values.containsKey(firstKey)) {
            return Double.valueOf(values.get(firstKey)
                    + (values.containsKey(secondKey) ? values.get(secondKey) : 0) / 2D).intValue();
        } else {
            return values.containsKey(secondKey) ? Double.valueOf(values.get(secondKey) / 2D).intValue() : 0;
        }
    }

    public int[] getRgbValues(final Xdir xDir) {
        final int[] rgb = {get(Col.RED, xDir), get(Col.GREEN, xDir), get(Col.BLUE, xDir)};
        return rgb;
    }

    public int[] getRgbValues(final Ydir yDir) {
        final int[] rgb = {get(Col.RED, yDir), get(Col.GREEN, yDir), get(Col.BLUE, yDir)};
        return rgb;
    }

    public int[] getRgbValues(final Ydir yDir, final Xdir xDir) {
        final int[] rgb = {get(Col.RED, yDir, xDir), get(Col.GREEN, yDir, xDir), get(Col.BLUE, yDir, xDir)};
        return rgb;
    }

    /**
     * Calculates the RGB values of the pixel in the provided imageRgb at
     * position x, y and applies the provided contrast and brightness.
     * @param id imageRgb
     * @param x the target pixel column
     * @param y the target pixel row
     * @param contrast is multiplied with the result
     * @param brightness is added to the result
     * @return an int[] of size 3 with the values for red, green and blue.
     */
    public static int[] calculateRgb(final Map<String, Integer[]> imageRgb, final int x,
            final int y, final int contrast, final int brightness) {
        final Integer[] rgb = imageRgb.get(y + ":" + x);
        final int[] result = {
                calculateColor(rgb[0], contrast, brightness),
                calculateColor(rgb[1], contrast, brightness),
                calculateColor(rgb[2], contrast, brightness) };
        return result;
    }

    private static int takeColor(final Map<String, Integer[]> imageRgb, final Col col,
            final int x, final int y, final int contrast,
            final int brightness) {
        final Integer[] rgb = imageRgb.get(y + ":" + x);
        if (col.equals(Col.RED)) {
            return calculateColor(rgb[0], contrast, brightness);
        } else if (col.equals(Col.GREEN)) {
            return calculateColor(rgb[1], contrast, brightness);
        } else if (col.equals(Col.BLUE)) {
            return calculateColor(rgb[2], contrast, brightness);
        }
        throw new IllegalArgumentException("Illegal color.");
    }

    /**
     * Applies the contrast and brightness to the provided value and makes sure
     * that the result is kept in range.
     * @param input the rgb value
     * @param contrast relative contrast between -100 and +100
     * @param brightness is added to the rgb value of the pixel
     * @return int the correted value
     */
    public static int calculateColor(final int input, final int contrast, final int brightness) {
        return keepInRange(correctDistance(input, contrast) + brightness);
    }

    /**
     * Applies contrast
     * @param input R, G, or B value between 0 and 255
     * @param contrast relative contrast between -100 and +100
     * @return
     */
    private static int correctDistance(final int input, final int contrast) {
        final int distanceFromCenter = input < HALF_RGB ? HALF_RGB - input : input - HALF_RGB;
        final double contrastedDist = distanceFromCenter * (1D + (Double.valueOf(contrast) / TOTAL_PERCENT));
        return Double.valueOf(input < HALF_RGB ? HALF_RGB - contrastedDist : HALF_RGB + contrastedDist).intValue();
    }

    /**
     * Makes sure that the provided color is kept in range between 0 and 255.
     * @param colorComponent value after correction
     * @return value of the provided colorComponent between 0 and 255
     */
    public static int keepInRange(final int colorComponent) {
        return Math.max(0, Math.min(colorComponent, MAX_RGB));
    }

}
