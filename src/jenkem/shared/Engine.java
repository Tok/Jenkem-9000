package jenkem.shared;

import java.util.HashMap;
import java.util.Map;

import jenkem.client.presenter.MainPresenter;
import jenkem.shared.color.Color;
import jenkem.shared.color.ColorUtil;
import jenkem.shared.color.Cube;
import jenkem.shared.color.IrcColor;
import jenkem.shared.color.Sample;

import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

public class Engine {
	private final Cube cube = new Cube();
	private final AsciiScheme asciiScheme = new AsciiScheme();
	private final MainPresenter presenter;
	
	private Map<String, Integer> colorMap;
	private ImageData id;
	private CharacterSet preset;
	private double contrast;
	private int brightness;

	private int startX;
	private int startY;

	/**
	 * Public constructor
	 * @param presenter with a method to add converted lines.
	 */
	public Engine(MainPresenter presenter) {
		this.presenter = presenter;
	}
	
	/**
	 * Initializes Engine and starts Full-HD mode conversion.
	 * Prepares the colorMap, sets the variables and starts generating the first line of the output.
	 * @param id the ImageData
	 * @param preset the CharacterSet to use
	 * @param contrast value will be multiplied with the rgb of each pixel
	 * @param brightness value will be added to the rgb of each pixel
	 */
	public void generateHighDef(final ImageData id, final ColorScheme scheme, final CharacterSet preset, final double contrast, final int brightness) {
		this.colorMap = prepareColorMap(scheme);
		this.id = id;
		this.preset = preset;
		this.contrast = contrast;
		this.brightness = brightness;
		generateHighDefLine(0); //start by triggering the conversion of the 1st line
	}
	
	/**
	 * Initializes Engine and starts Super-Hybrid mode conversion.
	 * Prepares the colorMap, sets the variables and starts generating the first line of the output.
	 * @param id the ImageData
	 * @param preset the CharacterSet to use
	 * @param contrast value will be multiplied with the rgb of each pixel
	 * @param brightness value will be added to the rgb of each pixel
	 * @param kick
	 */
	public void generateSuperHybrid(final ImageData id, final ColorScheme scheme, final CharacterSet preset, final double contrast, final int brightness, final Kick kick) {
		this.colorMap = prepareColorMap(scheme);
		this.id = id;
		this.preset = preset;
		this.contrast = contrast;
		this.brightness = brightness;
		applyKicks(kick);		
		generateSuperHybridLine(0 + startY); //start by triggering the conversion of the 1st line
	}
	
	/**
	 * Initializes Engine and starts Hybrid mode conversion.
	 * Prepares the colorMap, sets the variables and starts generating the first line of the output.
	 * @param id the ImageData
	 * @param preset the CharacterSet to use
	 * @param contrast value will be multiplied with the rgb of each pixel
	 * @param brightness value will be added to the rgb of each pixel
	 * @param kick
	 */
	public void generateHybrid(final ImageData id, final ColorScheme scheme, final CharacterSet preset, final double contrast, final int brightness, final Kick kick) {
		this.colorMap = prepareColorMap(scheme);
		this.id = id;
		this.preset = preset;
		this.contrast = contrast;
		this.brightness = brightness;
		applyKicks(kick);
		generateHybridLine(0 + startY); //start by triggering the conversion of the 1st line
	}
	
	/**
	 * Initializes Engine and starts Pwntari mode conversion.
	 * Prepares the colorMap, sets the variables and starts generating the first line of the output.
	 * @param id the ImageData
	 * @param preset the CharacterSet to use
	 * @param contrast value will be multiplied with the rgb of each pixel
	 * @param brightness value will be added to the rgb of each pixel
	 * @param kick
	 */
	public void generatePwntari(final ImageData id, final ColorScheme scheme, final CharacterSet preset, final double contrast, final int brightness, final Kick kick) {
		this.colorMap = prepareColorMap(scheme);
		this.id = id;
		this.preset = preset;
		this.contrast = contrast;
		this.brightness = brightness;
		applyKicks(kick);
		generatePwntariLine(0 + startY); //start by triggering the conversion of the 1st line
	}

