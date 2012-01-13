package jenkem.shared;

public enum AsciiPreset {
	Hard(" -+xX#"),
	DoubleHard(" -+xX##"),
	TrippleHard(" -+xXX###"),
	Soft(" .:oO@"),
	DoubleSoft(" .:oO@@"),
	TrippleSoft(" .:oOO@@@");
	//XXX numbers can't be used
	
	private String ascii;
	
	private AsciiPreset(final String ascii) {
		this.ascii = ascii;
	}

	public String getAscii() {
		return ascii;
	}

	public String toString() {
		return ascii;
	}
}
