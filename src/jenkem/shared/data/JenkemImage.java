package jenkem.shared.data;

import java.io.Serializable;
import java.util.ArrayList;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Order;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Text;



@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class JenkemImage implements Serializable {
	private static final long serialVersionUID = -6473171638408382577L;

	@PrimaryKey
	private String name;
	
	@Persistent(serialized = "true")
	@Order(column="JENKEMIMAGE_IRC")
	private ArrayList<Text> irc;
	@Persistent
	private Text html;
	@Persistent
	private Text css;
	
	public JenkemImage() {
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

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
	
}