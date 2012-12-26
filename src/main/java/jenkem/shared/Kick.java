package jenkem.shared;

/**
 * Enum to represent the possible kicks for the conversion.
 * Depending on the Kick, Methods that convert two rows or columns at a time
 * will start at position 0 or 1 in row (Y) or column (X).
 *   X
 * Y +------------>
 *   | ## ## ##
 *   | ## ## ##
 *   | ## ## ##
 *   | ## ## ##
 *   v
 */
public enum Kick {
    Off, X, Y, XY;
    private Kick() { }
    public static Kick getValueByName(final String name) {
        if (name.equals("0")) { return Off; }
        for (final Kick k : Kick.values()) {
            if (k.name().equalsIgnoreCase(name)) {
                return k;
            }
        }
        throw new IllegalArgumentException("Kick name must be one of: \"0\", \"X\", \"Y\" or \"XY\".");
    }
    public boolean hasX() { return this.equals(X) || this.equals(XY); }
    public boolean hasY() { return this.equals(Y) || this.equals(XY); }
    public int getXOffset() { return hasX() ? 1 : 0; }
    public int getYOffset() { return hasY() ? 1 : 0; }
    @Override public String toString() { return this.name(); }
}
