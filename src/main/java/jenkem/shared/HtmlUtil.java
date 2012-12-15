package jenkem.shared;

import java.util.List;
import jenkem.shared.color.ColorUtil;
import com.google.gwt.user.client.Window;

/**
 * Utility class to turn conversions into their HTML representation.
 */
public class HtmlUtil {
    private static final String SEP = "\n";
    private static final String CHARSET = "UTF-8";
    private static final String DOCTYPE = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\""
            + SEP + "    \"http://www.w3.org/TR/html4/strict.dtd\">";
    private static final String META = "<meta http-equiv=\"content-type\" content=\"text/html; charset=" + CHARSET + "\">";
    private final ColorUtil colorUtil = new ColorUtil();

    /**
     * Generates empty HTML.
     * @return html
     */
    public static final String generateEmpty() {
        final StringBuilder html = new StringBuilder();
        appendLineToBuilder(html, DOCTYPE);
        appendLineToBuilder(html, "<html>");
        appendLineToBuilder(html, "<head>");
        appendLineToBuilder(html, META);
        appendLineToBuilder(html, "<title></title>");
        appendLineToBuilder(html, "</head>");
        appendLineToBuilder(html, "<body class=\"jenkemBody\"></body>");
        appendLineToBuilder(html, "</html>");
        return html.toString();
    }

    /**
     * Generates the HTML for the provided IRC-Output.
     * @param ircOutput
     * @param name
     * @param method
     * @return html
     */
    public final String[] generateHtml(final List<String> ircOutput,
            final String name, final ConversionMethod method) {
        final StringBuilder html = new StringBuilder();
        final StringBuilder css = new StringBuilder();

        appendLineToBuilder(html, DOCTYPE);
        appendLineToBuilder(html, "<html>");
        appendLineToBuilder(html, "<head>");
        appendLineToBuilder(html, META);
        html.append("<title>");
        html.append(name);
        appendLineToBuilder(html, "</title>");
        html.append("<link href=\"" + getCssUrl(name) + "\" rel=\"stylesheet\" type=\"text/css\">");
        html.append(SEP);

        appendLineToBuilder(html, "</head>");
        appendLineToBuilder(html, "<body class=\"jenkemBody\">");
        appendLineToBuilder(html, "<div>");

        appendLineToBuilder(css, "form { margin: 0 auto; padding: 0; }");
        appendLineToBuilder(css, "html { background-color: #ffffff }");
        appendLineToBuilder(css, "body { font-family: monospace; font-size: 1em; font-weight: bold; margin: 0; background-color: black; }");
        appendLineToBuilder(css, "div { float: left; width: auto; clear: both; }");
        appendLineToBuilder(css, "span { float: left; width: auto; }");

        int line = 0;

        if (method.equals(ConversionMethod.Plain)) {
            while (ircOutput != null && line < ircOutput.size()
                    && ircOutput.get(line) != null && ircOutput.get(line).length() > 0) {
                html.append("<div class=\"jenkem\">");
                html.append("<span id=\"id_");
                html.append(line);
                html.append("\">");
                html.append(escape(ircOutput.get(line)));
                html.append("</span>");
                html.append("</div>");
                html.append(SEP);
                css.append("#id_");
                css.append(line);
                css.append(" { color: #000000; background-color: #ffffff; }");
                css.append(SEP);
                line++;
            }
        } else {
            while (ircOutput != null && line < ircOutput.size()
                    && ircOutput.get(line) != null && ircOutput.get(line).length() > 0) {
                html.append("<div class=\"jenkem\">");
                final String[] splitSections = ircOutput.get(line).split(ColorUtil.CC);
                final String[] sections = new String[splitSections.length];
                int i = 0;
                for (final String s : splitSections) {
                    if (!s.equals("")) {
                        sections[i] = s;
                        i++;
                    }
                }
                int section = 0;
                for (final String token : sections) {
                    if (token == null || token.equals("") || token.equals(ColorUtil.BC)) { break; }
                    final String[] splitCut = token.split(",");
                    final String[] cut = new String[splitCut.length];
                    int ii = 0;
                    for (final String s : splitCut) {
                        if (!s.equals("")) {
                            cut[ii] = s;
                            ii++;
                        }
                    }

                    final String bgAndChars = cut[1]; //1X, 1XX, 12X or 12XX
                    final String bg = bgAndChars.replaceAll("[^0-9]", ""); //remove nonnumeric
                    final String chars = bgAndChars.replaceAll("[0-9]", ""); //remove numeric

                    html.append("<span id=\"id_");
                    html.append(line);
                    html.append("_");
                    html.append(section);
                    html.append("\">");
                    html.append(escape(chars));
                    html.append("</span>");
                    css.append("#id_");
                    css.append(line);
                    css.append("_");
                    css.append(section);
                    css.append(" { color: ");

                    css.append(colorUtil.ircToCss(cut[0])); //fg
                    css.append("; background-color: ");
                    css.append(colorUtil.ircToCss(bg));
                    css.append("; }");
                    css.append(SEP);
                    section++;
                }
                html.append("</div>");
                html.append(SEP);
                line++;
            }
        }
        appendLineToBuilder(html, "</div>");

        // puts link with output for IRC
        html.append("<div class=\"ircBinary\">");
        html.append("<a href=\"");
        html.append(getIrcUrl(name));
        html.append("\" onclick=\"this.target='blank'\">Download binary textfile for IRC</a>");
        appendLineToBuilder(html, "</div>");

        html.append("<div class=\"validator\">");
        html.append("<a href=\"http://validator.w3.org/check?uri=" + getHtmlUrl(name) + "\">");
        html.append("<img src=\"/images/valid-html401.png\" alt=\"Valid HTML 4.01 Strict\" style=\"border: 0; width: 88px; height: 31px\">");
        html.append("</a>");
        if (!method.equals(ConversionMethod.Plain)) {
            html.append("<a href=\"http://jigsaw.w3.org/css-validator/validator?uri=" + getCssUrl(name) + "&amp;profile=css3\">");
            html.append("<img src=\"/images/vcss.gif\" alt=\"CSS is valid!\" style=\"border: 0; width: 88px; height: 31px\">");
            html.append("</a>");
        }
        appendLineToBuilder(html, "</div>");

        appendLineToBuilder(html, "</body>");
        html.append("</html>");

        final String[] ret = new String[2];
        ret[0] = html.toString();
        ret[1] = css.toString();
        return ret;
    }

