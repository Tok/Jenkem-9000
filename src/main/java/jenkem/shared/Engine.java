package jenkem.shared;

import java.util.Map;
import jenkem.shared.color.Color;
import jenkem.shared.color.ColorUtil;
import jenkem.shared.color.Cube;
import jenkem.shared.color.IrcColor;
import jenkem.shared.color.Sample;
import com.google.gwt.canvas.dom.client.ImageData;

/**
 * Makes the conversion to ASCII.
 */
public class Engine {
    private static final int CENTER = 127;

    private final Cube cube = new Cube();
    private final AsciiScheme asciiScheme = new AsciiScheme();
    private final ColorUtil colorUtil = new ColorUtil();

    private Map<IrcColor, Integer> colorMap;
    private ImageData id;
    private CharacterSet preset;
    private ProcessionSettings settings = new ProcessionSettings();
    private int contrast;
    private int brightness;

    private int startX;
    private int startY;

    /**
     * Prepares Engine.
     * @param id the ImageData
     * @param preset the CharacterSet to use
     * @param contrast value will be multiplied with the rgb of each pixel
     * @param brightness value will be added to the rgb of each pixel
     * @param settings
     */
    public final void setParams(final ImageData id, final CharacterSet preset, final Kick kick,
            final int contrast, final int brightness, final ProcessionSettings settings) {
        this.id = id;
        this.preset = preset;
        this.contrast = contrast;
        this.brightness = brightness;
        this.settings = settings;
        applyKicks(kick);
    }

    /**
     * Generates a line in HD mode and adds it to the presenter, which will
     * recall this method again with an increased y until all data is converted.
     * @param index row of pixels in the ImageData to convert
     */
    public final String generateHighDefLine(final int index) {
        final StringBuilder line = new StringBuilder();
        String oldPix;
        String newPix = null;
        for (int x = 0; x < id.getWidth(); x++) { // this method can handle uneven image widths
            final int[] rgb = Sample.calculateRgb(id, x, index, contrast, brightness);
            oldPix = newPix;
            newPix = cube.getColorChar(colorMap, preset, rgb, false);
            if (newPix.equals(oldPix)) { // don't change color, add char only
                line.append(newPix.substring(newPix.length() - 1, newPix.length()));
            } else { // do color change
                line.append(ColorUtil.CC); // adds the new CC
                line.append(newPix); // and the new character
            }
        }
        line.append(ColorUtil.CC); // closes the last CC in the line
        return ColorUtil.makeBlocksValid(line.toString());
    }

    /**
     * Generates a line in super hybrid mode and adds it to the presenter, which
     * will recall this method again with an increased y until all data is
     * converted.
     * @param index row of pixels in the ImageData to convert
     */
    public final String generateSuperHybridLine(final int index) {
        final StringBuilder row = new StringBuilder();
        String oldLeft;
        String newLeft = null;
        String newRight = null;
        for (int x = startX; x < getEvenWidth(); x = x + 2) {
            // this method can handle uneven image widths
            final Sample sample = Sample.getInstance(id, x, index, contrast, brightness);

            oldLeft = newLeft;
            newLeft = cube.getColorChar(colorMap, preset, sample, Sample.Xdir.LEFT);
            newRight = cube.getColorChar(colorMap, preset, sample, Sample.Xdir.RIGHT);

            final int[] leftRgb = sample.getRgbValues(Sample.Xdir.LEFT);
            final int[] rightRgb = sample.getRgbValues(Sample.Xdir.RIGHT);
            final int[] leftTopRgb = sample.getRgbValues(Sample.Ydir.TOP, Sample.Xdir.LEFT);
            final int[] leftBottomRgb = sample.getRgbValues(Sample.Ydir.BOT, Sample.Xdir.LEFT);
            final int[] rightTopRgb = sample.getRgbValues(Sample.Ydir.TOP, Sample.Xdir.RIGHT);
            final int[] rightBottomRgb = sample.getRgbValues(Sample.Ydir.BOT, Sample.Xdir.RIGHT);
            final Color leftCol = cube.getTwoNearestColors(colorMap, leftRgb);
            final Color rightCol = cube.getTwoNearestColors(colorMap, rightRgb);
            final Color leftTopCol = cube.getTwoNearestColors(colorMap, leftTopRgb);
            final Color leftBottomCol = cube.getTwoNearestColors(colorMap, leftBottomRgb);
            final Color rightTopCol = cube.getTwoNearestColors(colorMap, rightTopRgb);
            final Color rightBottomCol = cube.getTwoNearestColors(colorMap, rightBottomRgb);
            final int offset = settings.getOffset();
            if (cube.isFirstCloserTo(leftBottomCol.getRgb(), leftTopCol.getRgb(), leftCol.getFgRgb(), offset)) {
                newLeft = newLeft.substring(0, newLeft.length() - 1) + asciiScheme.selectDown(); // _
            } else if (cube.isFirstCloserTo(leftTopCol.getRgb(), leftBottomCol.getRgb(), leftCol.getFgRgb(), offset)) {
                newLeft = newLeft.substring(0, newLeft.length() - 1) + asciiScheme.selectUp(); // "
            }
            if (cube.isFirstCloserTo(rightBottomCol.getRgb(), rightTopCol.getRgb(), rightCol.getFgRgb(), offset)) {
                newRight = newRight.substring(0, newRight.length() - 1) + asciiScheme.selectDown(); // _
            } else if (cube.isFirstCloserTo(rightTopCol.getRgb(), rightBottomCol.getRgb(), rightCol.getFgRgb(), offset)) {
                newRight = newRight.substring(0, newRight.length() - 1) + asciiScheme.selectUp(); // "
            }

            if (newLeft.equals(oldLeft)) { //char only
                row.append(newLeft.substring(newLeft.length() - 1, newLeft.length()));
            } else {
                row.append(ColorUtil.CC);
                row.append(newLeft);
            }
            if (newRight.equals(newLeft)) { //char only
                row.append(newRight.substring(newRight.length() - 1, newRight.length()));
            } else {
                row.append(ColorUtil.CC);
                row.append(newRight);
            }
        }
        row.append(ColorUtil.CC);
        return colorUtil.postProcessColoredRow(row.toString(), preset, settings);
    }

