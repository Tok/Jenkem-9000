package jenkem.shared;

/**
 * Enum for different ASCII-conversion methods.
 */
public enum ConversionMethod {
    SuperHybrid("Super-Hybrid", true),
    FullHd("Full-HD", false),
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
        throw new IllegalArgumentException("Method must be one of: " + getNames());
    }

    private static String getNames() {
        final StringBuilder result = new StringBuilder();
        for (final ConversionMethod cm : values()) {
            result.append("\"");
            result.append(cm);
            result.append("\" ");
        }
        return result.toString();
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