	/**
	 * Initializes Engine and starts Plain mode conversion.
	 * Prepares the colorMap, sets the variables and starts generating the first line of the output.
	 * @param id the ImageData
	 * @param preset the CharacterSet to use
	 * @param contrast value will be multiplied with the rgb of each pixel
	 * @param brightness value will be added to the rgb of each pixel
	 * @param kick
	 */
	public void generatePlain(final ImageData id, final CharacterSet preset, final double contrast, final int brightness, final Kick kick) {
		this.id = id;
		this.preset = preset;
		this.contrast = contrast;
		this.brightness = brightness;
		applyKicks(kick);
		generatePlainLine(0 + startY); //start by triggering the conversion of the 1st line
	}
		
	/**
	 * Generates a line in HD mode and adds it to the presenter, 
	 * which will recall this method again with an increased y until all
	 * data is converted.
	 * @param index row of pixels in the ImageData to convert
	 */
	public void generateHighDefLine(final int index) {
		final StringBuilder line = new StringBuilder();
		String oldPix;
		String newPix = null;
		for (int x = 0; x < id.getWidth(); x++) { //this method can handle uneven image widths
			final int rgb[] = Sample.calculateRgb(id, x, index, contrast, brightness);
			oldPix = newPix;
			newPix = cube.getColorChar(colorMap, preset, rgb, false); // the cube is used here.
			if (newPix.equals(oldPix)) { //don't change color
				String charOnly = newPix.substring(newPix.length() - 1, newPix.length());
				line.append(charOnly);
			} else { //do color change
				if (line.length() > 0) {
					line.append(ColorUtil.CC); //closes the last CC used
				}
				line.append(ColorUtil.CC); //adds the new CC
				line.append(newPix); //and the new character
			}
		}
		line.append(ColorUtil.CC); //closes the last CC in the line
		presenter.addIrcOutputLine(line.toString(), index);
	}
	
	/**
	 * Generates a line in super hybrid mode and adds it to the presenter, 
	 * which will recall this method again with an increased y until all
	 * data is converted.
	 * @param index row of pixels in the ImageData to convert
	 */
	public void generateSuperHybridLine(final int index) {
		final StringBuilder row = new StringBuilder();
		String oldLeft;
		String newLeft = null;
		String newRight = null;
		for (int x = startX; x < getEvenWidth(); x = x + 2) { //this method can handle uneven image widths
			final Sample sample = Sample.getInstance(id, x, index, contrast, brightness);
			
			oldLeft = newLeft;
			newLeft = cube.getColorChar(colorMap, preset, sample, Sample.Xdir.LEFT);
			newRight = cube.getColorChar(colorMap, preset, sample, Sample.Xdir.RIGHT);
			final int[] leftRgb = sample.getRgbValues(Sample.Xdir.LEFT);
			final Color leftCol = cube.getTwoNearestColors(colorMap, leftRgb);
			final int[] rightRgb = sample.getRgbValues(Sample.Xdir.RIGHT);
			final Color rightCol = cube.getTwoNearestColors(colorMap, rightRgb);
			final int[] leftTopRgb = sample.getRgbValues(Sample.Ydir.TOP, Sample.Xdir.LEFT);
			final Color leftTopCol = cube.getTwoNearestColors(colorMap, leftTopRgb);
			final int[] leftBottomRgb = sample.getRgbValues(Sample.Ydir.BOT, Sample.Xdir.LEFT);
			final Color leftBottomCol = cube.getTwoNearestColors(colorMap, leftBottomRgb);
			final int[] rightTopRgb = sample.getRgbValues(Sample.Ydir.TOP, Sample.Xdir.RIGHT);
			final Color rightTopCol = cube.getTwoNearestColors(colorMap, rightTopRgb);
			final int[] rightBottomRgb = sample.getRgbValues(Sample.Ydir.BOT, Sample.Xdir.RIGHT);
			final Color rightBottomCol = cube.getTwoNearestColors(colorMap, rightBottomRgb);
			final double offset = +32.0D;
			if (cube.isFirstCloserTo(leftBottomCol.getRgb(), leftTopCol.getRgb(), leftCol.getFgRgb(), offset)) {
				if (rightCol.getBg().equals(leftCol.getFg())) {
					newLeft = newLeft.substring(0, newLeft.length() - 1) + asciiScheme.selectRightDown(); //d
				} else {
					newLeft = newLeft.substring(0, newLeft.length() - 1) + asciiScheme.selectDown(); // _
				}
			} else if (cube.isFirstCloserTo(leftTopCol.getRgb(), leftBottomCol.getRgb(), leftCol.getFgRgb(), offset)) {
				if (rightCol.getBg().equals(leftCol.getFg())) {
					newLeft = newLeft.substring(0, newLeft.length() - 1) + asciiScheme.selectRightUp(); //q
				} else {
					newLeft = newLeft.substring(0, newLeft.length() - 1) + asciiScheme.selectUp(); // "
				}
			}
			if (cube.isFirstCloserTo(rightBottomCol.getRgb(), rightTopCol.getRgb(), rightCol.getFgRgb(), offset)) {
				if (leftCol.getBg().equals(rightCol.getFg())) {
					newRight = newRight.substring(0, newRight.length() - 1) + asciiScheme.selectLeftDown(); //b
				} else {
					newRight = newRight.substring(0, newRight.length() - 1) + asciiScheme.selectDown(); // _
				}
			} else if (cube.isFirstCloserTo(rightTopCol.getRgb(), rightBottomCol.getRgb(), rightCol.getFgRgb(), offset)) {
				if (leftCol.getBg().equals(rightCol.getFg())) { //compare distance instead of equality?
					newRight = newRight.substring(0, newRight.length() - 1) + asciiScheme.selectLeftUp(); //P
				} else {
					newRight = newRight.substring(0, newRight.length() - 1) + asciiScheme.selectUp(); // "
				}
			}
			if (newLeft.equals(oldLeft)) {
				String charOnly = newLeft.substring(newLeft.length() - 1, newLeft.length());
				row.append(charOnly);
			} else {
				if (row.length() > 0) {
					row.append(ColorUtil.CC);
				}
				row.append(ColorUtil.CC);
				row.append(newLeft);
			}
			if (newRight.equals(newLeft)) {
				String charOnly = newRight.substring(newRight.length() - 1, newRight.length());
				row.append(charOnly);
			} else {
				row.append(ColorUtil.CC);
				row.append(newRight);
			}
		}
		row.append(ColorUtil.CC);
		String result = postProcessColoredRow(row.toString());
		presenter.addIrcOutputLine(result, index);
	}
	