    /**
     * Generates a line in hybrid mode and adds it to the presenter, wich will
     * recall this method again with an increased y until all data is converted.
     * @param index row of pixels in the ImageData to convert
     */
    public final String generateHybridLine(final int index) {
        final double offsetModifier = 16D;
        final StringBuilder line = new StringBuilder();
        String oldLeft;
        String newLeft = null;
        String newRight = null;
        for (int x = startX; x < id.getWidth() - 1; x = x + 2) {
            final Sample sample = Sample.getInstance(id, x, index, contrast, brightness);

            oldLeft = newLeft;
            @SuppressWarnings("unused")
            // TODO reimplement foreground enforcement
            final boolean isEnforceBlackFg = false;
            newLeft = cube.getColorChar(colorMap, preset, sample, Sample.Xdir.LEFT);
            newRight = cube.getColorChar(colorMap, preset, sample, Sample.Xdir.RIGHT);
            if (asciiScheme.isCharacterBright(newLeft, preset)
                    && asciiScheme.isCharacterDark(newRight, preset)) {
                newLeft = asciiScheme.replace(newLeft, asciiScheme.selectVline());
            }
            if (asciiScheme.isCharacterDark(newLeft, preset)
                    && asciiScheme.isCharacterBright(newRight, preset)) {
                newRight = asciiScheme.replace(newRight, asciiScheme.selectVline());
            }
            // XXX tune this
            final int offset = settings.getOffset();
            final int downOffset = Double.valueOf(offset * 2D / 3D).intValue();
            final int upOffset = Double.valueOf(offset * 2D / 3D).intValue();
            final int genUpDownOffset = Double.valueOf(offset / 8D).intValue();
            final int downUpOffset = Double.valueOf(offset / offsetModifier).intValue();
            final int upDownOffset = Double.valueOf(offset / offsetModifier).intValue();
            final int[] topRgb = sample.getRgbValues(Sample.Ydir.TOP);
            final int[] botRgb = sample.getRgbValues(Sample.Ydir.BOT);
            if (isUp(topRgb, botRgb, upOffset)) {
                if (asciiScheme.isCharacterDark(newRight, preset)) {
                    newLeft = newLeft.substring(0, newLeft.length() - 1)
                            + asciiScheme.selectRightUp(); // y7
                } else {
                    newLeft = newLeft.substring(0, newLeft.length() - 1)
                            + asciiScheme.selectUp(); // "
                }
            } else if (isDown(topRgb, botRgb, downOffset)) {
                if (asciiScheme.isCharacterDark(newRight, preset)) {
                    newLeft = newLeft.substring(0, newLeft.length() - 1)
                            + asciiScheme.selectRightDown(); // j
                } else {
                    newLeft = newLeft.substring(0, newLeft.length() - 1)
                            + asciiScheme.selectDown(); // _
                }
            }
            if (isUp(topRgb, botRgb, upOffset)) {
                if (asciiScheme.isCharacterDark(newLeft, preset)) {
                    newRight = newRight.substring(0, newRight.length() - 1)
                            + asciiScheme.selectLeftUp(); // F
                } else {
                    newRight = newRight.substring(0, newRight.length() - 1)
                            + asciiScheme.selectUp(); // "
                }
            } else if (isDown(topRgb, botRgb, downOffset)) {
                if (asciiScheme.isCharacterDark(newLeft, preset)) {
                    newRight = newRight.substring(0, newRight.length() - 1)
                            + asciiScheme.selectLeftDown(); // L
                } else {
                    newRight = newRight.substring(0, newRight.length() - 1)
                            + asciiScheme.selectDown(); // _
                }
            }
            final int[] leftTopRgb = sample.getRgbValues(Sample.Ydir.TOP, Sample.Xdir.LEFT);
            final int[] leftBottomRgb = sample.getRgbValues(Sample.Ydir.BOT, Sample.Xdir.LEFT);
            final int[] rightTopRgb = sample.getRgbValues(Sample.Ydir.TOP, Sample.Xdir.RIGHT);
            final int[] rightBottomRgb = sample.getRgbValues(Sample.Ydir.BOT, Sample.Xdir.RIGHT);
            if (isUp(leftTopRgb, leftBottomRgb, genUpDownOffset)
                    && isDown(rightTopRgb, rightBottomRgb, genUpDownOffset)
                    && isDown(leftTopRgb, leftBottomRgb, genUpDownOffset)
                    && isUp(rightTopRgb, rightBottomRgb, genUpDownOffset)) {
                newLeft = newLeft.substring(0, newLeft.length() - 1)
                        + asciiScheme.selectLeft(); // <[(
                newRight = newRight.substring(0, newRight.length() - 1)
                        + asciiScheme.selectRight(); // >])
            } else {
                if (isUp(rightTopRgb, leftBottomRgb, upDownOffset)
                        && isDown(rightTopRgb, rightBottomRgb, upDownOffset)) {
                    newLeft = newLeft.substring(0, newLeft.length() - 1)
                            + asciiScheme.selectUpDown().substring(0, 1); // \\"_',
                    newRight = newRight.substring(0, newRight.length() - 1)
                            + asciiScheme.selectUpDown().substring(1, 2); // \\"_',
                }
                if (isDown(leftTopRgb, leftBottomRgb, downUpOffset)
                        && isUp(rightTopRgb, rightBottomRgb, downUpOffset)) {
                    newLeft = newLeft.substring(0, newLeft.length() - 1)
                            + asciiScheme.selectDownUp().substring(0, 1); // //_".'
                    newRight = newRight.substring(0, newRight.length() - 1)
                            + asciiScheme.selectDownUp().substring(1, 2); // //_".'
                }
            }
            if (newLeft.equals(oldLeft)) { // char only
                line.append(newLeft.substring(newLeft.length() - 1, newLeft.length()));
            } else {
                line.append(ColorUtil.CC);
                line.append(newLeft);
            }
            if (newRight.equals(newLeft)) { // char only
                line.append(newRight.substring(newRight.length() - 1, newRight.length()));
            } else {
                line.append(ColorUtil.CC);
                line.append(newRight);
            }
        }
        line.append(ColorUtil.CC);
        return colorUtil.postProcessColoredRow(line.toString(), preset, settings);
    }

