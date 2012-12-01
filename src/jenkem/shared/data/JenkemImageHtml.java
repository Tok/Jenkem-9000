package jenkem.shared.data;

import java.io.Serializable;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Text;

/**
 * Persistent HTML data for converted images.
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class JenkemImageHtml implements Serializable {
    private static final long serialVersionUID = 4833906739614704L;

    @PrimaryKey
    private String name;

    @Persistent
    private Text html;

    public JenkemImageHtml() {
    }

    public final void setName(final String name) {
        this.name = name;
    }

    public final String getName() {
        return name;
    }

    public final String getHtml() {
        return html.getValue();
    }

    public final void setHtml(final String html) {
        this.html = new Text(html);
    }

}