	/**
	 * Generates a line in hybrid mode and adds it to the presenter, 
	 * wich will recall this method again with an increased y until all
	 * data is converted.
	 * @param index row of pixels in the ImageData to convert
	 */
	public void generateHybridLine(int index) {
		final StringBuilder row = new StringBuilder();
		String oldLeft;
		String newLeft = null;
		String newRight = null;
		for (int x = startX; x < id.getWidth() -1; x = x + 2) {
			final Sample sample = Sample.getInstance(id, x, index, contrast, brightness);
			
			oldLeft = newLeft;	
			@SuppressWarnings("unused") //TODO reimplement foreground enforcement
			final boolean isEnforceBlackFg = false;
			newLeft = cube.getColorChar(colorMap, preset, sample, Sample.Xdir.LEFT);
			newRight = cube.getColorChar(colorMap, preset, sample, Sample.Xdir.RIGHT);
			if (asciiScheme.isCharacterBright(newLeft, preset) && asciiScheme.isCharacterDark(newRight, preset)) {
				newLeft = asciiScheme.replace(newLeft, asciiScheme.selectVline());
			}
			if (asciiScheme.isCharacterDark(newLeft, preset) && asciiScheme.isCharacterBright(newRight, preset)) {
				newRight = asciiScheme.replace(newRight, asciiScheme.selectVline());
			}
			//XXX tune this
			final int downOffset = 21;
			final int upOffset = 13;
			final int genUpDownOffset = 3;
			final int downUpOffset = 1;
			final int upDownOffset = 2;
			final int[] topRgb = sample.getRgbValues(Sample.Ydir.TOP);
			final int[] botRgb = sample.getRgbValues(Sample.Ydir.BOT);
			if (isUp(topRgb, botRgb, upOffset)) {
				if (asciiScheme.isCharacterDark(newRight, preset)) {
					newLeft = newLeft.substring(0, newLeft.length() - 1) + asciiScheme.selectRightUp(); // y7
				} else {
					newLeft = newLeft.substring(0, newLeft.length() - 1) + asciiScheme.selectUp(); // "
				}
			} else if (isDown(topRgb, botRgb, downOffset)) {
				if (asciiScheme.isCharacterDark(newRight, preset)) {
					newLeft = newLeft.substring(0, newLeft.length() - 1) + asciiScheme.selectRightDown(); // j
				} else {
					newLeft = newLeft.substring(0, newLeft.length() - 1) + asciiScheme.selectDown(); // _
				}
			}
			if (isUp(topRgb, botRgb, upOffset)) {
				if (asciiScheme.isCharacterDark(newLeft, preset)) {
					newRight = newRight.substring(0, newRight.length() - 1) + asciiScheme.selectLeftUp(); // F
				} else {
					newRight = newRight.substring(0, newRight.length() - 1) + asciiScheme.selectUp(); // "
				}
			} else if (isDown(topRgb, botRgb, downOffset)) {
				if (asciiScheme.isCharacterDark(newLeft, preset)) {
					newRight = newRight.substring(0, newRight.length() - 1) + asciiScheme.selectLeftDown(); // L
				} else {
					newRight = newRight.substring(0, newRight.length() - 1) + asciiScheme.selectDown(); // _
				}
			}
			final int[] leftTopRgb = sample.getRgbValues(Sample.Ydir.TOP, Sample.Xdir.LEFT);
			final int[] leftBottomRgb = sample.getRgbValues(Sample.Ydir.BOT, Sample.Xdir.LEFT);
			final int[] rightTopRgb = sample.getRgbValues(Sample.Ydir.TOP, Sample.Xdir.RIGHT);
			final int[] rightBottomRgb = sample.getRgbValues(Sample.Ydir.BOT, Sample.Xdir.RIGHT);
			if (isUp(leftTopRgb, leftBottomRgb, genUpDownOffset)
					&& isDown(rightTopRgb, rightBottomRgb, genUpDownOffset)
					&& isDown(leftTopRgb, leftBottomRgb, genUpDownOffset)
					&& isUp(rightTopRgb, rightBottomRgb, genUpDownOffset)) {
				newLeft = newLeft.substring(0, newLeft.length() - 1) + asciiScheme.selectLeft(); // <[(
				newRight = newRight.substring(0, newRight.length() - 1) + asciiScheme.selectRight(); // >])
			} else {
				if (isUp(rightTopRgb, leftBottomRgb, upDownOffset)
						&& isDown(rightTopRgb, rightBottomRgb, upDownOffset)) {
					newLeft = newLeft.substring(0, newLeft.length() - 1) + asciiScheme.selectUpDown().substring(0, 1); // \\"_',
					newRight = newRight.substring(0, newRight.length() - 1) + asciiScheme.selectUpDown().substring(1, 2); // \\"_',
				}
				if (isDown(leftTopRgb, leftBottomRgb, downUpOffset)
						&& isUp(rightTopRgb, rightBottomRgb, downUpOffset)) {
					newLeft = newLeft.substring(0, newLeft.length() - 1) + asciiScheme.selectDownUp().substring(0, 1); // //_".'
					newRight = newRight.substring(0, newRight.length() - 1) + asciiScheme.selectDownUp().substring(1, 2); // //_".'
				}
			}
			if (newLeft.equals(oldLeft)) {
				String charOnly = newLeft.substring(newLeft.length() - 1, newLeft.length());
				row.append(charOnly);
			} else {
				if (row.length() > 0) {
					row.append(ColorUtil.CC);
				}
				row.append(ColorUtil.CC);
				row.append(newLeft);
			}
			if (newRight.equals(newLeft)) {
				String charOnly = newRight.substring(newRight.length() - 1, newRight.length());
				row.append(charOnly);
			} else {
				row.append(ColorUtil.CC);
				row.append(newRight);
			}
		}
		row.append(ColorUtil.CC);
		presenter.addIrcOutputLine(postProcessColoredRow(row.toString()), index);
	}
	
