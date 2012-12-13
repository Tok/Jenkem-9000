package jenkem.shared.data;

import java.io.Serializable;

/**
 * Wrapper for Jenkem image parts.
 */
@SuppressWarnings("serial")
public class JenkemImage implements Serializable {
    private JenkemImageInfo info;
    private JenkemImageHtml html;
    private JenkemImageCss css;
    private JenkemImageIrc irc;

    public JenkemImage() { }

    public JenkemImage(
            final JenkemImageInfo info,
            final JenkemImageHtml html,
            final JenkemImageCss css,
            final JenkemImageIrc irc) {
        this.info = info;
        this.html = html;
        this.css = css;
        this.irc = irc;
    }

    public final JenkemImageInfo getInfo() {
        return info;
    }

    public final JenkemImageHtml getHtml() {
        return html;
    }

    public final JenkemImageCss getCss() {
        return css;
    }

    public final JenkemImageIrc getIrc() {
        return irc;
    }
}
