package jenkem.shared.data;

import java.io.Serializable;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

/**
 * Persistent info data for converted images.
 */
@SuppressWarnings("serial")
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class JenkemImageInfo extends AbstractImagePart implements Serializable {

    @PrimaryKey
    private String name; // mongodb identifier
    private Integer lines;
    private Integer lineWidth;
    private String creation;

    public JenkemImageInfo() { super(null); }

    public JenkemImageInfo(final String name, final int lines,
            final int lineWidth, final String creation) {
        super(name);
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
