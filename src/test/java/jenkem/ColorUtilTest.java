package jenkem;

import jenkem.shared.AsciiScheme;
import jenkem.shared.CharacterSet;
import jenkem.shared.ProcessionSettings;
import jenkem.shared.color.ColorUtil;
import jenkem.shared.color.IrcColor;

public class ColorUtilTest extends AbstractReflectionTestCase {
    private final ProcessionSettings settings = new ProcessionSettings(32, true, true, true, true);
    private final CharacterSet preset = CharacterSet.Hard;
    private final ColorUtil util = new ColorUtil();
    private final AsciiScheme scheme = new AsciiScheme();
    private final String up = scheme.getUp();
    private final String down = scheme.getDown();
    private final String hLine = scheme.getHline();

    public final void testUpPostProcession() throws Exception {
        final String upInput = "##" + up + up + up + up + "##";
        final Object[] upParameters = {upInput, true};
        final String upOutput = (String) invokePrivateMethod(util, "postProcessHor", upParameters);
        assertEquals("##" + up + hLine + hLine + up + "##", upOutput);
    }

    public final void testDownPostProcession() throws Exception {
        final String downInput = "##" + down + down + down + down + "##";
        final Object[] downParameters = {downInput, false};
        final String downOutput = (String) invokePrivateMethod(util, "postProcessHor", downParameters);
        assertEquals("##" + down + hLine + hLine + down + "##", downOutput);
    }

    public final void testPostProcession() throws Exception {
        final String input = "##" + down + down + down + down + "##" + up + up + up + up + "##";
        final Object[] parameters = {input, preset.getCharacters(), settings};
        final String output = (String) invokePrivateMethod(util, "postProcessRow", parameters);
        assertEquals("##L" + down + down + "J##F" + up + up + "q##", output);
    }

    public final void testMixedPostProcession() throws Exception {
        final String input = "##" + down + down + down + "##" + up + up + up + "##";
        final Object[] parameters = {input, preset.getCharacters(), settings};
        final String output = (String) invokePrivateMethod(util, "postProcessRow", parameters);
        assertEquals("##L" + down + "J##F" + up + "q##", output);
    }

    public final void testMoreMixedPostProcession() throws Exception {
        final String input = "##" + down + down + down + down + down + "##" + up + up + up + up + up + "##";
        final Object[] parameters = {input, preset.getCharacters(), settings};
        final String output = (String) invokePrivateMethod(util, "postProcessRow", parameters);
        assertEquals("##L" + down + hLine + down + "J##F" + up + hLine + up + "q##", output);
    }

    public final void testMinPostProcession() throws Exception {
        final String input = "##" + down + down + "##" + up + up + "##";
        final Object[] parameters = {input, preset.getCharacters(), settings};
        final String output = (String) invokePrivateMethod(util, "postProcessRow", parameters);
        //assertEquals(input, output); //no change TODO rethink this
        assertEquals("##LJ##Fq##", output);
    }

    public final void testPostProcessRowWithColor() throws Exception {
        final String input = ColorUtil.CC + "##" + down + down + down + down + "##" + up + up + up + up + "##";
        final String expect = ColorUtil.CC + "##L" + down + down + "J##F" + up + up + "q##";
        final Object[] parameters = {input, preset.getCharacters(), settings};
        final String output = (String) invokePrivateMethod(util, "postProcessRow", parameters);
        assertEquals(expect, output);
    }

    public final void testPostProcessRowWithhoutColor() throws Exception {
        final String input = "##" + down + down + down + down + "##" + up + up + up + up + "##";
        final String expect = "##L" + down + down + "J##F" + up + up + "q##";
        final Object[] parameters = {input, preset.getCharacters(), settings};
        final String output = (String) invokePrivateMethod(util, "postProcessRow", parameters);
        assertEquals(expect, output);
    }

