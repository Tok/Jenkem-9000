package jenkem.shared.data;

import java.io.Serializable;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Text;

/**
 * Persistent CSS data for converted images.
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class JenkemImageCss implements Serializable {
    private static final long serialVersionUID = 4390406347861996970L;

    @PrimaryKey
    private String name;

    @Persistent
    private Text css;

    public JenkemImageCss() {
    }

    public final void setName(final String name) {
        this.name = name;
    }

    public final String getName() {
        return name;
    }

    public final String getCss() {
        return css.getValue();
    }

    public final void setCss(final String css) {
        this.css = new Text(css);
    }

}
