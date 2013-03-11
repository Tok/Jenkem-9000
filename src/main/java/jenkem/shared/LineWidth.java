package jenkem.shared;

/**
 * Enum for different line Widths.
 */
public enum LineWidth {
    GigaHuge(80), MegaHuge(76), Huge(72), Default(68), Large(64), Medium(56), Small(48), Mini(40), Icon(32), Tiny(16);

    private int value;

    private LineWidth(final int value) {
        this.value = value;
    }

    public String getValueString() {
        return String.valueOf(value);
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return name();
    }
}
