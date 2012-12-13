package jenkem.shared;

import jenkem.shared.color.ColorUtil;

import com.google.gwt.user.client.Window;

/**
 * Utility class to turn conversions into their HTML representation.
 */
public class HtmlUtil extends AbstractWebUtil {
    private final String charset = "UTF-8";
    private final String doctype = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\""
            + SEP + "    \"http://www.w3.org/TR/html4/strict.dtd\">";
    private final String meta = "<meta http-equiv=\"content-type\" content=\"text/html; charset=" + charset + "\">";
    private final ColorUtil colorUtil = new ColorUtil();

    /**
     * Generates empty HTML.
     * @return html
     */
    public final String generateEmpty() {
        final StringBuilder html = new StringBuilder();
        appendLineToBuilder(html, doctype);
        appendLineToBuilder(html, "<html>");
        appendLineToBuilder(html, "<head>");
        appendLineToBuilder(html, meta);
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
     * @param isPlain
     * @return html
     */
    public final String[] generateHtml(final String[] ircOutput,
            final String name, final boolean isPlain) {
        final StringBuilder html = new StringBuilder();
        final StringBuilder css = new StringBuilder();

        appendLineToBuilder(html, doctype);
        appendLineToBuilder(html, "<html>");
        appendLineToBuilder(html, "<head>");
        appendLineToBuilder(html, meta);
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

        if (isPlain) {
            while (ircOutput != null && line < ircOutput.length
                    && ircOutput[line] != null && ircOutput[line].length() > 0) {
                html.append("<div class=\"jenkem\">");
                html.append("<span id=\"id_");
                html.append(line);
                html.append("\">");
                html.append(escape(ircOutput[line]));
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
            while (ircOutput != null && line < ircOutput.length
                    && ircOutput[line] != null && ircOutput[line].length() > 0) {
                html.append("<div class=\"jenkem\">");
                final String[] splitSections = ircOutput[line].split(ColorUtil.CC);
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
                    if (token == null || token.equals("")) {
                        break;
                    }
                    if (token.equals(ColorUtil.BC)) {
                        break; // throw bold code away
                    }
                    final String[] splitCut = token.split(",");
                    final String[] cut = new String[splitCut.length];
                    int ii = 0;
                    for (final String s : splitCut) {
                        if (!s.equals("")) {
                            cut[ii] = s;
                            ii++;
                        }
                    }
                    final String fg = cut[0];
                    final String bgAndChars = cut[1];
                    String bg = "";
                    String chars = "";

                    try { // FIXME ugly trial and error approach
                        Integer.parseInt(bgAndChars.substring(0, 2));
                        // if parseInt works we know that the bg color takes two
                        // characters
                        bg = bgAndChars.substring(0, 2);
                        chars = bgAndChars.substring(2, bgAndChars.length());
                    } catch (final Exception e) {
                        bg = bgAndChars.substring(0, 1);
                        chars = bgAndChars.substring(1, bgAndChars.length());
                    }

                    html.append("<span id=\"id_");
                    html.append(line);
                    html.append("_");
                    html.append(section);
                    html.append("\">");

                    chars = escape(chars);

                    html.append(chars);
                    html.append("</span>");
                    css.append("#id_");
                    css.append(line);
                    css.append("_");
                    css.append(section);
                    css.append(" { color: ");

                    css.append(colorUtil.ircToCss(fg));
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
        if (!isPlain) {
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
                newInlineCss
                        .append(".jenkem { font-family: monospace; font-weight: bold; }");
            } else if (line.startsWith("form {")) {
                assert true; // ignore
            } else if (line.startsWith("body {")) {
                assert true; // ignore
            } else if (line.startsWith("html {")) {
                assert true; // ignore
            } else {
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
    public final String prepareHtmlForInline(final String inputHtml,
            final String inputCss) {
        final String[] htmlLines = inputHtml.split("\n");
        final StringBuffer newInlineHtml = new StringBuffer();
        for (final String line : htmlLines) {
            if (line.startsWith("<link href=")) {
                newInlineHtml.append(inputCss.toString());
            } else if (line.startsWith("<div class=\"ircBinary\">")) {
                assert true; // ignore
            } else if (line.startsWith("<div class=\"validator\">")) {
                assert true; // ignore
            } else {
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
        return Window.Location.getHost() == "127.0.0.1:8080";
    }
}
