package jenkem.shared.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Wrapper for Jenkem image parts.
 */
@SuppressWarnings("serial")
public class JenkemImage implements Serializable {
    public enum Part {
        INFO(ImageInfo.class), HTML(ImageHtml.class), CSS(ImageCss.class),  IRC(ImageIrc.class);
        private Class<? extends AbstractImagePart> c;
        private <T extends AbstractImagePart> Part(final Class<T> c) { this.c = c; }
        public final Class<? extends AbstractImagePart> obtainClass() { return c; }
    }

    // TODO use diamond syntax when gwt supports it
    private Map<Part, AbstractImagePart> components = new HashMap<Part, AbstractImagePart>();

    public JenkemImage() { }
    public JenkemImage(final ImageInfo info, final ImageHtml html,
            final ImageCss css, final ImageIrc irc) {
        this.components.put(Part.INFO, info);
        this.components.put(Part.HTML, html);
        this.components.put(Part.CSS, css);
        this.components.put(Part.IRC, irc);
    }

    public final Map<Part, AbstractImagePart> getComponents() {
        return components;
    }

    public final ImageInfo getInfo() {
        return (ImageInfo) components.get(Part.INFO);
    }

    public final ImageHtml getHtml() {
        return (ImageHtml) components.get(Part.HTML);
    }

    public final ImageCss getCss() {
        return (ImageCss) components.get(Part.CSS);
    }

    public final ImageIrc getIrc() {
        return (ImageIrc) components.get(Part.IRC);
    }
}
