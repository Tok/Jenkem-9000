package jenkem.shared.color;

public enum IrcColor {
//	          IRC,                              def, old, viv, mon, lsd, ski, bwg,  bw
	white(		0, new int[] { 255, 255, 255 }, 100, 100,  90, 100,  10,  90, 100, 100),
	black(		1, new int[] {   0,   0,   0 }, 100, 100,  90, 100,  10,  90, 100, 100),
	darkBlue(	2, new int[] {   0,   0, 127 },  70,  65,  70,   0,  75,  60,   0,   0),
	darkGreen(	3, new int[] {   0, 147,   0 },  75,  65,  70,   0,  75,  60,   0,   0),
	red(		4, new int[] { 255,   0,   0 }, 100, 100, 100, 100, 100,  90,   0,   0),
	brown(		5, new int[] { 127,   0,   0 },  65,  65,  70,   0,  75,  75,   0,   0),
	purple(		6, new int[] { 156,   0, 156 },  55,  60,  65,   0,  60,  20,   0,   0),
	orange(		7, new int[] { 252, 127,   0 },  75,  80,  85,   0,  75,  75,   0,   0),
	yellow(		8, new int[] { 255, 255,   0 }, 100, 100, 100, 100, 100, 100,   0,   0),
	green(		9, new int[] {   0, 255,   0 }, 100, 100, 100, 100, 100,  90,   0,   0),
	teal(	   10, new int[] {   0, 147, 147 },  55,  60,  65,   0,  75,  60,   0,   0),
	cyan(	   11, new int[] {   0, 255, 255 }, 100, 100, 100, 100, 100,  90,   0,   0),
	blue(	   12, new int[] {   0,   0, 255 }, 100, 100, 100, 100, 100,  90,   0,   0),
	magenta(   13, new int[] { 255,   0, 255 }, 100, 100, 100, 100, 100, 100,   0,   0),
	gray(	   14, new int[] { 127, 127, 127 },  10,  10,  10,   0,   2,   5, 100,   0),
	lightGray( 15, new int[] { 210, 210, 210 },  30,  30,  30,   0,   5,  10, 100,   0);
	
	private Integer value;
	private int[] rgb;
	
	private Integer defaultScheme;
	private Integer oldScheme;
	private Integer vividScheme;
	private Integer monoScheme;
	private Integer lsdScheme;
	private Integer skinScheme;
	private Integer bwgScheme;
	private Integer bwScheme;

	private IrcColor(final Integer value, final int[] rgb, 
			final Integer defaultScheme, final Integer oldScheme, final Integer vividScheme, final Integer monoScheme, 
			final Integer lsdScheme, final Integer skinScheme, final Integer bwgScheme, final Integer bwScheme) {
		this.setValue(value);
		this.setRgb(rgb);
		this.setDefaultScheme(defaultScheme);
		this.setOldScheme(oldScheme);
		this.setVividScheme(vividScheme);
		this.setMonoScheme(monoScheme);
		this.setLsdScheme(lsdScheme);
		this.setSkinScheme(skinScheme);
		this.setBwgScheme(bwgScheme);
		this.setBwScheme(bwScheme);
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

	public void setDefaultScheme(Integer defaultScheme) {
		this.defaultScheme = defaultScheme;
	}

	public Integer getDefaultScheme() {
		return defaultScheme;
	}

	public Integer getOldScheme() {
		return oldScheme;
	}

	public void setOldScheme(Integer oldScheme) {
		this.oldScheme = oldScheme;
	}

	public Integer getVividScheme() {
		return vividScheme;
	}

	public void setVividScheme(Integer vividScheme) {
		this.vividScheme = vividScheme;
	}

	public Integer getMonoScheme() {
		return monoScheme;
	}

	public void setMonoScheme(Integer monoScheme) {
		this.monoScheme = monoScheme;
	}

	public Integer getLsdScheme() {
		return lsdScheme;
	}

	public void setLsdScheme(Integer lsdScheme) {
		this.lsdScheme = lsdScheme;
	}

	public Integer getSkinScheme() {
		return skinScheme;
	}

	public void setSkinScheme(Integer skinScheme) {
		this.skinScheme = skinScheme;
	}

	public Integer getBwgScheme() {
		return bwgScheme;
	}

	public void setBwgScheme(Integer bwgScheme) {
		this.bwgScheme = bwgScheme;
	}

	public Integer getBwScheme() {
		return bwScheme;
	}

	public void setBwScheme(Integer bwScheme) {
		this.bwScheme = bwScheme;
	}

}
