package jenkem.shared.color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import jenkem.shared.AsciiScheme;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

/**
 * Just some constants and stuff.
 */
public class ColorUtil {
    private final AsciiScheme asciiScheme = new AsciiScheme();
    // conventional color codes according to mIRC
    public static final String BC = String.valueOf('\u0002'); // bold character
                                                              // for IRC
    public static final String CC = String.valueOf('\u0003'); // color character
                                                              // for IRC
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
        for (final IrcColor ircColor : IrcColor.values()) {
            if (col.equals(ircColor.getValue())) {
                // color found. return provided String with black or white
                // background
                if (ircColor.isDark()) {
                    return concatColorConfig(IrcColor.white.getValue(), col, input + "% ");
                } else {
                    return concatColorConfig(IrcColor.black.getValue(), col, input + "% ");
                }
            }
        }
        throw new IllegalArgumentException("Unknown color!");
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
            } else if (block.substring(0, 1).matches("[^0-9]")) {
                result.append(block); // remove CC
            } else {
                result.append(CC);
                result.append(block);
            }
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
            if (blockInfo.equals(lastInfo)) {
                result.append(block.substring(blockInfo.length(), block.length())); // remove
                                                                                    // blockinfo
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

    /**
     * Makes colored ASCII output smooth. Post procession is done as if the row
     * were plain (without CC's) Color Codes are removed, row is processed and
     * the Colors are added again.
     * @param row to process
     * @return the processed line
     */
    public final String postProcessColoredRow(final String row) {
        final String valid = ColorUtil.makeBlocksValid(row); //make valid
        // separate CCs and content:
        final List<String> ccs = new ArrayList<String>(Arrays.asList(valid.split("[^" + CC + "0-9,]")));
        ccs.removeAll(Arrays.asList("", null));
        final List<String> content = new ArrayList<String>(Arrays.asList(valid.split("[" + CC + "0-9,]")));
        content.removeAll(Arrays.asList("", null));
        // keep length of contents and strip CCs
        final List<Integer> lengths = new ArrayList<Integer>();
        final StringBuilder plainRow = new StringBuilder();
        for (final String noCc : content) {
            lengths.add(noCc.length());
            plainRow.append(noCc);
        }
        // process plain
        final String processed = postProcessRow(plainRow.toString());
        // reassemble processed conten and CCs
        final StringBuilder result = new StringBuilder();
        int beginIndex = 0;
        for (int i = 0; i < lengths.size(); i++) {
            result.append(ccs.get(i));
            result.append(processed.substring(beginIndex, beginIndex + lengths.get(i)));
            beginIndex += lengths.get(i);
        }
        return result.toString();
    }

    /**
     * Makes plain ASCII output smooth.
     * @param row to process
     * @return the processed line
     */
    public final String postProcessRow(final String row) {
        // 1st procession for the upper part of the characters (true case)
        // 2nd one for the lower parts (false case)
        return postProcessHor(postProcessHor(row, true), false);
    }
    //TODO implement vertical procession

    /**
     * Makes plain ASCII smooth.
     * @param row to process
     * @param up true if line is " half, false if _ half of ASCII character
     * @return the post-processed line.
     */
    private String postProcessHor(final String row, final boolean up) {
        final String replaceBy = up ? asciiScheme.getUp() : asciiScheme.getDown();
        final String matchMe = replaceBy + replaceBy + "*" + replaceBy;
        final RegExp regex = RegExp.compile(matchMe);
        final MatchResult matcher = regex.exec(row);
        final boolean matchFound = regex.test(row);
        final StringBuffer buf = new StringBuffer();
        if (matchFound) {
            for (int i = 0; i < matcher.getGroupCount(); i++) {
                final String originalStr = matcher.getGroup(i);
                if (originalStr != null) {
                    final StringBuilder line = new StringBuilder();
                    final String[] strings = row.split(originalStr);
                    int index = 0;
                    for (final String part : strings) {
                        line.append(part);
                        index++;
                        if (index < strings.length) {
                            line.append(replaceBy);
                            for (int ii = 0; ii < originalStr.length() - 2; ii++) {
                                // -2 because the first and the last letter is
                                // replaced
                                line.append(asciiScheme.getHline());
                            }
                            line.append(replaceBy);
                        }
                    }
                    buf.append(line);
                }
            }
        } else {
            buf.append(row);
        }
        return buf.toString();
    }
}
