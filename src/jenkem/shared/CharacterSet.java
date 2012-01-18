package jenkem.shared;

public enum CharacterSet {
	Hard(" -+xX#"),
	Soft(" .:oO@"),
	Ansi(" ░▒▓"),
	HalfHard("      -----++++xxxXX#"),
	HalfSoft("      .....::::oooOO@"),
	HalfAnsi("   ░░░▒▒▓"),
	DoubleHard(" --+++xxxxXXXXX######"),
	DoubleSoft(" ..:::ooooOOOOO@@@@@@"),
	DoubleAnsi(" ░░▒▒▒▓▓▓▓");
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
