package jenkem.shared;

/**
 * Enum for different line Widths.
 */
public enum LineWidth {
    Default(72), Huge(68), Large(64), Medium(56), Small(48), Mini(40), Icon(32), Tiny(16);

    private int value;

    private LineWidth(final int value) {
        this.value = value;
    }

    public String getValueString() {
        return String.valueOf(value);
    }

    @Override
    public String toString() {
        return name();
    }
}
