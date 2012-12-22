package jenkem.shared.color;

public final class WeightedColor {
    private final IrcColor color;
    private final Double weight;

    public static WeightedColor getInstance(final IrcColor color, final double weight) {
        return new WeightedColor(color, weight);
    }

    private WeightedColor(final IrcColor color, final double weight) {
        this.color = color;
        this.weight = weight;
    }

    public IrcColor getColor() {
        return color;
    }

    public int[] getCoords() {
        return CopyUtil.makeCopy(color.getRgb());
    }

    public Double getWeight() {
        return weight;
    }

}
