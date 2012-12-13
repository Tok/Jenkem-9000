package jenkem.shared.data;

import java.io.Serializable;

/**
 * Wrapper for Jenkem image parts.
 */
@SuppressWarnings("serial")
public class JenkemImage implements Serializable {
    private ImageInfo info;
    private ImageHtml html;
    private ImageCss css;
    private ImageIrc irc;

    public JenkemImage() { }

    public JenkemImage(
            final ImageInfo info,
            final ImageHtml html,
            final ImageCss css,
            final ImageIrc irc) {
        this.info = info;
        this.html = html;
        this.css = css;
        this.irc = irc;
    }

    public final ImageInfo getInfo() {
        return info;
    }

    public final ImageHtml getHtml() {
        return html;
    }

    public final ImageCss getCss() {
        return css;
    }

    public final ImageIrc getIrc() {
        return irc;
    }
}
