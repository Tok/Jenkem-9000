package jenkem.shared.data;

import java.io.Serializable;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Text;



@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class JenkemImageHtml implements Serializable {
	private static final long serialVersionUID = 4833906739614704L;

	@PrimaryKey
	private String name;
	
	@Persistent
	private Text html;

	
	public JenkemImageHtml() {
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public String getHtml() {
		return html.getValue();
	}
	
	public void setHtml(String html) {
		this.html = new Text(html);
	}
	
}