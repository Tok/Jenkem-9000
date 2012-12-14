package jenkem.shared.data;

import java.io.Serializable;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

/**
 * Persistent info data for converted images.
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class ImageInfo extends AbstractImagePart implements Serializable {
    private static final long serialVersionUID = -2831420951898466391L;

    @PrimaryKey
    private String _id; // mongodb identifier
    private String name;
    private Integer lines;
    private Integer lineWidth;
    private String creation;

    public ImageInfo() { super(null); }

    public ImageInfo(final String name, final int lines,
            final int lineWidth, final String creation) {
        super(name);
        this._id = name;
        this.name = name;
        this.lines = lines;
        this.lineWidth = lineWidth;
        this.creation = creation;
    }

    public final String getName() {
        return name;
    }

    public final String getCreation() {
        return creation;
    }

    public final Integer getLineWidth() {
        return lineWidth;
    }

    public final Integer getLines() {
        return lines;
    }
}
