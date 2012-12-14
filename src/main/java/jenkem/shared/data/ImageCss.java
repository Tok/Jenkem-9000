package jenkem.shared.data;

import java.io.Serializable;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

/**
 * Persistent CSS data for converted images.
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class ImageCss extends AbstractImagePart implements Serializable {
    private static final long serialVersionUID = 6300119792059722410L;

    @PrimaryKey
    private String _id; //mongodb identifier
    private String name;
    private String css;

    public ImageCss() { super(null); }

    public ImageCss(final String name, final String css) {
        super(name);
        this._id = name;
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
