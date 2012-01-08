package jenkem.shared.color;

/**
 * represents a color for irc etc.
 */
public class Color {
	private int[] rgb; //only used for phpbbCode and full-hd html
	private String fg; //foreground color
	private int[] fgRgb; //internal
	private String bg; //background color
	private int[] bgRgb; //internal
	private double bgStrength; //represents the strength of the background compared to the foreground.
	
	public int[] getRgb() {
		return rgb;
	}
	public void setRgb(final int[] rgb) {
		this.rgb = rgb;
	}
	public String getFg() {
		return fg;
	}
	public void setFg(final String fg) {
		this.fg = fg;
	}
	public String getBg() {
		return bg;
	}
	public void setBg(final String bg) {
		this.bg = bg;
	}
	public double getBgStrength() {
		return bgStrength;
	}
	public void setBgStrength(final double bgStrength) {
		this.bgStrength = bgStrength;
	}
	public void setFgRgb(final int[] fgRgb) {
		this.fgRgb = fgRgb;
	}
	public int[] getFgRgb() {
		return fgRgb;
	}
	public void setBgRgb(final int[] bgRgb) {
		this.bgRgb = bgRgb;
	}
	public int[] getBgRgb() {
		return bgRgb;
	}
}