package jenkem.client.presenter;

//import gwt.g2d.client.graphics.ImageLoader;
//import gwt.g2d.client.graphics.Surface;
//import gwt.g2d.client.graphics.canvas.ImageDataAdapter;

import gwt.g2d.client.graphics.ImageLoader;
import gwt.g2d.client.graphics.Surface;
import gwt.g2d.client.graphics.canvas.ImageDataAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jenkem.client.service.JenkemServiceAsync;
import jenkem.shared.AsciiScheme;
import jenkem.shared.HtmlUtil;
import jenkem.shared.Sample;
import jenkem.shared.SchemeUnknownException;
import jenkem.shared.color.ColorUtil;
import jenkem.shared.color.Cube;
import jenkem.shared.color.IrcColor;
import jenkem.shared.data.JenkemImage;

import com.google.appengine.api.datastore.Text;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.regexp.shared.SplitResult;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class MainPresenter implements Presenter {

	public interface Display {
		HasValue<String> getInputLink();

		TextBox getInputTextBox();

//		Canvas getCanvas();
		Surface getSurface();

		Frame getPreviewFrame();

		HasClickHandlers getShowButton();

		Widget asWidget();
	}

	private final JenkemServiceAsync jenkemService;
	@SuppressWarnings("unused")
	private final HandlerManager eventBus;
	private final Display display;

	public MainPresenter(final JenkemServiceAsync jenkemService,
			final HandlerManager eventBus, final Display view) {
		this.jenkemService = jenkemService;
		this.eventBus = eventBus;
		this.display = view;
	}

	public void bind() {
		this.display.getShowButton().addClickHandler(new ClickHandler() {
			public void onClick(final ClickEvent event) {
				// calls the local image servlet to proxify the provided image
				// in order to circumvent the restrictions put by the same
				// origin policy
				String url = "http://" + Window.Location.getHost()
						+ "/jenkem/image?url="
						+ display.getInputTextBox().getText();
				doShow(url);
			}
		});
	}

	public void go(final HasWidgets container) {
		bind();
		container.clear();
		container.add(display.asWidget());
		display.getInputTextBox().setFocus(true);
	}

	private void doShow(String url) {
		String[] urls = new String[] { url };
		ImageLoader.loadImages(urls, new ImageLoader.CallBack() {
			@Override
			public void onImagesLoaded(ImageElement[] imageElements) {
				ImageElement image = imageElements[0];

				//best for full-HD mode
//				final int WIDTH = 72;
//				final int HEIGHT = (36 * image.getHeight()) / image.getWidth();

				//best for super-hybrid mode
				final int WIDTH = 72;
				final int HEIGHT = (72 * image.getHeight()) / image.getWidth();
				
				
				
				display.getSurface().clear();
				display.getSurface().drawImage(image, 0, 0, WIDTH, HEIGHT);

				final ImageDataAdapter ida = display.getSurface().getImageData(
						0, 0, WIDTH, HEIGHT);

				// TODO
//				String[] ircOutput = generateHighDef(ida);
				String[] ircOutput = generateSuperHybrid(ida);

				ArrayList<Text> irc = new ArrayList<Text>();
				for (String s : ircOutput) {
					irc.add(new Text(s));
				}

				HtmlUtil htmlUtil = new HtmlUtil();
				Date now = new Date();
				String name = Long.valueOf(now.getTime()).toString();
				String[] htmlAndCss = htmlUtil.generateHtml(ircOutput, name,
						HtmlUtil.MODE_FULL_HD);

				JenkemImage jenkemImage = new JenkemImage();
				jenkemImage.setName(name);
				jenkemImage.setCreateDate(now);
				jenkemImage.setIrc(irc);
				jenkemImage.setHtml(htmlAndCss[0]);
				jenkemImage.setCss(htmlAndCss[1]);

				jenkemService.saveJenkemImage(jenkemImage,
						new AsyncCallback<String>() {
							@Override
							public void onFailure(Throwable caught) {
								// TODO Auto-generated method stub
							}

							@Override
							public void onSuccess(String result) {
								display.getPreviewFrame().setUrl(
										"http://" + Window.Location.getHost()
												+ "/jenkem/output?name="
												+ result + "&type=html");
							}
						});
			}
		});
	}

	/**
	 * full-hd mode
	 * 
	 * @param img
	 * @return Strings for IRC.
	 * @throws SchemeUnknownException
	 */
	public String[] generateHighDef(ImageDataAdapter ida) {
		final Cube cube = new Cube();
		Map<String, Integer> colorMap = new HashMap<String, Integer>();
		for (IrcColor ic : IrcColor.values()) {
			colorMap.put(ic.name(), ic.getDefaultScheme());
		}

		String[] ret = new String[ida.getHeight()];
		for (int y = 0; y < ida.getHeight(); y++) {
			ret[y] = "";
			StringBuilder row = new StringBuilder();
			String oldPix; // lets pretend this is FROTRAN :D
			String newPix = null;
			try {
				for (int x = 0; x < ida.getWidth(); x++) {
					final int red = ida.getRed(x, y);
					final int green = ida.getGreen(x, y);
					final int blue = ida.getBlue(x, y);


					oldPix = newPix;
					newPix = cube.getColorChar(colorMap, red, green, blue,
							false); // the cube is used here.
					if (newPix.equals(oldPix)) {
						String charOnly = newPix.substring(newPix.length() - 1,
								newPix.length());
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
				ret[y] = row.toString();
			} catch (StringIndexOutOfBoundsException aioobe) {
				// happens with images that have an uneven width
				// just ignore this
			}
		}
		return ret;
	}


	
	private String[] generateSuperHybrid(ImageDataAdapter ida) {
		final Cube cube = new Cube();

//		ColorMap colors = ColorMap.Default;
		
		Map<String, Integer> colorMap = new HashMap<String, Integer>();
		for (IrcColor ic : IrcColor.values()) {
			colorMap.put(ic.name(), ic.getDefaultScheme());
		}
		
		final AsciiScheme asciiScheme = new AsciiScheme();
			
		int height = Math.round(ida.getHeight() / 2);
		int width = ida.getWidth();
		
//		colorMap = asciiScheme.getColorMap();
		String[] ret = new String[height];

		//     X
		//   +------------>
		// Y | ## ## ##
		//   | ## ## ##
		//   |
		//   | ## ## ##
		//   | ## ## ##
		//   v

		//the loop-counters are initialized according to the 4 possible 'kick' options.
		//the image is looped 2 rows and 2 columns at a time giving 4 pixels
		//to examine inside the loop. everything is generated by examining the relations
		//of the color values between those 4 pixels which is why the kick option even works.
		//(a good anti-aliasing algorithm on the ASCII level would defeat this purpose)

			
		//TODO reimplement
		int startX = 0;
//		if (ctx.getKick().equalsIgnoreCase("x") || ctx.getKick().equalsIgnoreCase("xy")) {
// 			startX++; //kick to the left
//		}
		int startY = 0;
//		if (ctx.getKick().equalsIgnoreCase("y") || ctx.getKick().equalsIgnoreCase("xy")) {
//			startY++; //kick down
//		}

		//loop over the image, 2 pixels at a time
		for (int y = startY; y < height * 2; y = y + 2) {
			try {
				ret[y / 2] = "";
				StringBuilder row = new StringBuilder();
				String oldLeft;
				String newLeft = null;
				String newRight = null;
				for (int x = startX; x < width; x = x + 2) {
					try {
						final double CONTRAST = 0.70;
						Sample sample = new Sample(ida, x, y, CONTRAST);
						
						oldLeft = newLeft;
						newLeft = cube.getColorChar(
							colorMap, sample.getRedLeft(), sample.getGreenLeft(), sample.getBlueLeft()
						);
						newRight = cube.getColorChar(
							colorMap, sample.getRedRight(), sample.getGreenRight(), sample.getBlueRight()
						);

						jenkem.shared.color.Color leftCol = cube.getTwoNearestColors(colorMap, sample.getRedLeft(), sample.getGreenLeft(), sample.getBlueLeft());
						jenkem.shared.color.Color rightCol = cube.getTwoNearestColors(colorMap, sample.getRedRight(), sample.getGreenRight(), sample.getBlueRight());
						jenkem.shared.color.Color leftTopCol = cube.getTwoNearestColors(colorMap, sample.getRedTopLeft(), sample.getGreenTopLeft(), sample.getBlueTopLeft());
						jenkem.shared.color.Color leftBottomCol = cube.getTwoNearestColors(colorMap, sample.getRedBottomLeft(), sample.getGreenBottomLeft(), sample.getBlueBottomLeft());
						jenkem.shared.color.Color rightTopCol = cube.getTwoNearestColors(colorMap, sample.getRedTopRight(), sample.getGreenTopRight(), sample.getBlueTopRight());
						jenkem.shared.color.Color rightBottomCol = cube.getTwoNearestColors(colorMap, sample.getRedBottomRight(), sample.getGreenBottomRight(), sample.getBlueBottomRight());
						//XXX FFFFFFFFUUUUUUUUUUUUUUUUUUUUUUUUUUUU--

						double offset = +32.0D;
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
				ret[y / 2] = postProcessColoredRow(row.toString());
			} catch (StringIndexOutOfBoundsException aioobe) {
				//depending on the kick settings and on the height of the resized image,
				//this happens if the last row is not even.
				//just ignore it and do nothing.
			}
		}
		return ret;
	}
	
	private String postProcessColoredRow(final String row) {
		if (row.indexOf(ColorUtil.CC) <= 0) { //no CC, so process
			StringBuilder result = new StringBuilder();
			result.append(row.substring(0, row.length() - 2));
			result.append(postProcessRow(row.substring(row.length() - 2, row.length())));
			return result.toString();
		} else {
			return row; //can't touch this //TODO throw exception
		}
	}
	
	private String postProcessRow(String row) {
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
	private String postProcessVert(String row, boolean up) {
		final AsciiScheme asciiScheme = new AsciiScheme();
		String replaceBy;
		if (up) { // replace """"""" by "-----"
			replaceBy = asciiScheme.getUp();
		} else { // replace _______ by _-----_
			replaceBy = asciiScheme.getDown();
		}
		
		final String matchMe = replaceBy + replaceBy + "*" + replaceBy;
		
		
		RegExp regex = RegExp.compile(matchMe);
		
		
//		Pattern pattern = Pattern.compile(matchMe);
//		Matcher matcher = pattern.matcher(row);
		
		StringBuilder buf = new StringBuilder();
		
		while (regex.test(row)) {
			//String originalStr = matcher.group();
			SplitResult originalStr = regex.split(row);
			
			StringBuilder line = new StringBuilder();
			for (int i = 0; i < originalStr.length() - 2; i++) {
				// -2 because the first and the last letter is replaced
				line.append(asciiScheme.getHline());
			}
			
//			matcher.appendReplacement(buf, replaceBy + line.toString() + replaceBy);
			
			//buf.append(replaceBy + line.toString() + replaceBy); //FIXME
		}

//		matcher.appendTail(buf);
		
		
//		buf.append(buf); //FIXME

		return buf.toString();
	}
	
}
