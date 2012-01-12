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
public class JenkemImageIrc implements Serializable {
	private static final long serialVersionUID = -8360097117647923335L;

	@PrimaryKey
	private String name;
	
	@Persistent(serialized = "true")
	@Order(column="JENKEMIMAGEIRC_IRC")
	private ArrayList<Text> irc;
	
	public JenkemImageIrc() {
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
	
}