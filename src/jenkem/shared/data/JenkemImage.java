package jenkem.shared.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Text;



@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class JenkemImage implements Serializable {
	private static final long serialVersionUID = -6473171638408382577L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long key;

	@Persistent
	private String name;

	@Persistent
	private ArrayList<Text> irc;

	@Persistent
	private Text html;

	@Persistent
	private Text css;

	@Persistent
	private Date createDate;
	
	public JenkemImage() {
	}
	
	
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
//	public byte[] getByteArray() {
//		return byteArray;
//	}
//	public void setByteArray(byte[] byteArray) {
//		if (byteArray != null) {
//			this.byteArray = byteArray;
//		}
//	}
	
	public ArrayList<Text> getIrc() {
		return irc;
	}
	public void setIrc(ArrayList<Text> irc) {
		this.irc = irc;
	}
	public String getHtml() {
		return html.getValue();
	}
	public void setHtml(String html) {
		this.html = new Text(html);
	}
	public String getCss() {
		return css.getValue();
	}
	public void setCss(String css) {
		this.css = new Text(css);
	}
	
	public Long getKey() {
		return key;
	}

	public void setKey(Long key) {
		this.key = key;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getCreateDate() {
		return createDate;
	}

}