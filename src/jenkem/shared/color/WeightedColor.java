package jenkem.shared.color;

public class WeightedColor {
	private String color;
	private int[] coords;
	private String name;
	private Double weight;
	
	public String getColor() {
		return color;
	}
	public void setColor(final String color) {
		this.color = color;
	}
	public int[] getCoords() {
		return coords;
	}
	public void setCoords(final int[] coords) {
		this.coords = coords;
	}
	public String getName() {
		return name;
	}
	public void setName(final String name) {
		this.name = name;
	}
	public Double getWeight() {
		return weight;
	}
	public void setWeight(final Double weight) {
		this.weight = weight;
	}
}