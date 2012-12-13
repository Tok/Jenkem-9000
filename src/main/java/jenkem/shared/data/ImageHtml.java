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
public class ImageHtml extends AbstractImagePart implements Serializable {

    @PrimaryKey
    private String _id; // mongodb identifier
    private String name;
    private String html;

    public ImageHtml() { super(null); }

    public ImageHtml(final String name, final String html) {
        super(name);
        this._id = name;
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
