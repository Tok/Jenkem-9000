package jenkem.shared.data;

import java.io.Serializable;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

/**
 * Persistent CSS data for converted images.
 */
@SuppressWarnings("serial")
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class JenkemImageCss extends AbstractImagePart implements Serializable {

    @PrimaryKey
    private String name; //mongodb identifier
    private String css;

    public JenkemImageCss() { super(null); }

    public JenkemImageCss(final String name, final String css) {
        super(name);
        this.name = name;
        this.css = css;
    }

    public final String getName() {
        return name;
    }

    public final String getCss() {
        return css;
    }
}