	/**
	 * Generates a line in Pwntari mode and adds it to the presenter, 
	 * which will recall this method again with an increased y until all
	 * data is converted.
	 * @param index row of pixels in the ImageData to convert
	 */
	public void generatePwntariLine(int index) {
		StringBuilder row = new StringBuilder();
		String oldLeft;
		String newLeft = null;
		String newRight = null;
		for (int x = startX; x < id.getWidth() -1; x = x + 2) {
			final Sample sample = Sample.getInstance(id, x, index, contrast, brightness);

			oldLeft = newLeft;
			newLeft = cube.getColorChar(colorMap, preset, sample, Sample.Xdir.LEFT);
			newRight = cube.getColorChar(colorMap, preset, sample, Sample.Xdir.RIGHT);
			newLeft = newLeft.substring(0, newLeft.length() - 1) + asciiScheme.selectDown(); // _
			newRight = newRight.substring(0, newRight.length() - 1) + asciiScheme.selectDown(); // _
			if (newLeft.equals(oldLeft)) {
				final String charOnly = newLeft.substring(newLeft.length() - 1, newLeft.length());
				row.append(charOnly);
			} else {
				if (row.length() > 0) {
					row.append(ColorUtil.CC);
				}
				row.append(ColorUtil.CC);
				row.append(newLeft);
			}
			if (newRight.equals(newLeft)) {
				final String charOnly = newRight.substring(newRight.length() - 1, newRight.length());
				row.append(charOnly);
			} else {
				row.append(ColorUtil.CC);
				row.append(newRight);
			}
		}
		row.append(ColorUtil.CC);
		presenter.addIrcOutputLine(postProcessColoredRow(row.toString()), index);
	}
	
