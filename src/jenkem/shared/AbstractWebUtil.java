package jenkem.shared;

/**
 * Abstract utility class for WebUtils.
 */
public abstract class AbstractWebUtil {
    // String sep = System.getProperty("line.separator");
    public static final String SEP = "\n";

    /**
     * Escapes and returns the provided String
     * @param input
     * @return escaped
     */
    final String escape(final String input) {
        String escaped = input.replaceAll("&", "&amp;");
        escaped = escaped.replaceAll("<", "&lt;");
        escaped = escaped.replaceAll(">", "&gt;");
        escaped = escaped.replaceAll(" ", "&nbsp;");
        return escaped;
    }

    /**
     * Appends a new Line to the provided StringBuilder.
     * @param builder
     * @param line
     */
    final void appendLineToBuilder(final StringBuilder builder, final String line) {
        builder.append(line);
        builder.append(SEP);
    }
}
