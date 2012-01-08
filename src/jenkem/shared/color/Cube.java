package jenkem.shared.color;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import jenkem.shared.AsciiScheme;
import jenkem.shared.SchemeUnknownException;


/**
 * This Class is used to manipulate a Cube made of colors.
 * X, Y, Z ---> Red, Green, Blue
 * http://en.wikipedia.org/wiki/RGB_color_space
 * http://upload.wikimedia.org/wikipedia/commons/0/03/RGB_farbwuerfel.jpg
 */
public class Cube {
	public Cube() {
	}

	/**
	 * Translates the RGB values of the pixel to a colored IRC character
	 * @param red 0-255
	 * @param blue 0-255
	 * @param green 0-255
	 * @return a String with the IRC-color codes and the character to display in IRC
	 * @throws SchemeUnknownException 
	 */
	public String getColorChar(Map<String, Integer> colorMap, int red, int blue, int green, boolean enforceBlackFg) {
		
		final double CONTRAST = 0.95d;
		int fixedRed = (int) (red * CONTRAST);
		int fixedBlue = (int) (blue * CONTRAST);
		int fixedGreen = (int) (green * CONTRAST);
		
		int[] col = { fixedRed, fixedBlue, fixedGreen };
		
		Color c = getTwoNearestColors(colorMap, col);

		StringBuilder result = new StringBuilder();
		if(!enforceBlackFg) {
			result.append(c.getFg()); //append the foreground color
		} else {
			result.append("1"); //ignore the actual foreground color and use a black (=1) characters
			//XXX why not use black or white again?
		}
		result.append(",");
		result.append(c.getBg()); //append the background color

		
		AsciiScheme asciiScheme = new AsciiScheme();
		String character = asciiScheme.getChar(c.getBgStrength(), false);
		result.append(character); //append the selected ASCII character
		return result.toString();
	}

	public String getColorChar(Map<String, Integer> colorMap, int red, int blue, int green) {
		return getColorChar(colorMap, red, blue, green, false);
	}

	private WeightedColor createWc(Map<String, Integer> colorMap, int[] col, String name) {
		Integer color = IrcColor.valueOf(name).getValue();
		int[] coords = IrcColor.valueOf(name).getRgb();

		double weight = calcStrength(col, coords, colorMap.get(name));

		WeightedColor wc = new WeightedColor(); //TODO write a Factory
		wc.setWeight(weight);
		wc.setColor(color.toString());
		wc.setCoords(coords);
		wc.setName(name);
		return wc;
	}

	private double calcStrength(int[] col, int[] comp, double factor) {
		return calcDistance(col,comp) / factor;
	}

	/**
	 * This method is where everything happens. it's the tongue of jenkem.
	 * @param col  and array with three RGB values representing the pixel to translate.
	 * @return a Color object with info to represent the same color in irc
	 * @throws SchemeUnknownException 
	 */
	public Color getTwoNearestColors(Map<String, Integer> colorMap ,int[] col) {
		ArrayList<WeightedColor> list = new ArrayList<WeightedColor>();

		for (IrcColor ic : IrcColor.values()) {
			list.add(createWc(colorMap,col,ic.name()));
		}

		//FIXME if randomization is turned off:
		//the color that happens to be the 1st in the collection is used in favor of the others,
		//when more possible values would apply with the same strength.
		//this is not good, because it often favors one random color of (RGB) and of (CYM) over the others.
		//instead, all possibilities of R, G or B should use Black and C, Y or M should use White instead of the color that
		//is selected by doing nothing.
		//fixing this could potentially have a good effect on the output of colorless images with alot of pixels
		//on the black-white scale
		//(=represented by the black to white diagonal in the cube, which has the same distance to all the 3 RGB and the 3 CMY edges).

//		Collections.shuffle(list, new Random()); //shuffle to randomize colors with the same weight

		SortedMap<Double, WeightedColor> map = new TreeMap<Double, WeightedColor>();
		Iterator<WeightedColor> it = list.iterator();
		while (it.hasNext()) {
			WeightedColor wc = (WeightedColor) it.next();
			map.put(wc.getWeight(), wc);
		}

		Map.Entry<Double, WeightedColor> strongest;
		Map.Entry<Double, WeightedColor> second;

		Iterator<Map.Entry<Double, WeightedColor>> i = map.entrySet().iterator();
		strongest = i.next();
		second = i.next();

		Color c = new Color();
		c.setRgb(col); 
		c.setBg(strongest.getValue().getColor());
		c.setBgRgb(strongest.getValue().getCoords());
		c.setFg(second.getValue().getColor());
		c.setFgRgb(second.getValue().getCoords());

		//strength is used to calculate which character to return. 
		//TODO XXX FIXME tune and explain this, #math suggested Vorni diagrams.
		//..using a variable for power and the formula applied in calcStrength
		//is just approximative workaround. instead there should be a mathematically provable 
		//correct way on how to weigh two colors against each other in regard to their 
		//distance to the center of the cube 127,127,127
		double power = 4.00; //default should be 3.00 or 4.00;
		double strongestStrength = 
			Math.pow(
				calcStrength(
						col
						,strongest.getValue().getCoords()
						,colorMap.get(strongest.getValue().getName())
					),power
				);
		double secondStrength = 
			Math.pow(
				calcStrength(
						col
						,second.getValue().getCoords()
						,colorMap.get(second.getValue().getName())
					),power
				);		
		double strength = strongestStrength / secondStrength;
		c.setBgStrength(strength);

		return c;
	}

	public Color getTwoNearestColors(Map<String, Integer> colorMap, int red, int green, int blue) {
		int[] col = { red, blue, green };
		return getTwoNearestColors(colorMap, col);
	}

	/**
	 * Calculates the distance between two points (=colors) in the RGB color-space cube (255^3)
	 * @param from RGB colors
	 * @param to RGB colors
	 * @return the distance between the colors
	 */
	private double calcDistance(final int[] from, final int[] to) {
		double fromRed = from[0];
		double fromGreen = from[1];
		double fromBlue = from[2];
		double toRed = to[0];
		double toGreen = to[1];
		double toBlue = to[2];
		double distance = Math.sqrt(
				(Math.pow(toRed - fromRed, 2.0) +
				 Math.pow(toGreen - fromGreen, 2.0) +
				 Math.pow(toBlue - fromBlue, 2.0))
		);
		return distance;
	}

	public boolean isFirstCloserTo(int[] first, int[] second, int[] compare, double offset) {
		double firstDist = calcDistance(first, compare);
		double secondDist = calcDistance(second, compare);
		if ((firstDist + offset) < secondDist) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * compares two colors to a third and returns true if the
	 * first one is closes to the third than the second.
	 * @param first
	 * @param second
	 * @param compare
	 * @return
	 */
	public boolean isFirstCloserTo(int[] first, int[] second, int[] compare) {
		//TODO this is awkward. replace this method with a method 
		//that returns an int that is negative, 0 or positive
		double firstDist = calcDistance(first, compare);
		double secondDist = calcDistance(second, compare);
		if (firstDist < secondDist) {
			return true;
		} else {
			return false;
		}
	}

}