	/**
	 * Generates a line in Plain mode and adds it to the presenter, 
	 * which will recall this method again with an increased y until all
	 * data is converted.
	 * @param index row of pixels in the ImageData to convert
	 */
	public void generatePlainLine(int index) {
		final StringBuilder row = new StringBuilder();
		for (int x = startX; x < id.getWidth() -1; x = x + 2) {
			int topLeft = 0, bottomLeft = 0, topRight = 0, bottomRight = 0;
			topLeft = Sample.keepInRange((int) (getDarkFromImage(id, x, index) * contrast) + brightness);
			bottomLeft = Sample.keepInRange((int) (getDarkFromImage(id, x, index + 1) * contrast) + brightness);
			topRight = Sample.keepInRange((int) (getDarkFromImage(id, x + 1, index) * contrast) + brightness);
			bottomRight = Sample.keepInRange((int) (getDarkFromImage(id, x + 1, index + 1) * contrast) + brightness);

			String charPixel = "";
			// 1st char
			if (topLeft <= 127 && bottomLeft > 127) {
				charPixel = asciiScheme.getUp();
			} else if (topLeft > 127 && bottomLeft <= 127) {
				charPixel = asciiScheme.getDown();
			} else {
				charPixel = asciiScheme.getChar((topLeft + bottomLeft) / 2, preset,	true);
			}
			// 2nd char
			if (topRight <= 127 && bottomRight > 127) {
				charPixel = charPixel + asciiScheme.getUp();
			} else if (topRight > 127 && bottomRight <= 127) {
				charPixel = charPixel + asciiScheme.getDown();
			} else {
				charPixel = charPixel + asciiScheme.getChar((topRight + bottomRight) / 2, preset, true);
			}

			// replace chars
			if (charPixel.equals(asciiScheme.getUp() + asciiScheme.getDown())) {
				charPixel = asciiScheme.getUpDown();
			}
			if (charPixel.equals(asciiScheme.getDown() + asciiScheme.getUp())) {
				charPixel = asciiScheme.getDownUp();
			}
			if (asciiScheme.isCharacterDark(charPixel.substring(0, 1), preset)
					&& charPixel.substring(1, 2).equals(asciiScheme.getDown())) {
				charPixel = charPixel.substring(0, 1) + asciiScheme.selectLeftDown();
			}
			if (charPixel.substring(0, 1).equals(asciiScheme.getDown())
					&& asciiScheme.isCharacterDark(charPixel.substring(1, 2), preset)) {
				charPixel = asciiScheme.selectRightDown() + charPixel.substring(1, 2);
			}
			if (asciiScheme.isCharacterDark(charPixel.substring(0, 1), preset)
					&& charPixel.substring(1, 2).equals(asciiScheme.getUp())) {
				charPixel = charPixel.substring(0, 1) + asciiScheme.selectLeftUp();
			}
			if (charPixel.substring(0, 1).equals(asciiScheme.getUp())
					&& asciiScheme.isCharacterDark(charPixel.substring(1, 2), preset)) {
				charPixel = asciiScheme.selectRightUp() + charPixel.substring(1, 2);
			}
			if (charPixel.equals(asciiScheme.getDarkestCharacter(preset) + " ")) {
				charPixel = asciiScheme.getVline() + " ";
			}
			if (charPixel.equals(" " + asciiScheme.getDarkestCharacter(preset))) {
				charPixel = " " + asciiScheme.getVline();
			}
			row.append(charPixel);
		}
		if (asciiScheme.isPostProcessed()) {
			presenter.addIrcOutputLine(postProcessRow(row.toString()), index);
		} else {
			presenter.addIrcOutputLine(row.toString(), index);
		}
	}
	
