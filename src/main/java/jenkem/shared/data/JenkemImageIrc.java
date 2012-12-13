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
public class JenkemImageIrc extends AbstractImagePart implements Serializable {

    @PrimaryKey
    private String name; // mongodb identifier
    private String irc;

    public JenkemImageIrc() { super(null); }

    public JenkemImageIrc(final String name, final String irc) {
        super(name);
        this.name = name;
        this.irc = irc;
    }

    public final String getName() {
        return name;
    }

    public final String getIrc() {
        return irc;
    }
}
