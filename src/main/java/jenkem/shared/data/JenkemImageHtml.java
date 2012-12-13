package jenkem.shared.data;

import java.io.Serializable;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

/**
 * Persistent HTML data for converted images.
 */
@SuppressWarnings("serial")
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class JenkemImageHtml extends AbstractImagePart implements Serializable {

    @PrimaryKey
    private String name; // mongodb identifier
    private String html;

    public JenkemImageHtml() { super(null); }

    public JenkemImageHtml(final String name, final String html) {
        super(name);
        this.name = name;
        this.html = html;
    }

    public final String getName() {
        return name;
    }

    public final String getHtml() {
        return html;
    }
}
