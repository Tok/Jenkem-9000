package jenkem.shared;

/**
 * Enum for different color schemes to set on the IRC/ASCII level.
 */
public enum ColorScheme {
    Default(0), Vivid(1), Mono(2), Lsd(3), Bwg(4), Bw(5);

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
