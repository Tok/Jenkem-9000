package jenkem.shared;

import java.util.Random;

/**
 * Settings for the ASCII conversion.
 */
public class AsciiScheme {
    private static final double BARRIER = 0.3d;
    private static final int MAX_RGB = 255;

    private final Random random = new Random();

    private boolean postProcessed = true;
    private boolean processed = true;
    private boolean randomized = true;

    private String up = "\"";
    private String down = "_";
    private String hline = "-";
    private String vline = "|";
    private String upDown = "\\\\";
    private String downUp = "//";
    private String leftDown = "L";
    private String rightDown = "J";
    private String leftUp = "F";
    private String rightUp = "q";
    private String left = "[";
    private String right = "]";

    /**
     * Returns a String with the character according to the provided parameters.
     * @param strength
     * @param preset
     * @param isAbsolute
     * @return characterString
     */
    public final String getChar(final double strength,
            final CharacterSet preset, final boolean isAbsolute) {
        if (isAbsolute) {
            return getChar((MAX_RGB - strength) / MAX_RGB, preset);
        } else {
            return getChar(strength, preset);
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
    private String getChar(final double relStrength, final CharacterSet preset) {
        double strength = relStrength;
        if (relStrength > 1) {
            strength = 1.0D;
        }
        if (relStrength < 0) {
            strength = 0.0D;
        }
        final double th = 1.0 / preset.getCharacters().length();
        String ret = "";
        for (int i = 0; i < preset.getCharacters().length(); i++) {
            if (strength <= (i + 1) * th) {
                ret = Character.toString(preset.getCharacters().toCharArray()[i]);
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
            return String.valueOf(c[new Random().nextInt(c.length)]);
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
    public final boolean isCharacterDark(final String character,
            final CharacterSet preset) {
        final int halfLength = (preset.getCharacters().length() + 3) / 2;
        // cutting off the decimals in OK here
        for (int i = 0; i <= halfLength; i++) {
            final String compare = preset.getCharacters().substring(
                    preset.getCharacters().length() - i - 1);
            if (unFormat(character).equals(compare)) {
                return true;
            }
        }
        return false;
    }

    /**
     * TRue if the provided character is bright.
     * @param character
     * @param preset
     * @return isCharacterBright
     */
    public final boolean isCharacterBright(final String character,
            final CharacterSet preset) {
        final int halfLength = (preset.getCharacters().length() + 3) / 2;
        // cutting off the decimals in OK here
        for (int i = 0; i <= halfLength; i++) {
            // String compare = preset.getCharacters().substring(i);
            final String compare = preset.getCharacters().substring(i, i + 1);
            if (unFormat(character).equals(compare)) {
                return true;
            }
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
        return up;
    }

    public final String selectUp() {
        return randomize(up);
    }

    public final void setUp(final String up) {
        this.up = up;
    }

    public final String getDown() {
        return down;
    }

    public final String selectDown() {
        return randomize(down);
    }

    public final void setDown(final String down) {
        this.down = down;
    }

    public final String getHline() {
        return hline;
    }

    public final String selectHline() {
        return randomize(hline);
    }

    public final void setHline(final String hline) {
        this.hline = hline;
    }

    public final String getVline() {
        return vline;
    }

    public final String selectVline() {
        return randomize(vline);
    }

    public final void setVline(final String vline) {
        this.vline = vline;
    }

    public final String getUpDown() {
        return upDown;
    }

    public final String selectUpDown() {
        return randomizeSix(upDown);
    }

    public final void setUpDown(final String upDown) {
        this.upDown = upDown;
    }

    public final String getDownUp() {
        return downUp;
    }

    public final String selectDownUp() {
        return randomizeSix(downUp);
    }

    public final void setDownUp(final String downUp) {
        this.downUp = downUp;
    }

    public final String getLeftDown() {
        return leftDown;
    }

    public final String selectLeftDown() {
        return randomize(leftDown);
    }

    public final void setLeftDown(final String leftDown) {
        this.leftDown = leftDown;
    }

    public final String getRightDown() {
        return rightDown;
    }

    public final String selectRightDown() {
        return randomize(rightDown);
    }

    public final void setRightDown(final String rightDown) {
        this.rightDown = rightDown;
    }

    public final String getLeftUp() {
        return leftUp;
    }

    public final String selectLeftUp() {
        return randomize(leftUp);
    }

    public final void setLeftUp(final String leftUp) {
        this.leftUp = leftUp;
    }

    public final void setRightUp(final String rightUp) {
        this.rightUp = rightUp;
    }

    public final String getRightUp() {
        return rightUp;
    }

    public final String selectRightUp() {
        return randomize(rightUp);
    }

    public final String getLeft() {
        return left;
    }

    public final String selectLeft() {
        return randomize(left);
    }

    public final void setLeft(final String left) {
        this.left = left;
    }

    public final String getRight() {
        return right;
    }

    public final String selectRight() {
        return randomize(right);
    }

    public final void setRight(final String right) {
        this.right = right;
    }

    /**
     * Returns the darkest character according to the provided preset.
     * @param preset
     * @return darkestCharacter
     */
    public final String getDarkestCharacter(final CharacterSet preset) {
        return preset.getCharacters().substring(
                preset.getCharacters().length() - 1,
                preset.getCharacters().length());
    }

}
