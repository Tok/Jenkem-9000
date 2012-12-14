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
        return CopyUtil.makeCopy(rgb);
    }

    public final void setRgb(final int[] rgb) {
        this.rgb = CopyUtil.makeCopy(rgb);
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
        this.fgRgb = CopyUtil.makeCopy(fgRgb);
    }

    public final int[] getFgRgb() {
        return CopyUtil.makeCopy(fgRgb);
    }

    public final void setBgRgb(final int[] bgRgb) {
        this.bgRgb = CopyUtil.makeCopy(bgRgb);
    }

    public final int[] getBgRgb() {
        return CopyUtil.makeCopy(bgRgb);
    }
}
