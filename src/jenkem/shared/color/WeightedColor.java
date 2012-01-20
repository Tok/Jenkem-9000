package jenkem.shared.color;


public class WeightedColor {
	private String color;
	private int[] coords;
	private String name;
	private Double weight;
	
	public static WeightedColor getInstance(String name, int[] coords, double weight) {
		return new WeightedColor(name, coords, weight);
	}
	
	private WeightedColor(String name, int[] coords, double weight) {
		this.name = name;
		this.color = IrcColor.valueOf(name).getValue().toString();
		this.coords = coords;
		this.weight = weight;
	}
	
	public String getColor() {
		return color;
	}

	public int[] getCoords() {
		return coords;
	}

	public String getName() {
		return name;
	}

	public Double getWeight() {
		return weight;
	}

}