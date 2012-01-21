package jenkem.shared;

public enum Kick {
    Off, X, Y, XY;

    private Kick() {
    }

    @Override
    public String toString() {
        return this.name();
    }
}
