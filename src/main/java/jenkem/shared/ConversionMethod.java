package jenkem.shared;

/**
 * Enum for different ASCII-conversion methods.
 */
public enum ConversionMethod {
    SuperHybrid("Super-Hybrid", true),
    FullHd("Full HD", false),
    Pwntari("Pwntari", true),
    Hybrid("Hybrid", true),
    Plain("Plain", true);

    private String name;
    private boolean hasKick;

    private ConversionMethod(final String name, final boolean hasKick) {
        this.name = name;
        this.hasKick = hasKick;
    }

    public static ConversionMethod getValueByName(final String name) {
        for (final ConversionMethod method : ConversionMethod.values()) {
            if (method.getName().equalsIgnoreCase(name)) {
                return method;
            }
        }
        throw new IllegalArgumentException("Unknown name for ConversionMethod.");
    }

    public String getName() {
        return name;
    }

    public boolean hasKick() {
        return hasKick;
    }

    public int getStep() {
        return hasKick ? 2 : 1;
    }

    @Override
    public String toString() {
        return name;
    }
}