    /**
     * Prepares the CSS to inline.
     * @param inputCss
     * @return css
     */
    public final String prepareCssForInline(final String inputCss) {
        final String[] cssLines = inputCss.split("\n");
        final StringBuffer newInlineCss = new StringBuffer();
        newInlineCss.append("<style type=\"text/css\">\n");
        for (final String line : cssLines) {
            if (line.startsWith("div {")) {
                newInlineCss.append(".jenkem { font-family: monospace; font-weight: bold; }");
            } else if (!line.startsWith("form {")
                    && !line.startsWith("body {")
                    && !line.startsWith("html {")) {
                newInlineCss.append(line);
            }
        }
        newInlineCss.append("\n</style>");
        return newInlineCss.toString();
    }

    /**
     * Prepares the HTML for inline.
     * @param inputHtml
     * @param inputCss
     * @return html
     */
    public final String prepareHtmlForInline(final String inputHtml, final String inputCss) {
        final String[] htmlLines = inputHtml.split("\n");
        final StringBuffer newInlineHtml = new StringBuffer();
        for (final String line : htmlLines) {
            if (line.startsWith("<link href=")) {
                newInlineHtml.append(inputCss);
            } else if (!line.startsWith("<div class=\"ircBinary\">")
                    && !line.startsWith("<div class=\"validator\">")) {
                newInlineHtml.append(line);
            }
        }
        return newInlineHtml.toString();
    }

    /**
     * Returns the url for the html associated with the provided name.
     * @param name
     * @return url
     */
    public static String getHtmlUrl(final String name) {
        return obtainProtocolAndHost() + "/jenkem/output?name=" + name + ".html";
    }

    /**
     * Returns the url for the css associated with the provided name.
     * @param name
     * @return url
     */
    public static String getCssUrl(final String name) {
        return obtainProtocolAndHost() + "/jenkem/cssOutput?name=" + name + ".css";
    }

    /**
     * Returns the url for the irc text associated with the provided name.
     * @param name
     * @return url
     */
    public static String getIrcUrl(final String name) {
        return obtainProtocolAndHost() + "/jenkem/irc?name=" + name + ".txt";
    }

    /**
     * Returns protocol and host. insecure for local os ssl for production mode.
     * @return protocol string
     */
    private static String obtainProtocolAndHost() {
        final String protocol = isLocal() ? "http://" : "https://";
        return protocol + Window.Location.getHost();
    }

    /**
     * Decides if app runs on localhost.
     * @return isLocal
     */
    private static boolean isLocal() {
        return Window.Location.getHost().equals("127.0.0.1:8080");
    }

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
    static final void appendLineToBuilder(final StringBuilder builder, final String line) {
        builder.append(line);
        builder.append(SEP);
    }
}
