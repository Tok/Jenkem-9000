package jenkem.shared;

import com.google.gwt.canvas.dom.client.ImageData;

/**
 * Utility class for getting Information on image content.
 */
public final class ImageUtil {
    private static final int MAX_RGB = 255;
    private static final int AVERAGE_RGB = 127;
    private static final int BW_TOLERANCE = 25; //arbitrary value
    private static final int BW_THRESHOLD = 95; //arbitrary value
    private static final double MAX_PERCENT = 100D;
    private static final int BRIGHTNESS_SLIDER_ZERO_POS = 100;
    private static final int MIN_BRIGHTNESS = -100;
    private static final int MAX_BRIGHTNESS = 100;
    private static final double BRIGHTNESS_FACTOR = 0.78D; // ~100/128
    private static final int BRIGHTNESS_OFFSET = -40; //arbitrary value

    private ImageUtil() { }

    /**
     * Returns the default Brightness based on the number of black and white pixels
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
     * Returns the default Brightness
     * @param id image data
     * @return default brightness
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
     * Returns the mean RGB value of all pixels in the image.
     * @param id image data
     * @return mean RGB of all pixels in the provided image
     */
    private static int getMeanRgb(final ImageData id) {
        long sum = 0L;
        long pixelCount = 0L;
        for (int y = 0; y < id.getHeight(); y++) {
            for (int x = 0; x < id.getWidth(); x++) {
                final int pixelMean = getMeanRgbFromPixel(id, x, y);
                sum += pixelMean;
                pixelCount++;
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

    @SuppressWarnings("unused")
    private static int getRgbDeviationFromPixel(final ImageData id, final int x, final int y) {
        final int mean = getMeanRgbFromPixel(id, x, y);
        final int deviations = (id.getRedAt(x, y) - mean)
                + (id.getGreenAt(x, y) - mean)
                + (id.getBlueAt(x, y) - mean);
        final int meanDev = Double.valueOf(Math.round(deviations / 3D)).intValue();
        return meanDev;
    }

    private static boolean isBlackOrWhitePixel(final ImageData id, final int x, final int y) {
        final int sum = getPixelRgbSum(id, x, y);
        return sum > (MAX_RGB * 3) - BW_TOLERANCE || sum < BW_TOLERANCE;
    }
}