    /**
     * Generates a line in Pwntari mode and adds it to the presenter, which will
     * recall this method again with an increased y until all data is converted.
     * @param index row of pixels in the ImageData to convert
     */
    public final String generatePwntariLine(final int index) {
        final StringBuilder line = new StringBuilder();
        String oldLeft;
        String newLeft = null;
        String newRight = null;
        for (int x = startX; x < id.getWidth() - 1; x = x + 2) {
            final Sample sample = Sample.getInstance(id, x, index, contrast, brightness);
            oldLeft = newLeft;
            newLeft = cube.getColorChar(colorMap, preset, sample, Sample.Xdir.LEFT);
            newRight = cube.getColorChar(colorMap, preset, sample, Sample.Xdir.RIGHT);
            newLeft = newLeft.substring(0, newLeft.length() - 1) + asciiScheme.selectDown(); // _
            newRight = newRight.substring(0, newRight.length() - 1) + asciiScheme.selectDown(); // _
            if (newLeft.equals(oldLeft)) {
                line.append(newLeft.substring(newLeft.length() - 1, newLeft.length()));
            } else {
                line.append(ColorUtil.CC);
                line.append(newLeft);
            }
            if (newRight.equals(newLeft)) {
                line.append(newRight.substring(newRight.length() - 1, newRight.length()));
            } else {
                line.append(ColorUtil.CC);
                line.append(newRight);
            }
        }
        line.append(ColorUtil.CC);
        // postProcession is pointless for Pwntari mode.
        return ColorUtil.makeBlocksValid(line.toString());
    }