    public final void testColorPostProcessionWithColorRedundancies() throws Exception {
        final String input = ColorUtil.CC + "1,1##" + ColorUtil.CC + "1,1XX" + ColorUtil.CC + "11,11xx" + ColorUtil.CC + "1,1@@";
        final String expect = ColorUtil.CC + "1,1##XX" + ColorUtil.CC + "11,11xx" + ColorUtil.CC + "1,1@@";
        final Object[] parameters = {input, preset.getCharacters(), settings};
        final String output = (String) invokePrivateMethod(util, "postProcessColoredRow", parameters);
        assertEquals(expect, output);
    }

    public final void testColorPostProcessionWithoutColor() throws Exception {
        final String input = ColorUtil.CC + "1,1##" + down + ColorUtil.CC + "2,2"
                + down + down + down + "##" + up + up + ColorUtil.CC + "3,3" + up + up + "##";
        final String expect = ColorUtil.CC + "1,1##L" + ColorUtil.CC + "2,2"
                + down + down + "J##F" + up + ColorUtil.CC + "3,3" + up + "q##";
        final Object[] parameters = {input, preset.getCharacters(), settings};
        final String output = (String) invokePrivateMethod(util, "postProcessColoredRow", parameters);
        assertEquals(expect, output);
    }

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
        /* prepare css entries:
        for (IrcColor ic : IrcColor.values()) {
            System.out.println("." + ic + " {");
            System.out.println("    color: " + util.ircToCss(ic.getValue()));
            System.out.println("}");
            System.out.println("");
        } */
    }

    public final void testIsColorInfo() throws Exception {
        assertTrue(ColorUtil.isColorInfo(ColorUtil.CC + "1,1"));
        assertTrue(ColorUtil.isColorInfo(ColorUtil.CC + "11,0"));
        assertTrue(ColorUtil.isColorInfo(ColorUtil.CC + "1,11"));
        assertTrue(ColorUtil.isColorInfo(ColorUtil.CC + "11,11"));
        assertFalse(ColorUtil.isColorInfo(""));
        assertFalse(ColorUtil.isColorInfo(","));
        assertFalse(ColorUtil.isColorInfo("#"));
        assertFalse(ColorUtil.isColorInfo("1"));
        assertFalse(ColorUtil.isColorInfo("1,1"));
        assertFalse(ColorUtil.isColorInfo("1,11"));
        assertFalse(ColorUtil.isColorInfo("11,1"));
        assertFalse(ColorUtil.isColorInfo("11,11"));
    }

    public final void testIsLegalColorBlock() throws Exception {
        assertTrue(ColorUtil.isLegalColorBlock(ColorUtil.CC + "1,1##"));
        assertTrue(ColorUtil.isLegalColorBlock(ColorUtil.CC + "1,1##"));
        assertTrue(ColorUtil.isLegalColorBlock(ColorUtil.CC + "11,0##"));
        assertTrue(ColorUtil.isLegalColorBlock(ColorUtil.CC + "1,11##"));
        assertTrue(ColorUtil.isLegalColorBlock(ColorUtil.CC + "11,11##"));
        assertFalse(ColorUtil.isLegalColorBlock(ColorUtil.CC));
        assertFalse(ColorUtil.isLegalColorBlock(""));
        assertFalse(ColorUtil.isLegalColorBlock(","));
        assertFalse(ColorUtil.isLegalColorBlock("#"));
        assertFalse(ColorUtil.isLegalColorBlock("1##"));
        assertFalse(ColorUtil.isLegalColorBlock("1,1##"));
        assertFalse(ColorUtil.isLegalColorBlock("1,11##"));
        assertFalse(ColorUtil.isLegalColorBlock("11,1##"));
        assertFalse(ColorUtil.isLegalColorBlock("11,11##"));
    }

    public final void testRemoveConsecutiveCcs() throws Exception {
        final String input = ColorUtil.CC + "2,1  " + ColorUtil.CC + "1,2#" + ColorUtil.CC + "2,1 " + ColorUtil.CC + ColorUtil.CC + "  ";
        final String expected = ColorUtil.CC + "2,1  " + ColorUtil.CC + "1,2#" + ColorUtil.CC + "2,1   ";
        final String result = ColorUtil.removeInvalidCc(input);
        assertEquals(expected, result);
    }

    public final void testRemoveInvalidCcs() throws Exception {
        final String input = ColorUtil.CC + "2,1  " + ColorUtil.CC + "#" + ColorUtil.CC + "2,1 " + ColorUtil.CC + ColorUtil.CC + "  ";
        final String expected = ColorUtil.CC + "2,1  #" + ColorUtil.CC + "2,1   ";
        final String result = ColorUtil.removeInvalidCc(input);
        assertEquals(expected, result);
    }

    public final void testReturnBlockInfo() throws Exception {
        assertEquals(ColorUtil.CC + "1,1", ColorUtil.returnBlockInfo(ColorUtil.CC + "1,1xx"));
        assertEquals(ColorUtil.CC + "11,1", ColorUtil.returnBlockInfo(ColorUtil.CC + "11,1xx"));
        assertEquals(ColorUtil.CC + "1,11", ColorUtil.returnBlockInfo(ColorUtil.CC + "1,11xx"));
        assertEquals(ColorUtil.CC + "11,11", ColorUtil.returnBlockInfo(ColorUtil.CC + "11,11xx"));
        assertEquals("1,1", ColorUtil.returnBlockInfo("1,1xx"));
        assertEquals("11,1", ColorUtil.returnBlockInfo("11,1xx"));
        assertEquals("1,11", ColorUtil.returnBlockInfo("1,11xx"));
        assertEquals("11,11", ColorUtil.returnBlockInfo("11,11xx"));
        assertEquals(ColorUtil.CC, ColorUtil.returnBlockInfo(ColorUtil.CC));
        assertEquals("", ColorUtil.returnBlockInfo(""));
    }

    public final void testRemoveDoubleCcs() throws Exception {
        final String input = ColorUtil.CC + "2,1XX" + ColorUtil.CC + "2,1xx" + ColorUtil.CC + "3,3xx" + ColorUtil.CC + "2,1xx";
        final String expected = ColorUtil.CC + "2,1XXxx" + ColorUtil.CC + "3,3xx" + ColorUtil.CC + "2,1xx";
        final String result = ColorUtil.removeDoubleCc(input);
        assertEquals(expected, result);
    }

    public final void testSplitIntoColorBlocks() throws Exception {
        final String input = ColorUtil.CC + "2,1  " + ColorUtil.CC + "1,2#" + ColorUtil.CC + "2,1 " + ColorUtil.CC + ColorUtil.CC + "  ";
        final String expected = ColorUtil.CC + "2,1  " + ColorUtil.CC + "1,2#" + ColorUtil.CC + "2,1   ";
        final String result = ColorUtil.makeBlocksValid(input);
        assertEquals(expected, result);
    }

    public final void testPostReplacementsVline() throws Exception {
        final ProcessionSettings settings = new ProcessionSettings(32, true, true, true, true);
        final String input = " ### ###  ###";
        final String expected = " |#| |#|  |##";
        final String result = util.postReplacements(input, preset.getCharacters(), settings);
        assertEquals(expected.length(), result.length());
        assertEquals(expected, result);
    }

    public final void testPostReplacementsDownUp() throws Exception {
        final String input = " _\" \"_ ";
        final String expected = " // \\\\ ";
        final String result = util.postReplacements(input, preset.getCharacters(), settings);
        assertEquals(expected, result);
    }

    public final void testPostReplacementsLeftRight() throws Exception {
        final String input = " \"#\" _#_ ";
        final String expected = " q#F J#L ";
        final String result = util.postReplacements(input, preset.getCharacters(), settings);
        assertEquals(expected, result);
    }
}
