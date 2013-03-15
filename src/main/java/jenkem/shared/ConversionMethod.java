package jenkem.shared;

/**
 * Enum for different ASCII-conversion methods.
 */
public enum ConversionMethod {
    Vortacular, Plain, Stencil;

    private ConversionMethod() { }

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
        return name();
    }

    @Override
    public String toString() {
        return name();
    }
}
