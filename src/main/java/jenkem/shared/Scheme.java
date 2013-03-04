package jenkem.shared;

import java.util.Random;

/**
 * Settings for the ASCII conversion.
 */
public class Scheme {
    public enum StrengthType { RELATIVE, ABSOLUTE }
    public enum Type { ASCII, ANSI }
    private static final double BARRIER = 0.3d;
    private static final int MAX_RGB = 255;

    private final Random random = new Random();
    private final Type type;

    private boolean postProcessed = true;
    private boolean processed = true;
    private boolean randomized = true;

    private String up = "\"";
    private String ansiUp = "▀";
    private String down = "_";
    private String ansiDown = "▄";
    private String hline = "-";
    private String ansiHline = "▬";
    private String vline = "|";
    private String ansiVline = "│";
    private String upDown = "\\"; //has length() == 1
    private String downUp = "/";
    private String leftDown = "b"; //L
    private String rightDown = "d"; //J
    private String leftUp = "p"; //F
    private String rightUp = "q";
    private String ansiLeftDown = "╗";
    private String ansiRightDown = "╔";
    private String ansiLeftUp = "╝";
    private String ansiRightUp = "╚";
    private String left = "[";
    private String right = "]";
    private String ansiLeft = "▌";
    private String ansiRight = "▐";

    public Scheme(final Type type) {
        this.type = type;
    }

    /**
     * Returns a String with the character according to the provided parameters.
     * @param strength
     * @param preset
     * @param type
     * @return characterString
     */
    public final String getChar(final double strength, final String charset, final StrengthType type) {
        if (type.equals(StrengthType.ABSOLUTE)) {
            return getChar((MAX_RGB - strength) / MAX_RGB, charset);
        } else {
            return getChar(strength, charset);
        }
    }

    /**
     * Selects a character from the provided ASCII palette and selects the
     * character to use depending on the entered strength value. ascii must be a
     * String like ' .:xX#', from bright to dark.
     * @param relStrength a relative value between 0.0 and 1.0
     * @param preset the character set to use
     * @return String with the character to use. (assuming bg > fg)
     */
    private String getChar(final double relStrength, final String charset) {
        double strength = relStrength;
        if (relStrength > 1) {
            strength = 1.0D;
        }
        if (relStrength < 0) {
            strength = 0.0D;
        }
        final double th = 1.0 / charset.length();
        String ret = "";
        for (int i = 0; i < charset.length(); i++) {
            if (strength <= (i + 1) * th) {
                ret = Character.toString(charset.toCharArray()[i]);
                break; // we have a winrar
            }
        }
        if (ret.length() == 0) {
            throw new IllegalArgumentException("Cannot find character.");
        }
        return ret;
    }

    /**
     * Randomizes an input String.
     * @param input
     * @return
     */
    private String randomize(final String input) {
        if (randomized) {
            final char[] c = input.toCharArray();
            return String.valueOf(c[random.nextInt(c.length)]);
        } else {
            return input.substring(0, 1);
        }
    }

    /**
     * Randomizes an input String with six options.
     * @param input
     * @return the randomized String
     */
    private String randomizeSix(final String input) {
        if (input.length() < 2) {
            return input + " ";
        }
        if (randomized) {
            try {
                if (random.nextDouble() <= BARRIER) {
                    return input.substring(0, 2);
                } else if (random.nextDouble() <= BARRIER) {
                    return input.substring(2, 4);
                } else {
                    return input.substring(4, 6);
                }
            } catch (final IndexOutOfBoundsException ioobe) {
                // just ignore the error and return 2
                return input.substring(0, 2);
            }
        } else {
            return input.substring(0, 2);
        }
    }

    /**
     * True if the provided character is dark.
     * @param character
     * @param preset
     * @return isCharacterDark
     */
    public final boolean isCharacterDark(final String character, final String charset,
            final boolean makeEdgy) {
        if (makeEdgy) { return true; }
        try {
            final int halfLength = (charset.length() + 3) / 2;
            // cutting off the decimals is OK here
            for (int i = 0; i <= halfLength; i++) {
                final String compare = charset.substring(charset.length() - i - 1);
                if (unFormat(character).equals(compare)) {
                    return true;
                }
            }
        } catch (final StringIndexOutOfBoundsException sioobe) {
            return makeEdgy;
        }
        return false;
    }

    /**
     * TRue if the provided character is bright.
     * @param character
     * @param preset
     * @return isCharacterBright
     */
    public final boolean isCharacterBright(final String character, final String charset,
            final boolean makeEdgy) {
        if (makeEdgy) { return false; }
        try {
            // cutting off the decimals is OK here
            for (int i = 0; i <= CharacterSet.getSensitivity(charset); i++) {
                final String compare = charset.substring(i, i + 1);
                if (unFormat(character).equals(compare)) {
                    return true;
                }
            }
        } catch (final StringIndexOutOfBoundsException sioobe) {
            return !makeEdgy;
        }
        return false;
    }

