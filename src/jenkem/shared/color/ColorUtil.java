package jenkem.shared.color;

/**
 * Just some constants and stuff.
 */
public class ColorUtil {
    // conventional color codes according to mIRC
    public static final String BC = String.valueOf('\u0002'); // bold character for IRC
    public static final String CC = String.valueOf('\u0003'); // color character for IRC
    public static final int MAX_RGB = 255;

    /**
     * Colors the entered String black or white, depending on the provided
     * background color. This method is used to output the color config into IRC
     * @param col the color for the background (fg will become white or black)
     * @param input the message to color
     * @return coloredMessage
     */
    public final String colorConfig(final Integer col, final String input) {
        for (final IrcColor ircColor : IrcColor.values()) {
            if (col.equals(ircColor.getValue())) {
                // color found. return provided String with black or white background
                if (ircColor.isDark()) {
                    return concatColorConfig(IrcColor.white.getValue(), col, input + "% ");
                } else {
                    return concatColorConfig(IrcColor.black.getValue(), col, input + "% ");
                }
            }
        }
        assert false;
        return concatColorConfig(IrcColor.black.getValue(), col, input + "% ");
    }

    /**
     * Concatinates the IRC style colors.
     * @param fg
     * @param bg
     * @param input
     * @return concatinatedColors
     */
    private String concatColorConfig(final Integer fg, final Integer bg,
            final String input) {
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
        final Integer ircString = Integer.valueOf(ircColor);
        String css = "#000000"; // assume default black
        for (final IrcColor ircCol : IrcColor.values()) {
            if (ircString.equals(ircCol.getValue())) {
                css = rgbToCss(ircCol.getRgb());
                break;
            }
        }
        return css;
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
        String ret = Integer.toHexString(i);
        if (ret.length() == 1) {
            ret = "0" + ret;
        }
        return ret;
    }
}
