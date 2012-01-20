package jenkem.client;

import jenkem.shared.color.ColorUtil;
import jenkem.shared.color.IrcColor;

public class ColorUtilTester extends AbstractReflectionTestCase {
	final ColorUtil util = new ColorUtil();
	
	public void testColorConfig() throws Exception {
		String whiteResult = util.colorConfig(IrcColor.white.getValue(), "###");
		assertEquals(ColorUtil.CC + 1 + "," + 0 + " ###% " + ColorUtil.CC, whiteResult);
		String redResult = util.colorConfig(IrcColor.red.getValue(), "###");
		assertEquals(ColorUtil.CC + 0 + "," + 4 + " ###% " + ColorUtil.CC, redResult);
		String blackResult = util.colorConfig(IrcColor.black.getValue(), "###");
		assertEquals(ColorUtil.CC + 0 + "," + 1 + " ###% " + ColorUtil.CC, blackResult);
	}

	public void testMakeBold() throws Exception {
		String[] input = { "X", "Y", "Z" };
		String[] result = util.makeBold(input);
		String[] expected = { ColorUtil.BC + "X", ColorUtil.BC + "Y", ColorUtil.BC + "Z" };
		int index = 0;
		for (String exp : expected) {
			assertEquals(exp, result[index].toString());
			index++;
		}
	}
	
	public void testRgbToCss() throws Exception {
		final Integer[] blackParameters = { 0, 0, 0 };
		String blackOutput = (String) invokePrivateMethodWithIntegerParameters(util, "rgbToCss", blackParameters);
		assertEquals("#000000", blackOutput);
		final Integer[] redParameters = { 255, 0, 0 };
		String redOutput = (String) invokePrivateMethodWithIntegerParameters(util, "rgbToCss", redParameters);
		assertEquals("#ff0000", redOutput);
		final Integer[] greenParameters = { 0, 255, 0 };
		String greenOutput = (String) invokePrivateMethodWithIntegerParameters(util, "rgbToCss", greenParameters);
		assertEquals("#00ff00", greenOutput);
		final Integer[] blueParameters = { 0, 0, 255 };
		String blueOutput = (String) invokePrivateMethodWithIntegerParameters(util, "rgbToCss", blueParameters);
		assertEquals("#0000ff", blueOutput);
		final Integer[] whiteParameters = { 255, 255, 255 };
		String whiteOutput = (String) invokePrivateMethodWithIntegerParameters(util, "rgbToCss", whiteParameters);
		assertEquals("#ffffff", whiteOutput);
	}
	
	public void testIrcToCss() throws Exception {
		assertEquals("#000000", util.ircToCss(IrcColor.black.getValue()));		
		assertEquals("#ff0000", util.ircToCss(IrcColor.red.getValue()));		
		assertEquals("#00ff00", util.ircToCss(IrcColor.green.getValue()));		
		assertEquals("#0000ff", util.ircToCss(IrcColor.blue.getValue()));		
		assertEquals("#ffffff", util.ircToCss(IrcColor.white.getValue()));		
	}
}