	/**
	 * Returns the width of the imageData or 1 less if it isn't even.
	 * This is done because some conversion methods cannot handle uneven numbers.
	 * @return corrected width.
	 */
	private int getEvenWidth() {
		if (id.getWidth() % 2 == 0) { // even
			return id.getWidth();
		} else {
			return id.getWidth() - 1;
		}		
	}
	
	/**
	 * Calculates the average darkness of a pixel.
	 * @param ImageData image with the pixel to calculate
	 * @param int x
	 * @param int y
	 * @return darkness of the selected pixel
	 */
	private static int getDarkFromImage(final ImageData id, int x, int y) {
		final double d = id.getRedAt(x, y) + id.getGreenAt(x, y) + id.getBlueAt(x, y);
		final int dark = Double.valueOf(Math.round(d / 3)).intValue();
		return dark;
	}
	
	/**
	 * Removes empty lines from output.
	 * @param input String array with the input line
	 * @return String array without empty lines
	 */
	public String[] removeEmptyLines(String[] input) {
		int notEmptyCounter = 0;
		for (String line : input) {
			if (line != null && !line.equals("")) {
				notEmptyCounter++;				
			}
		}
		final String[] result = new String[notEmptyCounter];
		int index = 0;
		for (String line : input) {
			if (line != null && !line.equals("")) {
				result[index] = line;
				index++;
			}
		}
		return result;
	}
	
	/**
	 * Prepares a Map with the Colors depending on the selected ColorScheme.
	 * @param scheme the selected ColorScheme
	 * @return prepared Map
	 */
	private Map<String, Integer> prepareColorMap(final ColorScheme scheme) {
		final Map<String, Integer> colorMap = new HashMap<String, Integer>();
		for (IrcColor ic : IrcColor.values()) {
			if (scheme.equals(ColorScheme.Default)) {
				colorMap.put(ic.name(), ic.getDefaultScheme());
			} else if (scheme.equals(ColorScheme.Old)) {
				colorMap.put(ic.name(), ic.getOldScheme());
			} else if (scheme.equals(ColorScheme.Vivid)) {
				colorMap.put(ic.name(), ic.getVividScheme());
			} else if (scheme.equals(ColorScheme.Mono)) {
				colorMap.put(ic.name(), ic.getMonoScheme());
			} else if (scheme.equals(ColorScheme.Lsd)) {
				colorMap.put(ic.name(), ic.getLsdScheme());
			} else if (scheme.equals(ColorScheme.Skin)) {
				colorMap.put(ic.name(), ic.getSkinScheme());
			} else if (scheme.equals(ColorScheme.Bwg)) {
				colorMap.put(ic.name(), ic.getBwgScheme());
			} else if (scheme.equals(ColorScheme.Bw)) {
				colorMap.put(ic.name(), ic.getBwScheme());
			} else {
				colorMap.put(ic.name(), ic.getDefaultScheme());				
			}
		}
		return colorMap;
	}

	/**
	 * Decides if top is darker than bottom.
	 * @param top rgb values of the top pixels
	 * @param bottom rgb values of the bottom pixels
	 * @param offset for calculation
	 * @return true if top is darker than top
	 */
	private boolean isUp(final int[] top, final int[] bottom, final int offset){
		return ((top[0] + top[1] + top[2]) / 3) <= (127 + offset)
		     && ((bottom[0] + bottom[1] + bottom[2]) / 3) > (127 - offset);
	}

