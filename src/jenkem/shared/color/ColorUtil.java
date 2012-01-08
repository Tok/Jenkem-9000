package jenkem.shared.color;
import jenkem.shared.ColorUnknownException;

/**
 * just some constants and stuff.
 */
public class ColorUtil {
//	private HashMap<String, Integer> ircColors; //TODO use enums instead?
//	private HashMap<String, int[]> rgbColors; //(according to mIRC, as measured using gimp)

	//conventional ASCII color codes according to mIRC
	public static final String CC = ""; // start irc color //TODO better not like this
	public static final String BC = ""; // bold character for irc

	/**
	 * Colors the entered String black or white, depending of the provided background color.
	 * @param col the color for the background (fg will become white or black)
	 * @param input the message to color
	 * @return the colored message
	 */
	public String colorConfig(Integer col, String input) {
		input = input + "% ";
		if (col.equals(IrcColor.white.getValue())
				|| col.equals(IrcColor.cyan.getValue()) 
				|| col.equals(IrcColor.yellow.getValue()) 
				|| col.equals(IrcColor.magenta.getValue()) 
				|| col.equals(IrcColor.green.getValue()) 
				|| col.equals(IrcColor.orange.getValue()) 				
				|| col.equals(IrcColor.lightGray.getValue())) {
			return concatColorConfig(IrcColor.black.getValue(), col, input);
		} else {
			return concatColorConfig(IrcColor.white.getValue(), col, input);
		}
	}

	private String concatColorConfig(Integer fg, Integer bg, String input) {
		return CC + fg + "," + bg + " " + input + CC;	
	}

	public String[] makeBold(String[] ircOutput) {
		String[] result = new String[ircOutput.length];
		for (int i = 0; i < ircOutput.length; i++) {
			result[i] = BC + ircOutput[i];
		}
		return result;
	}

	public String ircToCss(String irc) throws ColorUnknownException {
		return ircToCss(Integer.valueOf(irc));
	}
	public String ircToCss(int irc) throws ColorUnknownException {
		if (!IrcColor.isIrcColor(irc)) {
			throw new ColorUnknownException(String.valueOf(irc));
		}
		Integer ircString = Integer.valueOf(irc);
		String css = "#000000"; //assume default black
		
		
		for (IrcColor ircCol : IrcColor.values()) {
			if (ircString.equals(ircCol.getValue())) { 
				css = rgbToCss(ircCol.getRgb()); 
			}
		}
		
//		Iterator<String> iterator = ircColors.keySet().iterator();
//		while (iterator.hasNext()) {
//			String colorName = iterator.next();
//			if (ircString.equals(ircColors.get(colorName))) { 
//				css = rgbToCss(rgbColors.get(colorName)); 
//			}
//		}
		
		
		return css;
	}

	private static String rgbToCss(int[] rgb) {
		return "#" + toHex(rgb[0]) + toHex(rgb[1]) + toHex(rgb[2]);
	}

	public static String toHex(int i) {
		String ret = Integer.toHexString(i);
		if (ret.length() == 1) {
			ret = "0" + ret;
		}
		return ret;
	}

//	public boolean isColor(int col) {
//		return ircColors.containsValue(Integer.valueOf(col));
//	}
//
//	public boolean isColorName(String name) {
//		return ircColors.containsKey(name);
//	}

}
