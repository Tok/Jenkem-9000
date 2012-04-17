package jenkem.shared.color;

/**
 * Represents a color for irc etc.
 */
public class Color {
    private int[] rgb; // only used for phpbbCode and full-hd html
    private String fg; // foreground color
    private int[] fgRgb; // internal
    private String bg; // background color
    private int[] bgRgb; // internal
    private double bgStrength; // represents the strength of the background compared to the foreground.

    public final int[] getRgb() {
        return rgb;
    }

    public final void setRgb(final int[] rgb) {
        this.rgb = rgb;
    }

    public final String getFg() {
        return fg;
    }

    public final void setFg(final String fg) {
        this.fg = fg;
    }

    public final String getBg() {
        return bg;
    }

    public final void setBg(final String bg) {
        this.bg = bg;
    }

    public final double getBgStrength() {
        return bgStrength;
    }

    public final void setBgStrength(final double bgStrength) {
        this.bgStrength = bgStrength;
    }

    public final void setFgRgb(final int[] fgRgb) {
        this.fgRgb = fgRgb;
    }

    public final int[] getFgRgb() {
        return fgRgb;
    }

    public final void setBgRgb(final int[] bgRgb) {
        this.bgRgb = bgRgb;
    }

    public final int[] getBgRgb() {
        return bgRgb;
    }
}
