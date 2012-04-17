package jenkem.shared.data;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * Persistent info data for converted images.
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class JenkemImageInfo implements Serializable {
    private static final long serialVersionUID = 790775137840003002L;

    @PrimaryKey
    private String name;

    @Persistent
    private Integer lines;
    @Persistent
    private Integer lineWidth;

    @Persistent
    private Date createDate;

    public JenkemImageInfo() {
    }

    public final void setName(final String name) {
        this.name = name;
    }

    public final String getName() {
        return name;
    }

    public final void setCreateDate(final Date createDate) {
        this.createDate = createDate;
    }

    public final Date getCreateDate() {
        return createDate;
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

}
