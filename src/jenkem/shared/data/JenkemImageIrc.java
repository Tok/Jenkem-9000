package jenkem.shared.data;

import java.io.Serializable;
import java.util.ArrayList;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Order;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Text;

/**
 * Persistent IRC data for converted images.
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class JenkemImageIrc implements Serializable {
    private static final long serialVersionUID = -8360097117647923335L;

    @PrimaryKey
    private String name;

    @Persistent(serialized = "true")
    @Order(column = "JENKEMIMAGEIRC_IRC")
    private ArrayList<Text> irc;

    public JenkemImageIrc() {
    }

    public final void setName(final String name) {
        this.name = name;
    }

    public final String getName() {
        return name;
    }

    public final ArrayList<Text> getIrc() {
        return irc;
    }

    public final void setIrc(final ArrayList<Text> irc) {
        this.irc = irc;
    }

}