	/**
	 * Decides if bottom is darker than bottom.
	 * @param top rgb values of the top pixels
	 * @param bottom rgb values of the bottom pixels
	 * @param offset for calculation
	 * @return true if bottom is darker than top
	 */
	private boolean isDown(final int[] top, final int[] bottom, final int offset){
	     return ((top[0] + top[1] + top[2]) / 3) > (127 - offset)
		     && ((bottom[0] + bottom[1] + bottom[2]) / 3) <= (127 + offset);
	}
		
	/**
	 * Makes colored ASCII output smooth.
	 * @param row to process
	 * @return the processed line
	 */
	//FIXME this method doesn't work the way it was intended.
	private String postProcessColoredRow(final String row) {
		if (row.indexOf(ColorUtil.CC) <= 0) { //no CC, so process
			final StringBuilder result = new StringBuilder();
			result.append(row.substring(0, row.length() - 2));
			result.append(postProcessRow(row.substring(row.length() - 2, row.length())));
			return result.toString();
		} else {
			return row; //can't touch this //TODO throw exception
		}
	}
	
	/**
	 * Makes plain ASCII output smooth
	 * @param row to process
	 * @return the processed line
	 */
	private String postProcessRow(final String row) {
		// 1st procession for the upper part of the characters (true case)
		// 2nd one for the lower parts (false case)
		return postProcessVert(postProcessVert(row, true), false);
	}
	
	/**
	 * Makes plain ASCII output smooth
	 * @param row to process
	 * @param up true if line is " half, false if _ half of ASCII character
	 * @return the post-processed line.
	 */
	private String postProcessVert(final String row, final boolean up) {
		final String replaceBy;
		if (up) { // replace """"""" by "-----"
			replaceBy = asciiScheme.getUp();
		} else { // replace _______ by _-----_
			replaceBy = asciiScheme.getDown();
		}
		
		final String matchMe = replaceBy + replaceBy + "*" + replaceBy;
		final RegExp regex = RegExp.compile(matchMe);
		final MatchResult matcher = regex.exec(row);
		boolean matchFound = regex.test(row);

		final StringBuffer buf = new StringBuffer();
		if (matchFound) {
		    for (int i=0; i < matcher.getGroupCount(); i++) {
		    	String originalStr = matcher.getGroup(i);
		    	if (originalStr != null) {
		    		final StringBuilder line = new StringBuilder();
		    		final String[] strings = row.split(originalStr);
		    		int index = 0;
		    		for (String part : strings) {
		    			line.append(part);
		    			index++;
		    			if (index < strings.length) {
		    				line.append(replaceBy);
		    				for (int ii = 0; ii < originalStr.length() - 2; ii++) {
		    					// -2 because the first and the last letter is replaced
		    					line.append(asciiScheme.getHline());
		    				}
		    				line.append(replaceBy);
		    			}		    			
		    		}
		    		buf.append(line);			    	
		    	}
		    }
		} else {
			buf.append(row);
		}
		return buf.toString();
	}
	
	/**
	 * For modes with a kick-option
	 * the loop-counters are initialized according to the 4 possible kicks.
	 * The image is looped 2 rows and 2 columns at a time giving 4 pixels (compare Sample class)
	 * to examine inside the loop. everything is generated by examining the relations
	 * of the color values between those 4 pixels which is why the kick option even works.
	 * (a good anti-aliasing algorithm on the ASCII level would defeat this purpose)
	 *
	 *	     X
	 *   +------------>
	 * Y | ## ## ##
	 *   | ## ## ##
	 *   |
	 *   | ## ## ##
	 *   | ## ## ##
	 *   v
	 * 
	 * @param kick
	 */
	private void applyKicks(Kick kick) {
		this.startX = getKickedX(kick);
		this.startY = getKickedY(kick);
	}
	
	/**
	 * Decides if x is kicked
	 * @param kick
	 * @return 1 or 0
	 */
	private int getKickedX(Kick kick) {
		if (kick.equals(Kick.X) || kick.equals(Kick.XY)) {
 			return 1; 
		} else {
			return 0;
		}
	}

	/**
	 * Decides if y is kicked
	 * @param kick
	 * @return 1 or 0
	 */
	private int getKickedY(Kick kick) {
		if (kick.equals(Kick.Y) || kick.equals(Kick.XY)) {
 			return 1; 
		} else {
			return 0;
		}
	}
}
