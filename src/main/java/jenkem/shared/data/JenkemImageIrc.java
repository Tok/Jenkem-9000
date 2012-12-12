package jenkem.shared.data;

import java.io.Serializable;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

/**
 * Persistent IRC data for converted images.
 */
@SuppressWarnings("serial")
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class JenkemImageIrc implements Serializable {

    @PrimaryKey
    public String name; // mongodb identifier
    private String irc;

    public JenkemImageIrc() {}

    public final void setName(final String name) {
        this.name = name;
    }

    public final String getName() {
        return name;
    }

    public final String getIrc() {
        return irc;
    }

    public final void setIrc(final String irc) {
        this.irc = irc;
    }

    @Override
    public final String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        JenkemImageIrc other = (JenkemImageIrc) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
}
