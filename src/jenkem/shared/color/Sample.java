package jenkem.shared.color;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.canvas.dom.client.ImageData;

/**
 * Represents the RGBcolors of four pixels from the provided image.
 * TL | TR
 * ---+---
 * BL | BR
 */
public class Sample {
	public enum Col {
		RED, GREEN, BLUE;
	}
	public enum Ydir {
		TOP, BOT;
	}
	public enum Xdir {
		LEFT, RIGHT;
	}

	private Map<String, Integer> values = new HashMap<String, Integer>(12);

	public static class SampleKey {
		private final Col col;
		private final Ydir ydir;
		private final Xdir xdir;
		private SampleKey(Col col, Ydir ydir, Xdir xdir) {
			this.col = col;
			this.ydir = ydir;
			this.xdir = xdir;
		}
		public static String getKey(Col col, Ydir ydir, Xdir xdir) {
			return new SampleKey(col, ydir, xdir).toString();
		}
		public String toString() {
			return col.name() + ydir.name() + xdir.name();
		}
	}
	
	public static Sample getInstance(final ImageData img, final int x, final int y, final double contrast, final int brightness) {
		return new Sample(img, x, y, contrast, brightness);
	}
	
	private Sample(final ImageData img, final int x, final int y, final double contrast, final int brightness) {
		for (Col col : Col.values()) {
			values.put(SampleKey.getKey(col, Ydir.TOP, Xdir.LEFT), takeColor(img, col, x, y, contrast, brightness));
			values.put(SampleKey.getKey(col, Ydir.BOT, Xdir.LEFT), takeColor(img, col, x, y+1, contrast, brightness));
			if (img.getWidth() <= x + 1) {
				values.put(SampleKey.getKey(col, Ydir.TOP, Xdir.RIGHT), takeColor(img, col, x+1, y, contrast, brightness)); 
				values.put(SampleKey.getKey(col, Ydir.BOT, Xdir.RIGHT), takeColor(img, col, x+1, y+1, contrast, brightness));
			} else { //fallback by using the pixel to the left
				values.put(SampleKey.getKey(col, Ydir.TOP, Xdir.RIGHT), takeColor(img, col, x, y, contrast, brightness));
				values.put(SampleKey.getKey(col, Ydir.BOT, Xdir.RIGHT), takeColor(img, col, x, y+1, contrast, brightness));
			}
		}
	}

	public int get(Col col, Ydir yDir, Xdir xDir) {
		final String key = SampleKey.getKey(col, yDir, xDir);
		if (values.containsKey(key)) {
			return values.get(key);
		} else {
			return 0;
		}
	}
	
	public int get(Col col, Xdir xDir) {
		final String firstKey = SampleKey.getKey(col, Ydir.TOP, xDir);
		final String secondKey = SampleKey.getKey(col, Ydir.BOT, xDir);
		if (values.containsKey(firstKey)) {
			if (values.containsKey(secondKey)) { //TODO test to make sure no other cases can be true
				return values.get(firstKey) + values.get(secondKey) / 2;
			} else {
				return values.get(firstKey) / 2;
			}
		} else {
			if (values.containsKey(secondKey)) {
				return values.get(secondKey) / 2;
			} else {
				return 0;
			}
		}
	}

	public int get(Col col, Ydir yDir) {
		final String firstKey = SampleKey.getKey(col, yDir, Xdir.LEFT);
		final String secondKey = SampleKey.getKey(col, yDir, Xdir.RIGHT);
		if (values.containsKey(firstKey)) {
			if (values.containsKey(secondKey)) {
				return values.get(firstKey) + values.get(secondKey) / 2;				
			} else {
				return values.get(firstKey) / 2;				
			}
		} else {
			if (values.containsKey(secondKey)) {
				return values.get(secondKey) / 2;				
			} else {
				return 0;
			}
		}
	}

	public int[] getRgbValues(Xdir xDir) {
		int[] rgb = { get(Col.RED, xDir), get(Col.GREEN, xDir), get(Col.BLUE, xDir) };
		return rgb;
	}
	
	public int[] getRgbValues(Ydir yDir) {
		int[] rgb = { get(Col.RED, yDir), get(Col.GREEN, yDir), get(Col.BLUE, yDir) };
		return rgb;
	}
	
	public int[] getRgbValues(Ydir yDir, Xdir xDir) {
		int[] rgb = { get(Col.RED, xDir), get(Col.GREEN, xDir), get(Col.BLUE, xDir) };
		return rgb;
	}
	
	/**
	 * Calculates the RGB values of the pixel in the provided imageData at position x, y
	 * and applies the provided contrast and brightness.
	 * @param id ImageData
	 * @param x the target pixel column
	 * @param y the target pixel row
	 * @param contrast is multiplied with the result
	 * @param brightness is added to the result
	 * @return an int[] of size 3 with the values for red, green and blue.
	 */
	public static int[] calculateRgb(ImageData id, int x, int y,
			double contrast, int brightness) {
		final int[] result = { 
			calculateColor(id.getRedAt(x, y), contrast, brightness), 
			calculateColor(id.getGreenAt(x, y), contrast, brightness), 
			calculateColor(id.getBlueAt(x, y), contrast, brightness) };
		return result;
	}

	private static int takeColor(ImageData id, Col col, int x, int y, double contrast, int brightness) {
		if (col.equals(Col.RED)) {
			return calculateColor(id.getRedAt(x, y), contrast, brightness);
		} else if (col.equals(Col.GREEN)) {
			return calculateColor(id.getGreenAt(x, y), contrast, brightness);
		} else if (col.equals(Col.BLUE)) {
			return calculateColor(id.getBlueAt(x, y), contrast, brightness);			
		}
		assert false;
		return 0;
	}
	
	/**
	 * Applies the contrast and brightness to the provided value
	 * and makes sure that the result is kept in range.
	 * @param input
	 * @param contrast
	 * @param brightness
	 * @return int the correted value
	 */
	private static int calculateColor(int input, double contrast, int brightness) {
		return keepInRange((int) (input * contrast) + brightness);
	}

	/**
	 * Makes sure that the provided color is kept in range between 0 and 255.
	 * @param colorComponent value after correction
	 * @return value of the provided colorComponent between 0 and 255
	 */
	public static int keepInRange(final int colorComponent) {
		if (colorComponent > 255) {
			return 255;
		} else if (colorComponent < 0) {
			return 0;
		} else {
			return colorComponent;
		}
	}


}
