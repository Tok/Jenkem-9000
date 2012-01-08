package jenkem.shared.color;

import java.util.Map;

public class Scheme {
	private String name;
	private Map<String, Integer> colorMap;
	
	public void setName(final String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setColorMap(final Map<String, Integer> colorMap) {
		this.colorMap = colorMap;
	}
	public Map<String, Integer> getColorMap() {
		return colorMap;
	}
}