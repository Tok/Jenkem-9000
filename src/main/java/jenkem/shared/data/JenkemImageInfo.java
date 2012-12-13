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
public class JenkemImageInfo implements Serializable {

    @PrimaryKey
    private String name; // mongodb identifier
    private Integer lines;
    private Integer lineWidth;
    private String creation;

    public JenkemImageInfo() {
    }

    public final void setName(final String name) {
        this.name = name;
    }

    public final String getName() {
        return name;
    }

    public final void setCreation(final String creation) {
        this.creation = creation;
    }

    public final String getCreation() {
        return creation;
    }

    public final Integer getLineWidth() {
        return lineWidth;
    }

    public final void setLineWidth(final Integer lineWidth) {
        this.lineWidth = lineWidth;
    }

    public final Integer getLines() {
        return lines;
    }

    public final void setLines(final Integer lines) {
        this.lines = lines;
    }

    @Override
    public final String toString() {
        return name;
    }

    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (getClass() != obj.getClass()) { return false; }
        JenkemImageInfo other = (JenkemImageInfo) obj;
        if (name == null) {
            if (other.name != null) { return false; }
        } else if (!name.equals(other.name)) { return false; }
        return true;
    }
}
