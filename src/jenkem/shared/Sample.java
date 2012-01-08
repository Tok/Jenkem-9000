package jenkem.shared;

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

	public Sample(ImageDataAdapter img, int x, int y, double contrast) {
		redTopLeft = (int) (img.getRed(x, y) * contrast);
		greenTopLeft = (int) (img.getGreen(x, y) * contrast);
		blueTopLeft = (int) (img.getBlue(x, y) * contrast);
		redBottomLeft = (int) (img.getRed(x, y + 1) * contrast);
		greenBottomLeft = (int) (img.getGreen(x, y + 1) * contrast);
		blueBottomLeft = (int) (img.getBlue(x, y + 1) * contrast);
		redTopRight = (int) (img.getRed(x + 1, y) * contrast);
		greenTopRight = (int) (img.getGreen(x + 1, y) * contrast);
		blueTopRight = (int) (img.getBlue(x + 1, y) * contrast);
		redBottomRight = (int) (img.getRed(x + 1, y + 1) * contrast);
		greenBottomRight = (int) (img.getGreen(x + 1, y + 1) * contrast);
		blueBottomRight = (int) (img.getBlue(x + 1, y + 1) * contrast);
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

}
