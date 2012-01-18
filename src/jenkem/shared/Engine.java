package jenkem.shared;

import java.util.HashMap;
import java.util.Map;

import jenkem.client.presenter.MainPresenter;
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
	
	public Engine(MainPresenter presenter) {
		this.presenter = presenter;
	}
	
	/**
	 * Full-HD mode
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
	 * Super-Hybrid mode
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
	 * Hybrid mode
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
	 * Pwntari mode
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
	 * Plain mode
	 */
	public void generatePlain(final ImageData id, final CharacterSet preset, final double contrast, final int brightness, final Kick kick) {
		this.id = id;
		this.preset = preset;
		this.contrast = contrast;
		this.brightness = brightness;
		applyKicks(kick);
		generatePlainLine(0 + startY); //start by triggering the conversion of the 1st line
	}
	
	public void generateHighDefLine(final int index) {
		String result = "";
		final StringBuilder row = new StringBuilder();
		String oldPix; // lets pretend this is FROTRAN :D
		String newPix = null;
		try {
			for (int x = 0; x < id.getWidth(); x++) {
				final int red = Sample.keepInRange((int) (id.getRedAt(x, index) * contrast) + brightness);
				final int green = Sample.keepInRange((int) (id.getGreenAt(x, index) * contrast) + brightness);
				final int blue = Sample.keepInRange((int) (id.getBlueAt(x, index) * contrast) + brightness);
				oldPix = newPix;
				newPix = cube.getColorChar(colorMap, preset, red, green, blue, false); // the cube is used here.
				if (newPix.equals(oldPix)) {
					String charOnly = newPix.substring(newPix.length() - 1, newPix.length());
					row.append(charOnly);
				} else {
					if (row.length() > 0) {
						row.append(ColorUtil.CC);
					}
					row.append(ColorUtil.CC);
					row.append(newPix);
				}
			}
			row.append(ColorUtil.CC);
			result = row.toString();
		} catch (StringIndexOutOfBoundsException aioobe) {
			// happens with images that have an uneven width
			// just ignore this
		}
		presenter.addIrcOutputLine(result, index);
	}
	
	public void generateSuperHybridLine(final int index) {
		String result = "";
		if (isLineOmitted(index)) {
			presenter.addIrcOutputLine("", index);
			return;
		}
		try {
			result = "";
			final StringBuilder row = new StringBuilder();
			String oldLeft;
			String newLeft = null;
			String newRight = null;
			for (int x = startX; x < id.getWidth() -1; x = x + 2) {
				try {
					final Sample sample = new Sample(id, x, index, contrast, brightness);
					
					oldLeft = newLeft;
					newLeft = cube.getColorChar(
						colorMap, preset, sample.getRedLeft(), sample.getGreenLeft(), sample.getBlueLeft()
					);
					newRight = cube.getColorChar(
						colorMap, preset, sample.getRedRight(), sample.getGreenRight(), sample.getBlueRight()
					);

					final jenkem.shared.color.Color leftCol = cube.getTwoNearestColors(colorMap, sample.getRedLeft(), sample.getGreenLeft(), sample.getBlueLeft());
					final jenkem.shared.color.Color rightCol = cube.getTwoNearestColors(colorMap, sample.getRedRight(), sample.getGreenRight(), sample.getBlueRight());
					final jenkem.shared.color.Color leftTopCol = cube.getTwoNearestColors(colorMap, sample.getRedTopLeft(), sample.getGreenTopLeft(), sample.getBlueTopLeft());
					final jenkem.shared.color.Color leftBottomCol = cube.getTwoNearestColors(colorMap, sample.getRedBottomLeft(), sample.getGreenBottomLeft(), sample.getBlueBottomLeft());
					final jenkem.shared.color.Color rightTopCol = cube.getTwoNearestColors(colorMap, sample.getRedTopRight(), sample.getGreenTopRight(), sample.getBlueTopRight());
					final jenkem.shared.color.Color rightBottomCol = cube.getTwoNearestColors(colorMap, sample.getRedBottomRight(), sample.getGreenBottomRight(), sample.getBlueBottomRight());
					//XXX FFFFFFFFUUUUUUUUUUUUUUUUUUUUUUUUUUUU--

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

				} catch (ArrayIndexOutOfBoundsException aioobe) {
					//depending on the kick settings and the width settings,
					//this happens if the last column of pixels in the resized image is not even.
					//just ignore it and do nothing.
				}
			}
			row.append(ColorUtil.CC);
			result = postProcessColoredRow(row.toString());
		} catch (StringIndexOutOfBoundsException aioobe) {
			//depending on the kick settings and on the height of the resized image,
			//this happens if the last row is not even.
			//just ignore it and do nothing.
		}
		presenter.addIrcOutputLine(result, index);
	}
		
	public void generateHybridLine(int index) {
		String result = "";
		if (isLineOmitted(index)) {
			presenter.addIrcOutputLine("", index);
			return;
		}
		try {
			final StringBuilder row = new StringBuilder();
			String oldLeft;
			String newLeft = null;
			String newRight = null;
			for (int x = startX; x < id.getWidth() -1; x = x + 2) {
				try {					
					//FIXME really y/2 ?
					final Sample sample = new Sample(id, x, index, contrast, brightness);
					
					oldLeft = newLeft;
					//TODO reimplement foreground enforcement
					final boolean isEnforceBlackFg = false;
					newLeft = cube.getColorChar(
						colorMap, preset, sample.getRedLeft(), sample.getGreenLeft(), sample.getBlueLeft(), isEnforceBlackFg
					);
					newRight = cube.getColorChar(
						colorMap, preset, sample.getRedRight(), sample.getGreenRight(), sample.getBlueRight(), isEnforceBlackFg
					);

					if (asciiScheme.isCharacterBright(newLeft, preset) && asciiScheme.isCharacterDark(newRight, preset)) {
						newLeft = asciiScheme.replace(newLeft, asciiScheme.selectVline());
					}
					if (asciiScheme.isCharacterDark(newLeft, preset) && asciiScheme.isCharacterBright(newRight, preset)) {
						newRight = asciiScheme.replace(newRight, asciiScheme.selectVline());
					}

					//TODO this is all ugly
					//XXX tune this
					final int downOffset = 21;
					final int upOffset = 13;

					final int genUpDownOffset = 3;
					final int downUpOffset = 1;
					final int upDownOffset = 2;

					if (isUp(sample.getRedTopLeft(), sample.getGreenTopLeft(), sample.getBlueTopLeft(), sample.getRedBottomLeft(), sample.getGreenBottomLeft(), sample.getBlueBottomLeft(), upOffset)) {
						if (asciiScheme.isCharacterDark(newRight, preset)) {
							newLeft = newLeft.substring(0, newLeft.length() - 1) + asciiScheme.selectRightUp(); // y7
						} else {
							newLeft = newLeft.substring(0, newLeft.length() - 1) + asciiScheme.selectUp(); // "
						}
					} else if (isDown(sample.getRedTopLeft(), sample.getGreenTopLeft(), sample.getBlueTopLeft(), sample.getRedBottomLeft(), sample.getGreenBottomLeft(), sample.getBlueBottomLeft(), downOffset)) {
						if (asciiScheme.isCharacterDark(newRight, preset)) {
							newLeft = newLeft.substring(0, newLeft.length() - 1) + asciiScheme.selectRightDown(); // j
						} else {
							newLeft = newLeft.substring(0, newLeft.length() - 1) + asciiScheme.selectDown(); // _
						}
					}
					if (isUp(sample.getRedTopRight(), sample.getGreenTopRight(), sample.getBlueTopRight(), sample.getRedBottomRight(), sample.getGreenBottomRight(), sample.getBlueBottomRight(), upOffset)) {
						if (asciiScheme.isCharacterDark(newLeft, preset)) {
							newRight = newRight.substring(0, newRight.length() - 1) + asciiScheme.selectLeftUp(); // F
						} else {
							newRight = newRight.substring(0, newRight.length() - 1) + asciiScheme.selectUp(); // "
						}
					} else if (isDown(sample.getRedTopRight(), sample.getGreenTopRight(), sample.getBlueTopRight(), sample.getRedBottomRight(), sample.getGreenBottomRight(), sample.getBlueBottomRight(), downOffset)) {
						if (asciiScheme.isCharacterDark(newLeft, preset)) {
							newRight = newRight.substring(0, newRight.length() - 1) + asciiScheme.selectLeftDown(); // L
						} else {
							newRight = newRight.substring(0, newRight.length() - 1) + asciiScheme.selectDown(); // _
						}
					}

					if (isUp(sample.getRedTopLeft(), sample.getGreenTopLeft(), sample.getBlueTopLeft(), sample.getRedBottomLeft(), sample.getGreenBottomLeft(), sample.getBlueBottomLeft(), genUpDownOffset)
							&& isDown(sample.getRedTopRight(), sample.getGreenTopRight(), sample.getBlueTopRight(), sample.getRedBottomRight(), sample.getGreenBottomRight(), sample.getBlueBottomRight(), genUpDownOffset)
							&& isDown(sample.getRedTopLeft(), sample.getGreenTopLeft(), sample.getBlueTopLeft(), sample.getRedBottomLeft(), sample.getGreenBottomLeft(), sample.getBlueBottomLeft(), genUpDownOffset)
							&& isUp(sample.getRedTopRight(), sample.getGreenTopRight(), sample.getBlueTopRight(), sample.getRedBottomRight(), sample.getGreenBottomRight(), sample.getBlueBottomRight(), genUpDownOffset)) {
						newLeft = newLeft.substring(0, newLeft.length() - 1) + asciiScheme.selectLeft(); // <[(
						newRight = newRight.substring(0, newRight.length() - 1) + asciiScheme.selectRight(); // >])
					} else {
						if (isUp(sample.getRedTopLeft(), sample.getGreenTopLeft(), sample.getBlueTopLeft(), sample.getRedBottomLeft(), sample.getGreenBottomLeft(), sample.getBlueBottomLeft(), upDownOffset)
								&& isDown(sample.getRedTopRight(), sample.getGreenTopRight(), sample.getBlueTopRight(), sample.getRedBottomRight(), sample.getGreenBottomRight(), sample.getBlueBottomRight(), upDownOffset)) {
							newLeft = newLeft.substring(0, newLeft.length() - 1) + asciiScheme.selectUpDown().substring(0, 1); // \\"_',
							newRight = newRight.substring(0, newRight.length() - 1) + asciiScheme.selectUpDown().substring(1, 2); // \\"_',
						}
						if (isDown(sample.getRedTopLeft(), sample.getGreenTopLeft(), sample.getBlueTopLeft(), sample.getRedBottomLeft(), sample.getGreenBottomLeft(), sample.getBlueBottomLeft(), downUpOffset)
								&& isUp(sample.getRedTopRight(), sample.getGreenTopRight(), sample.getBlueTopRight(), sample.getRedBottomRight(), sample.getGreenBottomRight(), sample.getBlueBottomRight(), downUpOffset)) {
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

				} catch (ArrayIndexOutOfBoundsException aioobe) {
					//depending on the kick settings and the width settings,
					//this happens if the last column of pixels in the resized image is not even.
					//just ignore it and do nothing.
				}
			}
			row.append(ColorUtil.CC);
			result = postProcessColoredRow(row.toString());
		} catch (StringIndexOutOfBoundsException sioobe) {
			//depending on the kick settings and the width settings,
			//this happens if the last column of pixels in the resized image is not even.
			//just ignore it and do nothing.
		}
		presenter.addIrcOutputLine(result, index);
	}
	
	public void generatePwntariLine(int index) {
		String result = "";
		if (isLineOmitted(index)) {
			presenter.addIrcOutputLine("", index);
			return;
		}
		try {
			StringBuilder row = new StringBuilder();
			String oldLeft;
			String newLeft = null;
			String newRight = null;
			for (int x = startX; x < id.getWidth() -1; x = x + 2) {
				try {
					final Sample sample = new Sample(id, x, index, contrast, brightness);

					oldLeft = newLeft;
					newLeft = cube.getColorChar(
						colorMap, preset, sample.getRedLeft(), sample.getGreenLeft(), sample.getBlueLeft()
					);
					newRight = cube.getColorChar(
						colorMap, preset, sample.getRedRight(), sample.getGreenRight(), sample.getBlueRight()
					);
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
				} catch (ArrayIndexOutOfBoundsException aioobe) {
					//depending on the kick settings and the width settings,
					//this happens if the last column of pixels in the resized image is not even.
					//just ignore it and do nothing.
				}
			}
			row.append(ColorUtil.CC);
			result = postProcessColoredRow(row.toString());
		} catch (StringIndexOutOfBoundsException aioobe) {
			//depending on the kick settings and the width settings,
			//this happens if the last column of pixels in the resized image is not even.
			//just ignore it and do nothing.
		}
		presenter.addIrcOutputLine(result, index);
	}
		
	public void generatePlainLine(int index) {
		String result = "";
		if (isLineOmitted(index)) {
			presenter.addIrcOutputLine("", index);
			return;
		}
		try {
			final StringBuilder row = new StringBuilder();
			for (int x = startX; x < id.getWidth() -1; x = x + 2) {
				// TODO ugly
				int topLeft = 0, bottomLeft = 0, topRight = 0, bottomRight = 0;
				try {
					topLeft = Sample.keepInRange((int) (getDarkFromImage(id, x, index) * contrast) + brightness);
					bottomLeft = Sample.keepInRange((int) (getDarkFromImage(id, x, index + 1) * contrast) + brightness);
					topRight = Sample.keepInRange((int) (getDarkFromImage(id, x + 1, index) * contrast) + brightness);
					bottomRight = Sample.keepInRange((int) (getDarkFromImage(id, x + 1, index + 1) * contrast) + brightness);
				} catch (ArrayIndexOutOfBoundsException aioobe) {
					//depending on the kick settings and the width settings,
					//this happens if the last column of pixels in the resized image is not even.
					//just ignore it and do nothing.
				}
				String charPixel = "";
				// 1st char
				if (topLeft <= 127 && bottomLeft > 127) {
					charPixel = asciiScheme.getUp();
				} else if (topLeft > 127 && bottomLeft <= 127) {
					charPixel = asciiScheme.getDown();
				} else {
					charPixel = asciiScheme.getChar((topLeft + bottomLeft) / 2, preset,	true);
				} // TODO make this shit clean

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
				result = result + postProcessRow(row.toString());
			} else {
				result = result + row.toString();
			}
		} catch (StringIndexOutOfBoundsException aioobe) {
			//depending on the kick settings and the width settings,
			//this happens if the last column of pixels in the resized image is not even.
			//just ignore it and do nothing.
		}
		presenter.addIrcOutputLine(result, index);
	}
	
	private boolean isLineOmitted(final int index) {
		//omit last line if Y not kicked
		if (startY == 0 && (index == id.getHeight() || index == id.getHeight() - 1)) {
			return true; //omit last two lines
		} else if (startY == 1 && index == id.getHeight()) {
			return true; //omit last line
		} else {
			return false;
		}
	}
	
	/**
	 * @param ImageData
	 * @param int x
	 * @param int y
	 * @return darkness
	 */
	private static int getDarkFromImage(final ImageData id, int x, int y) {
		final double d = id.getRedAt(x, y) + id.getGreenAt(x, y) + id.getBlueAt(x, y);
		final int dark = Double.valueOf(Math.round(d / 3)).intValue();
		return dark;
	}
	
	/**
	 * Removes empty lines from output.
	 * @param String[] input
	 * @return String[] without empty lines
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
	 * Prepares a Map with the Colors depending on the selected ColorScheme
	 * @param scheme
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
	
	private boolean isUp(final int topRed, final int topGreen, final int topBlue, 
			final int bottomRed, final int bottomGreen, final int bottomBlue, final int offset){
		return ((topRed + topGreen + topBlue) / 3) <= (127 + offset)
		     && ((bottomRed + bottomGreen + bottomBlue) / 3) > (127 - offset);
	}
	
	private boolean isDown(final int topRed, final int topGreen, final int topBlue, 
			final int bottomRed, final int bottomGreen, final int bottomBlue, final int offset){
	     return ((topRed + topGreen + topBlue) / 3) > (127 - offset)
		     && ((bottomRed + bottomGreen + bottomBlue) / 3) <= (127 + offset);
	}
	
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
	
	private String postProcessRow(final String row) {
		// 1st procession for the upper part of the characters (true case)
		// 2nd one for the lower parts (false case)
		return postProcessVert(postProcessVert(row, true), false);
	}
	
	/**
	 * makes plain ASCII output smooth
	 * @param row
	 * @param up
	 * true if line is " half, false if _ half of ASCII character
	 * @return the post processed line.
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
	
	private int getKickedX(Kick kick) {
		if (kick.equals(Kick.X) || kick.equals(Kick.XY)) {
 			return 1; 
		} else {
			return 0;
		}
	}

	private int getKickedY(Kick kick) {
		if (kick.equals(Kick.Y) || kick.equals(Kick.XY)) {
 			return 1; 
		} else {
			return 0;
		}
	}
	
}
