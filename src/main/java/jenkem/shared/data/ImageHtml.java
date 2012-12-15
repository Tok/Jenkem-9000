package jenkem.shared.data;

import java.io.Serializable;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

/**
 * Persistent HTML data for converted images.
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class ImageHtml implements ImagePartIfc, Serializable {
    private static final long serialVersionUID = 7068931434659156682L;

    @PrimaryKey
    private String _id; // mongodb identifier
    private String name;
    private String html;

    public ImageHtml() {
    }

    public ImageHtml(final String name, final String html) {
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

    @Override
    public final String toString() {
        return name;
    }
}
