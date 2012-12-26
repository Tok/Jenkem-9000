package jenkem.shared;

/**
 * Enum for Powers.
 */
public enum Power {
    Linear(1.00D), Quadratic(2.00D), Cubic(3.00D), Quartic(4.00D);

    private double value;

    private Power(final double value) {
        this.value = value;
    }

    public static Power getValueByName(final String name) {
        for (final Power p : Power.values()) {
            if (p.name().equalsIgnoreCase(name)) {
                return p;
            }
        }
        throw new IllegalArgumentException("Power must be \"Linear\", \"Quadratic\", \"Cubic\" or \"Quartic\".");
    }

    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return name();
    }
}
