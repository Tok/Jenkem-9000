package jenkem.shared;

public enum ColorMap {
	Default(100, 10, 30, 100, 100, 100, 100, 100, 100, 100, 65, 75, 55, 70, 75, 55),
	Old(100, 10, 30, 100, 100, 100, 100, 100, 100, 100, 65, 65, 60, 65, 80, 60),
	Vivid(90, 10, 30, 90, 100, 100, 100, 100, 100, 100, 70, 70, 65, 70, 85, 65),
	Mono(100, 0, 0, 100, 100, 100, 100, 100, 100, 100, 0, 0, 0, 0, 0, 0),
	Lsd(10, 2, 5, 10, 100, 100, 100, 100, 100, 100, 75, 75, 75, 75, 75, 60),
	Skin(90, 5, 10, 90, 90, 90, 90, 90, 100, 100, 75, 60, 60, 60, 75, 20),
	Bwg(100, 100, 100, 100, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
	Bw(100, 0, 0, 100, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
	
	public int black;
	public int gray;
	public int lightGray;
	public int white;
	public int red;
	public int green;
	public int blue;
	public int cyan;
	public int yellow;
	public int magenta;
	public int brown;
	public int darkGreen;
	public int teal;
	public int darkBlue;
	public int orange;
	public int purple;
	
	private ColorMap(int black, int gray, int lightGray, int white,
			int red, int green, int blue, int cyan, int yellow, int magenta,
			int brown, int darkGreen, int teal, int darkBlue, int orange, int purple) {
		this.black = black;
		this.gray = gray;
		this.lightGray = lightGray;
		this.white = white;
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.cyan = cyan;
		this.yellow = yellow;
		this.magenta = magenta;
		this.brown = brown;
		this.darkGreen = darkGreen;
		this.teal = teal;
		this.darkBlue = darkBlue;
		this.orange = orange;
		this.purple = purple;		
	}
}
