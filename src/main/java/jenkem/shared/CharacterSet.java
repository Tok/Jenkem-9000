package jenkem.shared;

/**
 * Represents a style-set of characters for the ASCII output.
 */
public enum CharacterSet {
    //<-- brighter --- darker -->
    Hard(" -+xX#"),
    Soft(" .:oO@"),
    Ansi(" ░▒"), //"▓" makes FG > BG and should not be used
    XAnsi(" ░"),
    Mixed("  .-:+oxOX@#"),
    Chaos("  .'-:;~+=ox*OX&%$@#"),
    Letters("  ivozaxIVOAHZSXWM"),
    HalfHard("      -----++++xxxXX#"),
    DoubleHard(" --+++xxxxXXXXX######"),
    HalfSoft("      .....::::oooOO@"),
    DoubleSoft(" ..:::ooooOOOOO@@@@@@");
    // Numbers and comma cannot be used!
    // "*" is very dependent on the font and may lead to bad results.
    // for best results, the characters should be more or less symmetric.

    private String characters;

    private CharacterSet(final String ascii) {
        this.characters = ascii;
    }

    public String getCharacters() {
        return characters;
    }

    public static int getSensitivity(final String charset) {
        return Double.valueOf((charset.length() + 3) / 2D).intValue();
    }

    public static int getRepSensitivity(final String charset) {
        return Double.valueOf(charset.length() / 4D).intValue();
    }

    @Override
    public String toString() {
        return characters;
    }
}
