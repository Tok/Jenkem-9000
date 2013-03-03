package jenkem.shared.color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import jenkem.shared.CharacterSet;
import jenkem.shared.ConversionMethod;
import jenkem.shared.ProcessionSettings;
import jenkem.shared.Scheme;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

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

    /**
     * Makes colored ASCII output smooth. Post procession is done as if the row
     * were plain (without CC's) Color Codes are removed, row is processed and
     * the Colors are added again.
     * @param row to process
     * @param preset
     * @param settings
     * @return the processed line
     */
    public final String postProcessColoredRow(final Scheme scheme, final String row, final String charset, final ProcessionSettings settings) {
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
        String processed = postProcessRow(scheme, plainRow.toString(), charset, settings);

        // XXX ugly fix to ensure processed length
        if (processed.length() < plainRow.toString().length()) {
            final int len = plainRow.toString().length();
            final int diff = len - processed.length();
            processed = processed + plainRow.toString().substring(len - diff, len);
        }

        // reassemble processed conten and CCs
        final StringBuilder result = new StringBuilder();
        int beginIndex = 0;
        try {
            for (int i = 0; i < lengths.size(); i++) {
                result.append(ccs.get(i));
                result.append(processed.substring(beginIndex, beginIndex + lengths.get(i)));
                beginIndex += lengths.get(i);
            }
        } catch (StringIndexOutOfBoundsException sioobe) {
            assert true; //ignore
        }
        return result.toString();
    }

    /**
     * Makes plain ASCII output smooth.
     * @param row to process
     * @param preset
     * @param settings
     * @return the processed line
     */
    public final String postProcessRow(final Scheme scheme, final String row, final String charset, final ProcessionSettings settings) {
        final String replaced = postReplacements(scheme, row, charset, settings);
        // 1st procession for the upper part of the characters (true case)
        // 2nd one for the lower parts (false case)
        return settings.isDoHline() ? postProcessHor(scheme, postProcessHor(scheme, replaced, true), false) : replaced;
    }
    //TODO implement vertical procession?

    /**
     * Makes plain ASCII smooth.
     * @param row to process
     * @param up true if line is " half, false if _ half of ASCII character
     * @return the post-processed line.
     */
    private String postProcessHor(final Scheme scheme, final String row, final boolean up) {
        final String replaceBy = up ? scheme.getUp() : scheme.getDown();
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
                                line.append(scheme.getHline());
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

    public final String postReplacements(final Scheme scheme, final String row, final String charset,
            final ProcessionSettings settings) {
        return postReplacements(scheme, postReplacements(scheme, row, charset, settings, 0), charset, settings, 1);
    }

    private String postReplacements(final Scheme scheme, final String row, final String charset,
            final ProcessionSettings settings, final int offset) {
        final int rep = CharacterSet.getRepSensitivity(charset); //replacement sensitivity character count
        final StringBuilder result = new StringBuilder();
        final char[] chars = row.toCharArray();
        if (offset == 1) {
            result.append(String.valueOf(chars[0]));
        }
        for (int i = offset; i < chars.length - 1; i += 2) {
            final String first = String.valueOf(chars[i]);
            final String second = String.valueOf(chars[i+1]);
            if (settings.isDoDiagonal() && first.equals(scheme.getUp())
                    && second.equals(scheme.getDown())) {
                // ""_" --> "\\"
                result.append(scheme.getUpDown(ConversionMethod.Plain)); //XXX?
            } else if (settings.isDoDiagonal() && first.equals(scheme.getDown())
                    && second.equals(scheme.getUp())) {
                // "_"" --> "//"
                result.append(scheme.getDownUp(ConversionMethod.Plain));
            } else if (settings.isDoEdge() && scheme.isCharacterDark(first, charset)
                    && second.equals(scheme.getDown())) {
                // "#_" --> "#L"
                result.append(first);
                result.append(scheme.selectLeftDown());
            } else if (settings.isDoEdge() && first.equals(scheme.getDown())
                    && scheme.isCharacterDark(second, charset)) {
                // "_#" --> "J#"
                result.append(scheme.selectRightDown());
                result.append(second);
            } else if (settings.isDoEdge() && scheme.isCharacterDark(first, charset)
                    && second.equals(scheme.getUp())) {
                // "#"" --> "#F"
                result.append(first);
                result.append(scheme.selectLeftUp());
            } else if (settings.isDoEdge() && first.equals(scheme.getUp())
                    && scheme.isCharacterDark(second, charset)) {
                // ""#" --> "q#"
                result.append(scheme.selectRightUp());
                result.append(second);
            } else if (settings.isDoVline() && scheme.getDarkestCharacters(charset, rep).contains(first)
                    && scheme.getBrightestCharacters(charset, rep).contains(second)) {
                // "# " --> "| "
                result.append(scheme.getVline());
                result.append(second);
            } else if (settings.isDoVline() && scheme.getBrightestCharacters(charset, rep).contains(first)
                    && scheme.getDarkestCharacters(charset, rep).contains(second)) {
                // " #" --> " |"
                result.append(first);
                result.append(scheme.getVline());
            } else {
                result.append(first);
                result.append(second);
            }
        }
        if (result.toString().length() < row.length()) {
            result.append(String.valueOf(chars[chars.length-1]));
        }
        return result.toString();
    }
}
