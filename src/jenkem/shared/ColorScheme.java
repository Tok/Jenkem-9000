package jenkem.shared;

/**
 * Enum for different color schemes to set on the IRC/ASCII level.
 */
public enum ColorScheme {
    Default(0), Old(1), Vivid(2), Mono(3), Lsd(4), Skin(5), Bwg(6), Bw(7);

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
