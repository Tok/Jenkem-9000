package jenkem.shared;

import com.google.gwt.canvas.dom.client.ImageData;

/**
 * Utility class for getting Information on image content.
 */
public final class ImageUtil {
    private static final double MAX_PERCENT = 100D;
    private static final int MAX_RGB = 255;
    private static final int AVERAGE_RGB = 127;
    private static final int BRIGHTNESS_SLIDER_ZERO_POS = 100;
    private static final int CONTRAST_SLIDER_ZERO_POS = 100;
    private static final int MIN_BRIGHTNESS = -100;
    private static final int MAX_BRIGHTNESS = 100;
    private static final int MIN_CONTRAST = -100;
    private static final int MAX_CONTRAST = 100;
    private static final int BW_TOLERANCE = 25; //arbitrary value (absolute RGB * 3)
    private static final int BW_THRESHOLD = 95; //arbitrary value (relative %)
    private static final int COLOR_TOLERANCE = 20; //arbitrary value (absolute RGB)
    private static final int COLOR_THRESHOLD = 5; //arbitrary value (relative %)
    private static final double BRIGHTNESS_FACTOR = 0.78D; // ~100/128 (relative)
    private static final int BRIGHTNESS_OFFSET = -50; //arbitrary value (relative %)
    private static final double CONTRAST_FACTOR = 0.4D; //arbitrary value (relative)
    private static final int CONTRAST_OFFSET = 20; //arbitrary value (relative %)
    //XXX arbitrary values may need better tuning

    private ImageUtil() { }

    /**
     * Returns the default brightness based on the number of black and white pixels
     * compared to the total pixel count in the image.
     * @param id image data
     * @return default brightness
     */
    public static int getDefaultBrightness(final ImageData id) {
        final int mean = getMeanRgb(id);
        final int factoredDifference = Double.valueOf((AVERAGE_RGB - mean) * BRIGHTNESS_FACTOR).intValue();
        final int differenceWithOffset = factoredDifference + BRIGHTNESS_OFFSET;
        final int result = Math.max(MIN_BRIGHTNESS, Math.min(MAX_BRIGHTNESS, differenceWithOffset));
        return result + BRIGHTNESS_SLIDER_ZERO_POS;
    }

    /**
     * Returns the default contrast based on the mean of the estimated
     * distance of the pixels from the center of the color space cube.
     * @param id image data
     * @return default contrast
     */
    public static int getDefaultContrast(final ImageData id) {
        //bigger value --> the more contrast --> reduce
        final int dev = getMeanDev(id); // 0 < dev < 127 --> 64 ==> 0
        final int fixedDev = Double.valueOf(AVERAGE_RGB / 2D).intValue() - dev;
        final int factoredDev = Double.valueOf(fixedDev * CONTRAST_FACTOR).intValue();
        final int devWithOffset = factoredDev + CONTRAST_OFFSET;
        final int result = Math.max(MIN_CONTRAST, Math.min(MAX_CONTRAST, devWithOffset));
        return result + CONTRAST_SLIDER_ZERO_POS;
    }

    /**
     * Returns the default Brightness
     * @param id image data
     * @return default conversion method
     */
    public static ConversionMethod getDefaultMethod(final ImageData id) {
        final int pixelCount = id.getHeight() * id.getWidth();
        int countBw = 0;
        for (int y = 0; y < id.getHeight(); y++) {
            for (int x = 0; x < id.getWidth(); x++) {
                if (isBlackOrWhitePixel(id, x, y)) { countBw++; }
            }
        }
        final int bwRatio = Double.valueOf(countBw * MAX_PERCENT / pixelCount).intValue();
        return bwRatio < BW_THRESHOLD ? ConversionMethod.SuperHybrid : ConversionMethod.Plain;
    }

    /**
     * Returns the default ColorScheme based on how colorful
     * the provided image is.
     * @param id image data
     * @return default color scheme
     */
    public static ColorScheme getDefaultColorScheme(final ImageData id) {
        final int pixelCount = id.getHeight() * id.getWidth();
        int countColorful = 0;
        for (int y = 0; y < id.getHeight(); y++) {
            for (int x = 0; x < id.getWidth(); x++) {
                if (isPixelColorful(id, x, y)) { countColorful++; }
            }
        }
        final int colorRatio = Double.valueOf(countColorful * MAX_PERCENT / pixelCount).intValue();
        return colorRatio > COLOR_THRESHOLD ? ColorScheme.Default : ColorScheme.Bwg;
    }

    /**
     * Returns the mean RGB value of all pixels in the image.
     * @param id image data
     * @return mean RGB of all pixels in the provided image
     */
    private static int getMeanRgb(final ImageData id) {
        final long pixelCount = id.getHeight() * id.getWidth();
        long sum = 0L;
        for (int y = 0; y < id.getHeight(); y++) {
            for (int x = 0; x < id.getWidth(); x++) {
                sum += getMeanRgbFromPixel(id, x, y);
            }
        }
        return Double.valueOf(sum / pixelCount).intValue();
    }

    private static int getMeanDev(final ImageData id) {
        final long pixelCount = id.getHeight() * id.getWidth();
        long sum = 0L;
        for (int y = 0; y < id.getHeight(); y++) {
            for (int x = 0; x < id.getWidth(); x++) {
                sum += getRgbDeviationFromPixel(id, x, y);
            }
        }
        return Double.valueOf(sum / pixelCount).intValue();
    }

    /**
     * Calculates the average darkness of a pixel.
     * @param id ImageData image with the pixel to calculate
     * @param x horizontal coordinate
     * @param y vertical coordinate
     * @return darkness of the selected pixel
     */
    public static int getMeanRgbFromPixel(final ImageData id, final int x, final int y) {
        final int sum = getPixelRgbSum(id, x, y);
        return Double.valueOf(Math.round(sum / 3D)).intValue();
    }

    private static int getPixelRgbSum(final ImageData id, final int x, final int y) {
        return id.getRedAt(x, y) + id.getGreenAt(x, y) + id.getBlueAt(x, y);
    }

    /**
     * Returns true if the pixel at the provided coordinates is
     * relatively colorful.
     * @param id ImageData image with the pixel to calculate
     * @param x horizontal coordinate
     * @param y vertical coordinate
     * @return isPixelColorful
     */
    private static boolean isPixelColorful(final ImageData id, final int x, final int y) {
        final int r = id.getRedAt(x, y);
        final int g = id.getGreenAt(x, y);
        final int b = id.getBlueAt(x, y);
        return Math.abs(r - g) > COLOR_TOLERANCE
                || Math.abs(r - b) > COLOR_TOLERANCE
                || Math.abs(g - b) > COLOR_TOLERANCE;
    }

    private static int getRgbDeviationFromPixel(final ImageData id, final int x, final int y) {
        final int deviations =
                  Math.abs(id.getRedAt(x, y) - AVERAGE_RGB)
                + Math.abs(id.getGreenAt(x, y) - AVERAGE_RGB)
                + Math.abs(id.getBlueAt(x, y) - AVERAGE_RGB);
        return Double.valueOf(Math.round(deviations / 3D)).intValue();
    }

    private static boolean isBlackOrWhitePixel(final ImageData id, final int x, final int y) {
        final int sum = getPixelRgbSum(id, x, y);
        return sum > (MAX_RGB * 3) - BW_TOLERANCE || sum < BW_TOLERANCE;
    }
}
