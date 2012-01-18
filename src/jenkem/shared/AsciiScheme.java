package jenkem.shared;

import java.util.Random;

public class AsciiScheme {
	private boolean postProcessed = true;
	private boolean processed = true;
	private boolean randomized = true;

	private String up = "\"";
	private String down = "_";
	private String hline = "-";
	private String vline = "|";
	private String upDown = "\\";
	private String downUp = "/";
	private String leftDown = "L";
	private String rightDown = "J";
	private String leftUp = "F";
	private String rightUp = "q";
	private String left = "[";
	private String right = "]";

	public String getChar(double strength, final CharacterSet preset, final boolean isAbsolute) {
		if (isAbsolute) {
			strength = (255 - strength) / 255;
		}
		return getChar(strength, preset);
	}

	/**
	 * selects a character from the provided ASCII palette and selects the
	 * character to use depending on the entered strength value.
	 * ascii must be a String like ' .:xX#', from bright to dark.
	 * @param relStrength
	 *            a relative value between 0.0 and 1.0
	 * @return String with the character to use. (assuming bg > fg)
	 */
	private String getChar(double relStrength, final CharacterSet preset) {
		if (relStrength > 1) {
			relStrength = 1.0D;
		}
		if (relStrength < 0) {
			relStrength = 0.0D;
		}
		final double th = 1.0 / preset.getCharacters().length();
		String ret = "";
		for (int i = 0; i < preset.getCharacters().length(); i++) {
			if (relStrength <= (i + 1) * th) {
				ret = Character.toString(preset.getCharacters().toCharArray()[i]);
				break; // we have a winrar
			}
		}
		if (ret.length() == 0) { //TODO
			ret = "!"; // assert this forbidden character must never appear!!1
		}
		return ret;
	}

	private String randomize(final String in) {
		if (randomized) {
			char[] c = in.toCharArray();
			return String.valueOf(c[new Random().nextInt(c.length)]);
		} else {
			return in.substring(0, 1);
		}
	}

	private String randomizeSix(String in) {
		if (randomized) {
			try {
				if (new Random().nextDouble() <= 0.3d) {
					return in.substring(0, 2);
				} else if (new Random().nextDouble() <= 0.3d) { 
					return in.substring(2, 4);
				} else {
					return in.substring(4, 6);
				}
			} catch (IndexOutOfBoundsException ioobe) {
				//just ignore the error and return 2
				return in.substring(0, 2);
			}
		} else {
			return in.substring(0, 2);
		}
	}

	public boolean isCharacterDark(final String character, final CharacterSet preset) {
		final int halfLength = (preset.getCharacters().length() + 3) / 2; //cutting off the decimals is OK here
		for (int i = 0; i <= halfLength; i++) {
			String compare = preset.getCharacters().substring(preset.getCharacters().length() - i - 1);
			if (unFormat(character).equals(compare)) {
				return true;
			}
		}
		return false;
	}

	public boolean isCharacterBright(final String character, final CharacterSet preset) {
		final int halfLength = (preset.getCharacters().length() + 3) / 2; //cutting off the decimals is OK here
		for (int i = 0; i <= halfLength; i++) {
			String compare = preset.getCharacters().substring(i);
			if (unFormat(character).equals(compare)) {
				return true;
			}
		}
		return false;
	}

	private String unFormat(final String in) {
		if (in.length() > 1) {
			return in.substring(in.length() - 1, in.length());
		} else {
			return in;
		}
	}

	/**
	 * utility method
	 * 
	 * @param in
	 * @param rep
	 * @return modified String
	 */
	public String replace(final String in, final String rep) {
		return in.substring(0, in.length() - 1) + rep;
	}

	public void setPostProcessed(boolean postProcessed) {
		this.postProcessed = postProcessed;
	}

	public boolean isPostProcessed() {
		return postProcessed;
	}

	public void setProcessed(boolean processed) {
		this.processed = processed;
	}

	public boolean isProcessed() {
		return processed;
	}

	public void setRandomized(boolean randomized) {
		this.randomized = randomized;
	}

	public boolean isRandomized() {
		return randomized;
	}

	public String getUp() {
		return up;
	}

	public String selectUp() {
		return randomize(up);
	}

	public void setUp(String up) {
		this.up = up;
	}

	public String getDown() {
		return down;
	}

	public String selectDown() {
		return randomize(down);
	}

	public void setDown(String down) {
		this.down = down;
	}

	public String getHline() {
		return hline;
	}

	public String selectHline() {
		return randomize(hline);
	}

	public void setHline(String hline) {
		this.hline = hline;
	}

	public String getVline() {
		return vline;
	}

	public String selectVline() {
		return randomize(vline);
	}

	public void setVline(String vline) {
		this.vline = vline;
	}

	public String getUpDown() {
		return upDown;
	}

	public String selectUpDown() {
		return randomizeSix(upDown);
	}

	public void setUpDown(String upDown) {
		this.upDown = upDown;
	}

	public String getDownUp() {
		return downUp;
	}

	public String selectDownUp() {
		return randomizeSix(downUp);
	}

	public void setDownUp(String downUp) {
		this.downUp = downUp;
	}

	public String getLeftDown() {
		return leftDown;
	}

	public String selectLeftDown() {
		return randomize(leftDown);
	}

	public void setLeftDown(String leftDown) {
		this.leftDown = leftDown;
	}

	public String getRightDown() {
		return rightDown;
	}

	public String selectRightDown() {
		return randomize(rightDown);
	}

	public void setRightDown(String rightDown) {
		this.rightDown = rightDown;
	}

	public String getLeftUp() {
		return leftUp;
	}

	public String selectLeftUp() {
		return randomize(leftUp);
	}

	public void setLeftUp(String leftUp) {
		this.leftUp = leftUp;
	}

	public void setRightUp(String rightUp) {
		this.rightUp = rightUp;
	}

	public String getRightUp() {
		return rightUp;
	}

	public String selectRightUp() {
		return randomize(rightUp);
	}

	public String getLeft() {
		return left;
	}

	public String selectLeft() {
		return randomize(left);
	}

	public void setLeft(String left) {
		this.left = left;
	}

	public String getRight() {
		return right;
	}

	public String selectRight() {
		return randomize(right);
	}

	public void setRight(String right) {
		this.right = right;
	}

	public String getDarkestCharacter(final CharacterSet preset) {
		return preset.getCharacters().substring(preset.getCharacters().length());
	}

}