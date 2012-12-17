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

    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return name();
    }
}
