package jenkem.shared.color;

public class WeightedColor {
	String color;
	int[] coords;
	String name;
	Double weight;
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public int[] getCoords() {
		return coords;
	}
	public void setCoords(int[] coords) {
		this.coords = coords;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Double getWeight() {
		return weight;
	}
	public void setWeight(Double weight) {
		this.weight = weight;
	}
}