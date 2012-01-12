package jenkem.shared.color;

import gwt.g2d.client.graphics.canvas.ImageDataAdapter;

/**
 * Represents the RGBcolors of four pixels from the provided image.
 * TL | TR
 * ---+---
 * BL | BR
 */
public class Sample {
	private int redTopLeft;
	private int greenTopLeft;
	private int blueTopLeft;
	private int redBottomLeft;
	private int greenBottomLeft;
	private int blueBottomLeft;
	private int redTopRight;
	private int greenTopRight;
	private int blueTopRight;
	private int redBottomRight;
	private int greenBottomRight;
	private int blueBottomRight;
	private int redLeft;
	private int greenLeft;
	private int blueLeft;
	private int redRight;
	private int greenRight;
	private int blueRight;

	public Sample(final ImageDataAdapter img, final int x, final int y, final double contrast, final int brightness) {
		redTopLeft = keepInRange((int) (img.getRed(x, y) * contrast) + brightness);
		greenTopLeft = keepInRange((int) (img.getGreen(x, y) * contrast) + brightness);
		blueTopLeft = keepInRange((int) (img.getBlue(x, y) * contrast) + brightness);
		try {
			if (x < img.getWidth()) {
				redTopRight = keepInRange((int) (img.getRed(x + 1, y) * contrast) + brightness);
				greenTopRight = keepInRange((int) (img.getGreen(x + 1, y) * contrast) + brightness);
				blueTopRight = keepInRange((int) (img.getBlue(x + 1, y) * contrast) + brightness);
			} else {
				redBottomLeft = redTopLeft;
				greenBottomLeft = greenTopLeft;
				blueBottomLeft = blueTopLeft;
			}
			if (y < img.getHeight()) {
				redBottomLeft = keepInRange((int) (img.getRed(x, y + 1) * contrast) + brightness);
				greenBottomLeft = keepInRange((int) (img.getGreen(x, y + 1) * contrast) + brightness);
				blueBottomLeft = keepInRange((int) (img.getBlue(x, y + 1) * contrast) + brightness);
			} else {
				redBottomLeft = redTopLeft;
				greenBottomLeft = greenTopLeft;
				blueBottomLeft = blueTopLeft;
			}
			if (x < img.getWidth() && y < img.getHeight()) {
				redBottomRight = keepInRange((int) (img.getRed(x + 1, y + 1) * contrast) + brightness);
				greenBottomRight = keepInRange((int) (img.getGreen(x + 1, y + 1) * contrast) + brightness);
				blueBottomRight = keepInRange((int) (img.getBlue(x + 1, y + 1) * contrast) + brightness);
			} else {
				redBottomLeft = redTopLeft;
				greenBottomLeft = greenTopLeft;
				blueBottomLeft = blueTopLeft;
			}
		} catch (Exception e) {
			//just use Top left on all fields if eny Exception is thrown
			//may only happen if kick is not "0"
			//TODO replace ugly trial and error approach.
			redBottomLeft = redTopLeft;
			greenBottomLeft = greenTopLeft;
			blueBottomLeft = blueTopLeft;
			redBottomLeft = redTopLeft;
			greenBottomLeft = greenTopLeft;
			blueBottomLeft = blueTopLeft;
			redBottomLeft = redTopLeft;
			greenBottomLeft = greenTopLeft;
			blueBottomLeft = blueTopLeft;
		}
			
		redLeft = redTopLeft + redBottomLeft / 2;
		greenLeft = greenTopLeft + greenBottomLeft / 2;
		blueLeft = blueTopLeft + blueBottomLeft / 2;
		redRight = redTopRight + redBottomRight / 2;
		greenRight = greenTopRight + greenBottomRight / 2;
		blueRight = blueTopRight + blueBottomRight / 2;
	}

	public int getRedTopLeft() {
		return redTopLeft;
	}

	public int getGreenTopLeft() {
		return greenTopLeft;
	}

	public int getBlueTopLeft() {
		return blueTopLeft;
	}

	public int getRedBottomLeft() {
		return redBottomLeft;
	}

	public int getGreenBottomLeft() {
		return greenBottomLeft;
	}

	public int getBlueBottomLeft() {
		return blueBottomLeft;
	}

	public int getRedTopRight() {
		return redTopRight;
	}

	public int getGreenTopRight() {
		return greenTopRight;
	}

	public int getBlueTopRight() {
		return blueTopRight;
	}

	public int getRedBottomRight() {
		return redBottomRight;
	}

	public int getGreenBottomRight() {
		return greenBottomRight;
	}

	public int getBlueBottomRight() {
		return blueBottomRight;
	}

	public int getRedLeft() {
		return redLeft;
	}

	public int getGreenLeft() {
		return greenLeft;
	}

	public int getBlueLeft() {
		return blueLeft;
	}

	public int getRedRight() {
		return redRight;
	}

	public int getGreenRight() {
		return greenRight;
	}

	public int getBlueRight() {
		return blueRight;
	}
	
	public static int keepInRange(final int color) {
		if (color > 255) {
			return 255;
		} else if (color < 0) {
			return 0;
		} else {
			return color;
		}
	}
}