    /**
     * Generates a line in Plain mode and adds it to the presenter, which will
     * recall this method again with an increased y until all data is converted.
     * @param index row of pixels in the ImageData to convert
     */
    public final String generatePlainLine(final int index) {
        final StringBuilder line = new StringBuilder();
        for (int x = startX; x < id.getWidth() - 1; x += ConversionMethod.Plain.getStep()) {
            final int topLeft = Sample.calculateColor(getMeanRgbFromImage(id, x, index), contrast, brightness);
            final int bottomLeft = Sample.calculateColor(getMeanRgbFromImage(id, x, index + 1), contrast, brightness);
            final int topRight = Sample.calculateColor(getMeanRgbFromImage(id, x + 1, index), contrast, brightness);
            final int bottomRight = Sample.calculateColor(getMeanRgbFromImage(id, x + 1, index + 1), contrast, brightness);
            String charPixel = "";
            // 1st char
            if (topLeft <= CENTER && bottomLeft > CENTER) {
                charPixel = asciiScheme.getUp();
            } else if (topLeft > CENTER && bottomLeft <= CENTER) {
                charPixel = asciiScheme.getDown();
            } else {
                charPixel = asciiScheme.getChar((topLeft + bottomLeft) / 2D, preset, AsciiScheme.StrengthType.ABSOLUTE);
            }
            // 2nd char
            if (topRight <= CENTER && bottomRight > CENTER) {
                charPixel += asciiScheme.getUp();
            } else if (topRight > CENTER && bottomRight <= CENTER) {
                charPixel += asciiScheme.getDown();
            } else {
                charPixel += asciiScheme.getChar((topRight + bottomRight) / 2D, preset, AsciiScheme.StrengthType.ABSOLUTE);
            }
            line.append(charPixel);
        }
        return colorUtil.postProcessRow(line.toString(), preset, settings);
    }

    /**
     * Returns the width of the imageData or 1 less if it isn't even. This is
     * done because some conversion methods cannot handle uneven numbers.
     * @return corrected width.
     */
    private int getEvenWidth() {
        return id.getWidth() - ((id.getWidth() % 2 == 0) ? 0 : 1);
    }

    /**
     * Calculates the average darkness of a pixel.
     * @param id ImageData image with the pixel to calculate
     * @param x horizontal coordinate
     * @param y vertical coordinate
     * @return darkness of the selected pixel
     */
    private static int getMeanRgbFromImage(final ImageData id, final int x, final int y) {
        final double sum = id.getRedAt(x, y) + id.getGreenAt(x, y) + id.getBlueAt(x, y);
        return Double.valueOf(Math.round(sum / 3D)).intValue();
    }

    /**
     * Decides if top is darker than bottom.
     * @param top rgb values of the top pixels
     * @param bottom rgb values of the bottom pixels
     * @param offset for calculation
     * @return true if top is darker than top
     */
    private boolean isUp(final int[] top, final int[] bottom, final int offset) {
        return ((top[0] + top[1] + top[2]) / 3) <= (CENTER + offset)
                && ((bottom[0] + bottom[1] + bottom[2]) / 3) > (CENTER - offset);
    }

    /**
     * Decides if bottom is darker than bottom.
     * @param top rgb values of the top pixels
     * @param bottom rgb values of the bottom pixels
     * @param offset for calculation
     * @return true if bottom is darker than top
     */
    private boolean isDown(final int[] top, final int[] bottom, final int offset) {
        return ((top[0] + top[1] + top[2]) / 3) > (CENTER - offset)
                && ((bottom[0] + bottom[1] + bottom[2]) / 3) <= (CENTER + offset);
    }

    /**
     * Applies kick for modes with a kick-option. the loop-counters are
     * initialized according to the 4 possible kicks. The image is looped 2 rows
     * and 2 columns at a time giving 4 pixels (compare Sample class) to examine
     * inside the loop. everything is generated by examining the relations of
     * the color values between those 4 pixels which is why the kick option even
     * works. (a good anti-aliasing algorithm on the ASCII level would defeat
     * this purpose)
     *   X
     * Y +------------>
     *   | ## ## ##
     *   | ## ## ##
     *   | ## ## ##
     *   | ## ## ##
     *   v
     * @param kick the selected Kick
     */
    private void applyKicks(final Kick kick) {
        this.startX = getKickedX(kick);
        this.startY = getKickedY(kick);
    }

    /**
     * Decides if x is kicked.
     * @param kick the selected Kick
     * @return 1 or 0
     */
    private int getKickedX(final Kick kick) {
        return (kick.equals(Kick.X) || kick.equals(Kick.XY)) ? 1 : 0;
    }

    /**
     * Decides if y is kicked.
     * @param kick the selected Kick
     * @return 1 or 0
     */
    private int getKickedY(final Kick kick) {
        return (kick.equals(Kick.Y) || kick.equals(Kick.XY)) ? 1 : 0;
    }

    public final int getStartY() {
        return startY;
    }

    public final void prepareEngine(final Map<IrcColor, Integer> colorMap, final Power power) {
        this.colorMap = colorMap;
        cube.setPower(power);
    }
}
