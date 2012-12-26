package jenkem.shared;

/**
 * Enum for different color schemes to set on the IRC/ASCII level.
 */
public enum ColorScheme {
    Full(0), Default(1), Vivid(2), Mono(3), Lsd(4), Bwg(5), Bw(6);
    private int order;

    private ColorScheme(final int order) { this.order = order; }

    public static ColorScheme getValueByName(final String name) {
        for (final ColorScheme s : ColorScheme.values()) {
            if (s.name().equalsIgnoreCase(name)) { return s; }
        }
        throw new IllegalArgumentException("Scheme must be one of: " + getNames());
    }

    private static String getNames() {
        final StringBuilder result = new StringBuilder();
        for (final ColorScheme cs : values()) {
            result.append("\"");
            result.append(cs);
            result.append("\" ");
        }
        return result.toString();
    }

    public int getOrder() { return order; }
    @Override public String toString() { return name(); }
}
