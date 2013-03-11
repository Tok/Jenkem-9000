package jenkem.shared.data;

import java.io.Serializable;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

/**
 * Persistent info data for converted images.
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class ImageInfo implements ImagePartIfc, Serializable {
    private static final long serialVersionUID = -2831420951898466391L;

    @PrimaryKey
    private String _id; // mongodb identifier
    private String name;
    private String icon; // base64 encoded BufferedImage byte[]
    private String method;
    private String characters;
    private Integer contrast;
    private Integer brightness;
    private Integer lines;
    private Integer lineWidth;
    private String creation;

    public ImageInfo() { }

    public ImageInfo(final String name, final String icon, final String method,
            final String characters, final int[] ints, final String creation) {
        this._id = name;
        this.name = name;
        this.icon = icon;
        this.method = method;
        this.characters = characters;
        this.contrast = ints[0];
        this.brightness = ints[1];
        this.lines = ints[2];
        this.lineWidth = ints[3];
        this.creation = creation;
    }

    public final String getName() { return name; }
    public final String getIcon() { return icon; }
    public final String getMethod() { return method; }
    public final String getCharacters() { return characters; }
    public final Integer getContrast() { return contrast; }
    public final Integer getBrightness() { return brightness; }
    public final Integer getLineWidth() { return lineWidth; }
    public final Integer getLines() { return lines; }
    public final String getCreation() { return creation; }

    @Override public final String toString() { return name; }
}
