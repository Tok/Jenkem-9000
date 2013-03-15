package jenkem.shared;

/**
 * Represents a style-set of characters for the ASCII output.
 */
public enum CharacterSet {
    //<-- brighter --- darker -->
    HCrude(" #", false),
    SCrude(" @", false),
    Hard(" -+xX#", false),
    Soft(" .:oO@", false),
    Ansi(" ░▒", true), //"▓" makes FG > BG and should not be used
    XAnsi(" ░", true),
    DoubleAnsi(" ░░▒▒▒", true),
    DoubleXAnsi(" ░░", true),
    Mixed("  .-:+oxOX@#", false),
    Chaos("  .'-:;~+=ox*OX&%$@#", false),
    Letters("  ivozaxIVOAHZSXWM", false),
    HalfHard("      -----++++xxxXX#", false),
    DoubleHard(" --+++xxxxXXXXX######", false),
    HalfSoft("      .....::::oooOO@", false),
    DoubleSoft(" ..:::ooooOOOOO@@@@@@", false);
    // Numbers and comma cannot be used!
    // "*" is very dependent on the font and may lead to bad results.
    // for best results, the characters should be more or less symmetric.

    private String characters;
    private boolean isAnsi;

    private static final String ALL_ANSI_CHARS = Ansi.getCharacters();

    private CharacterSet(final String ascii, final boolean isAnsi) {
        this.characters = ascii;
        this.isAnsi = isAnsi;
    }

    public static CharacterSet getValueByName(final String name) {
        for (final CharacterSet set : CharacterSet.values()) {
            if (set.name().equalsIgnoreCase(name)) {
                return set;
            }
        }
        throw new IllegalArgumentException("CharSet must be one of: " + getNames());
    }

    private static String getNames() {
        final StringBuilder result = new StringBuilder();
        for (final CharacterSet cs : values()) {
            result.append("\"");
            result.append(cs);
            result.append("\" ");
        }
        return result.toString();
    }

    public String getCharacters() {
        return characters;
    }

    public boolean hasAnsi() {
        return isAnsi;
    }

    public static int getSensitivity(final String charset) {
        return Double.valueOf((charset.length() + 3) / 2D).intValue();
    }

    public static int getRepSensitivity(final String charset) {
        return Double.valueOf(charset.length() / 4D).intValue();
    }

    public static boolean hasAnsi(final String s) {
        for (final char c : ALL_ANSI_CHARS.toCharArray()) {
            if (s.indexOf(c) > 0) { return true; }
        }
        return false;
    }

    @Override
    public String toString() {
        return name();
    }
}
