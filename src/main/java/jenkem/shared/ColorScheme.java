package jenkem.shared;

/**
 * Enum for different color schemes to set on the IRC/ASCII level.
 */
public enum ColorScheme {
    Full(0), Default(1), Vivid(2), Mono(3), Lsd(4), Bwg(5), Bw(6);

    private int order;

    private ColorScheme(final int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }

    @Override
    public String toString() {
        return name();
    }
}
