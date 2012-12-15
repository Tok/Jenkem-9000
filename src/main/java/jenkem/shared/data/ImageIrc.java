package jenkem.shared.data;

import java.io.Serializable;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

/**
 * Persistent IRC data for converted images.
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class ImageIrc implements ImagePartIfc, Serializable {
    private static final long serialVersionUID = -4605887704668744633L;

    @PrimaryKey
    private String _id; // mongodb identifier
    private String name;
    private String irc;

    public ImageIrc() { }

    public ImageIrc(final String name, final String irc) {
        this._id = name;
        this.name = name;
        this.irc = irc;
    }

    public final String getName() {
        return name;
    }

    public final String getIrc() {
        return irc;
    }

    @Override
    public final String toString() {
        return name;
    }
}
