package jenkem.shared;

public abstract class AbstractWebUtil {
    // String sep = System.getProperty("line.separator");
    public static final String SEP = "\n";

    final String escape(final String in) {
        String out = in.replaceAll("&", "&amp;");
        out = out.replaceAll("<", "&lt;");
        out = out.replaceAll(">", "&gt;");
        out = out.replaceAll(" ", "&nbsp;");
        return out;
    }

    final void appendLineToBuilder(final StringBuilder builder, final String line) {
        builder.append(line);
        builder.append(SEP);
    }
}
