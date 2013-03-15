package jenkem.shared.color;


/**
 * Just some constants and stuff.
 */
public class ColorUtil {
    public static final String BC = String.valueOf('\u0002'); // bold character for IRC
    public static final String CC = String.valueOf('\u0003'); // color character for IRC
    public static final int MAX_RGB = 255;
    private static final String CC_MATCHER = "[" + CC + "][0-9][0-9]?[,][0-9][0-9]?";
    private static final String CC_BLOCK_MATCHER = CC_MATCHER + "[^" + CC + ",[0-9]]+";

    /**
     * Colors the entered String black or white, depending on the provided
     * background color. This method is used to output the color config into IRC
     * @param col the color for the background (fg will become white or black)
     * @param input the message to color
     * @return coloredMessage
     */
    public final String colorConfig(final Integer col, final String input) {
        final IrcColor ic = IrcColor.getFor(col);
        return concatColorConfig(ic.isDark() ? IrcColor.white.getValue() : IrcColor.black.getValue(), col, input + "% ");
    }

    /**
     * Concatinates the IRC style colors.
     * @param fg
     * @param bg
     * @param input
     * @return concatinatedColors
     */
    private String concatColorConfig(final Integer fg, final Integer bg, final String input) {
        return CC + fg + "," + bg + " " + input + CC;
    }

    /**
     * This method is used to add the bold code in front of every line in the
     * ircOutput.
     * @param ircOutput String[] with the lines to forward into IRC
     * @return String[] with every line preceded by bold code character
     */
    public final String[] makeBold(final String[] ircOutput) {
        final String[] result = new String[ircOutput.length];
        for (int i = 0; i < ircOutput.length; i++) {
            result[i] = BC + ircOutput[i];
        }
        return result;
    }

    /**
     * Converts an IRC color to a CSS color.
     * @param ircColor
     * @return cssColor
     */
    public final String ircToCss(final String ircColor) {
        return ircToCss(Integer.valueOf(ircColor));
    }

    /**
     * Converts an IRC color to a CSS color.
     * @param ircColor
     * @return cssColor
     */
    public final String ircToCss(final int ircColor) {
        return rgbToCss(IrcColor.getFor(ircColor).getRgb());
    }

    /**
     * Converts an RGB color to a CSS color.
     * @param rgb
     * @return cssColor
     */
    private static String rgbToCss(final int[] rgb) {
        return "#" + toHex(rgb[0]) + toHex(rgb[1]) + toHex(rgb[2]);
    }

    /**
     * Converts the provided interger to hex.
     * @param i
     * @return hex
     */
    public static String toHex(final int i) {
        final String ret = Integer.toHexString(i);
        return ret.length() == 1 ? "0" + ret : ret;
    }

    /**
     * Tests if the provided String is a color info for IRC
     * @param test
     * @return isColorInfo
     */
    public static boolean isColorInfo(final String test) {
        return test.matches("^" + CC_MATCHER + "$");
    }

    /**
     * Tests if the provided IRC String is legal and starts with a color info
     * for IRC
     * @param test
     * @return isLegalColorBlock
     */
    public static boolean isLegalColorBlock(final String test) {
        return test.matches("^" + CC_BLOCK_MATCHER + "$");
    }

    /**
     * Removes duplicated CC's and CC's not followed by numbers.
     * @param in
     * @return valid
     */
    public static String makeBlocksValid(final String in) {
        final String valid = removeInvalidCc(in);
        final String noDoubles = removeDoubleCc(valid);
        return noDoubles;
    }

    /**
     * Removes CC's not followed by numbers.
     * @param in
     * @return fixed
     */
    public static String removeInvalidCc(final String in) {
        // No Pattern and Matcher in GWT client -> String methods used.
        final StringBuffer result = new StringBuffer();
        final String[] splitted = in.split(CC);
        for (final String block : splitted) {
            if (block.isEmpty()) {
                continue; // ignore
            } else if (!block.substring(0, 1).matches("[^0-9]")) {
                result.append(CC); // preserve CC
            }
            result.append(block);
        }
        return result.toString();
    }

    /**
     * Removes CC's not followed by numbers.
     * @param in
     * @return fixed
     */
    public static String removeDoubleCc(final String in) {
        final StringBuffer result = new StringBuffer();
        final String[] splitted = in.split(CC);
        String lastInfo = "";
        for (final String block : splitted) {
            if (block.isEmpty()) {
                continue;
            }
            final String blockInfo = returnBlockInfo(block);
            if (blockInfo.equals(lastInfo)) { // remove blockinfo
                result.append(block.substring(blockInfo.length(), block.length()));
            } else { // leave untouched
                result.append(CC);
                result.append(block);
            }
            lastInfo = blockInfo;
        }
        return result.toString();
    }

    /**
     * Returns the CC and the color info of the submitted block.
     * @param in
     * @return info
     */
    public static String returnBlockInfo(final String in) {
        return in.split("[^" + CC + "0-9,]")[0];
    }
}