    /**
     * Unformats and returns the provided String if required.
     * @param input
     * @return unformattedString
     */
    private String unFormat(final String input) {
        if (input.length() > 1) {
            return input.substring(input.length() - 1, input.length());
        } else {
            return input;
        }
    }

    /**
     * Utility method to replace the last character in a String.
     * @param input String
     * @param replacement
     * @return modified String
     */
    public final String replace(final String input, final String replacement) {
        return input.substring(0, input.length() - 1) + replacement;
    }

    public final void setPostProcessed(final boolean postProcessed) {
        this.postProcessed = postProcessed;
    }

    public final boolean isPostProcessed() {
        return postProcessed;
    }

    public final void setProcessed(final boolean processed) {
        this.processed = processed;
    }

    public final boolean isProcessed() {
        return processed;
    }

    public final void setRandomized(final boolean randomized) {
        this.randomized = randomized;
    }

    public final boolean isRandomized() {
        return randomized;
    }

    public final String getUp() {
        return type.equals(Type.ASCII) ? up : ansiUp;
    }

    public final String selectUp() {
        return randomize(getUp());
    }

    public final void setUp(final String up) {
        this.up = up;
    }

    public final String getDown() {
        return type.equals(Type.ASCII) ? down : ansiDown;
    }

    public final String selectDown() {
        return randomize(getDown());
    }

    public final void setDown(final String down) {
        this.down = down;
    }

    public final String getHline() {
        return type.equals(Type.ASCII) ? hline : ansiHline;
    }

    public final String selectHline() {
        return randomize(getHline());
    }

    public final void setHline(final String hline) {
        this.hline = hline;
    }

    public final String getVline() {
        return type.equals(Type.ASCII) ? vline : ansiVline;
    }

    public final String selectVline() {
        return randomize(getVline());
    }

    public final void setVline(final String vline) {
        this.vline = vline;
    }

    public final String getUpDown(final ConversionMethod method) {
        if (method.equals(ConversionMethod.Plain)) {
            return upDown + upDown;
        } else {
            return upDown;
        }
    }

    public final String selectUpDown() {
        return randomizeSix(upDown);
    }

    public final void setUpDown(final String upDown) {
        this.upDown = upDown;
    }

    public final String getDownUp(final ConversionMethod method) {
        if (method.equals(ConversionMethod.Plain)) {
            return downUp + downUp;
        } else {
            return downUp;
        }
    }

    public final String selectDownUp() {
        return randomizeSix(downUp);
    }

    public final void setDownUp(final String downUp) {
        this.downUp = downUp;
    }

    public final String getLeftDown() {
        return type.equals(Type.ASCII) ? leftDown : ansiLeftDown;
    }

    public final String selectLeftDown() {
        return randomize(getLeftDown());
    }

    public final void setLeftDown(final String leftDown) {
        this.leftDown = leftDown;
    }

    public final String getRightDown() {
        return type.equals(Type.ASCII) ? rightDown : ansiRightDown;
    }

    public final String selectRightDown() {
        return randomize(getRightDown());
    }

    public final void setRightDown(final String rightDown) {
        this.rightDown = rightDown;
    }

    public final String getLeftUp() {
        return type.equals(Type.ASCII) ? leftUp : ansiLeftUp;
    }

    public final String selectLeftUp() {
        return randomize(getLeftUp());
    }

    public final void setLeftUp(final String leftUp) {
        this.leftUp = leftUp;
    }

    public final void setRightUp(final String rightUp) {
        this.rightUp = rightUp;
    }

    public final String getRightUp() {
        return type.equals(Type.ASCII) ? rightUp : ansiRightUp;
    }

    public final String selectRightUp() {
        return randomize(getRightUp());
    }

    public final String getLeft() {
        return type.equals(Type.ASCII) ? left : ansiLeft;
    }

    public final String selectLeft() {
        return randomize(getLeft());
    }

    public final void setLeft(final String left) {
        this.left = left;
    }

    public final String getRight() {
        return type.equals(Type.ASCII) ? right : ansiRight;
    }

    public final String selectRight() {
        return randomize(getRight());
    }

    public final void setRight(final String right) {
        this.right = right;
    }

    /**
     * Returns the darkest character according to the provided preset.
     * @param preset
     * @return darkestCharacter
     */
    public final String getDarkestCharacters(final String charset, final int count) {
        return charset.substring(charset.length() - count, charset.length());
    }

    public final String getBrightestCharacters(final String charset, final int count) {
        return charset.substring(0, count);
    }
}
