package jenkem.client;

import jenkem.shared.color.ColorUtil;
import jenkem.shared.color.IrcColor;

public class ColorUtilTester extends AbstractReflectionTestCase {
    private final ColorUtil util = new ColorUtil();

    public final void testColorConfig() throws Exception {
        final String whiteResult = util.colorConfig(IrcColor.white.getValue(), "###");
        assertEquals(ColorUtil.CC + 1 + "," + 0 + " ###% " + ColorUtil.CC, whiteResult);
        final String redResult = util.colorConfig(IrcColor.red.getValue(), "###");
        assertEquals(ColorUtil.CC + 0 + "," + 4 + " ###% " + ColorUtil.CC, redResult);
        final String blackResult = util.colorConfig(IrcColor.black.getValue(), "###");
        assertEquals(ColorUtil.CC + 0 + "," + 1 + " ###% " + ColorUtil.CC, blackResult);
    }

    public final void testMakeBold() throws Exception {
        final String[] input = {"X", "Y", "Z"};
        final String[] result = util.makeBold(input);
        final String[] expected = {ColorUtil.BC + "X", ColorUtil.BC + "Y", ColorUtil.BC + "Z"};
        int index = 0;
        for (final String exp : expected) {
            assertEquals(exp, result[index].toString());
            index++;
        }
    }

    public final void testRgbToCss() throws Exception {
        final Integer[] blackParameters = {0, 0, 0};
        final String blackOutput = (String) invokePrivateMethodWithIntegerParameters(
                util, "rgbToCss", blackParameters);
        assertEquals("#000000", blackOutput);
        final Integer[] redParameters = {255, 0, 0};
        final String redOutput = (String) invokePrivateMethodWithIntegerParameters(
                util, "rgbToCss", redParameters);
        assertEquals("#ff0000", redOutput);
        final Integer[] greenParameters = {0, 255, 0};
        final String greenOutput = (String) invokePrivateMethodWithIntegerParameters(
                util, "rgbToCss", greenParameters);
        assertEquals("#00ff00", greenOutput);
        final Integer[] blueParameters = {0, 0, 255};
        final String blueOutput = (String) invokePrivateMethodWithIntegerParameters(
                util, "rgbToCss", blueParameters);
        assertEquals("#0000ff", blueOutput);
        final Integer[] whiteParameters = {255, 255, 255};
        final String whiteOutput = (String) invokePrivateMethodWithIntegerParameters(
                util, "rgbToCss", whiteParameters);
        assertEquals("#ffffff", whiteOutput);
    }

    public final void testIrcToCss() throws Exception {
        assertEquals("#000000", util.ircToCss(IrcColor.black.getValue()));
        assertEquals("#ff0000", util.ircToCss(IrcColor.red.getValue()));
        assertEquals("#00ff00", util.ircToCss(IrcColor.green.getValue()));
        assertEquals("#0000ff", util.ircToCss(IrcColor.blue.getValue()));
        assertEquals("#ffffff", util.ircToCss(IrcColor.white.getValue()));
    }
}
