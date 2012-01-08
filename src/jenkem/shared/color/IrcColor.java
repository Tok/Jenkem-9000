package jenkem.shared.color;

public enum IrcColor {
	white(		0, new int[] { 255, 255, 255 }, 100),
	black(		1, new int[] {   0,   0,   0 }, 100),
	darkBlue(	2, new int[] {   0,   0, 127 },  70),
	darkGreen(	3, new int[] {   0, 147,   0 },  75),
	red(		4, new int[] { 255,   0,   0 }, 100),
	brown(		5, new int[] { 127,   0,   0 },  65),
	purple(		6, new int[] { 156,   0, 156 },  55),
	orange(		7, new int[] { 252, 127,   0 },  75),
	yellow(		8, new int[] { 255, 255,   0 }, 100),
	green(		9, new int[] {   0, 255,   0 }, 100),
	teal(	   10, new int[] {   0, 147, 147 },  55),
	cyan(	   11, new int[] {   0, 255, 255 }, 100),
	blue(	   12, new int[] {   0,   0, 255 }, 100),
	magenta(   13, new int[] { 255,   0, 255 }, 100),
	gray(	   14, new int[] { 127, 127, 127 },  10),
	lightGray( 15, new int[] { 210, 210, 210 },  30);
	
	private Integer value;
	private int[] rgb;
	private Integer defaultScheme;
	
	private IrcColor(Integer value, int[] rgb, Integer defaultScheme) {
		this.setValue(value);
		this.setRgb(rgb);
		this.setDefaultScheme(defaultScheme);
	}

	public void setDefaultScheme(Integer defaultScheme) {
		this.defaultScheme = defaultScheme;
	}

	public Integer getDefaultScheme() {
		return defaultScheme;
	}

	public void setRgb(int[] rgb) {
		this.rgb = rgb;
	}

	public int[] getRgb() {
		return rgb;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public Integer getValue() {
		return value;
	}

	public static boolean isIrcColor(int irc) {
		for (IrcColor color : IrcColor.values()) {
			if (color.getValue().equals(irc)) {
				return true;
			}
		}
		return false;
	}
}
