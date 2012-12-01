package jenkem.shared.color;

public final class WeightedColor {
    private final String color;
    private final int[] coords;
    private final String name;
    private final Double weight;

    public static WeightedColor getInstance(final String name,
            final int[] coords, final double weight) {
        return new WeightedColor(name, coords, weight);
    }

    private WeightedColor(final String name, final int[] coords,
            final double weight) {
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
