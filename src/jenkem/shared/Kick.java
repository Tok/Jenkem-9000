package jenkem.shared;

/**
 * Enum to represent the possible kicks for the ASCII conversion.
 */
public enum Kick {
    Off, X, Y, XY;

    private Kick() {
    }

    @Override
    public String toString() {
        return this.name();
    }
}
