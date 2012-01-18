package jenkem.shared;

public enum CharacterSet {
	Hard(" -+xX#"),
	DoubleHard(" -+xX##"),
	TrippleHard(" -+xXX###"),
	Soft(" .:oO@"),
	DoubleSoft(" .:oO@@"),
	TrippleSoft(" .:oOO@@@"),
	Ansi(" ░▒▓"),
	DoubleAnsi(" ░▒▒▓▓▓");	
	//XXX numbers can't be used
	
	private String characters;
	
	private CharacterSet(final String ascii) {
		this.characters = ascii;
	}

	public String getCharacters() {
		return characters;
	}

	public String toString() {
		return characters;
	}
}
