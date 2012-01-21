package jenkem.client;

import jenkem.shared.AsciiScheme;
import jenkem.shared.Engine;
import jenkem.shared.Kick;

public class EngineTester extends AbstractReflectionTestCase {
    private final Engine engine = new Engine(null);
    private final AsciiScheme scheme = new AsciiScheme();
    private final String up = scheme.getUp();
    private final String down = scheme.getDown();
    private final String hLine = scheme.getHline();

    public final void testUpPostProcession() throws Exception {
        final String upInput = "##" + up + up + up + up + "##";
        final Object[] upParameters = { upInput, true };
        final String upOutput = (String) invokePrivateMethod(engine,
                "postProcessVert", upParameters);
        assertEquals("##" + up + hLine + hLine + up + "##", upOutput);
    }

    public final void testDownPostProcession() throws Exception {
        final String downInput = "##" + down + down + down + down + "##";
        final Object[] downParameters = { downInput, false };
        final String downOutput = (String) invokePrivateMethod(engine,
                "postProcessVert", downParameters);
        assertEquals("##" + down + hLine + hLine + down + "##", downOutput);
    }

    public final void testPostProcession() throws Exception {
        final String input = "##" + down + down + down + down + "##" + up + up
                + up + up + "##";
        final Object[] parameters = { input };
        final String output = (String) invokePrivateMethod(engine,
                "postProcessRow", parameters);
        assertEquals("##" + down + hLine + hLine + down + "##" + up + hLine
                + hLine + up + "##", output);
    }

    /*
     * FIXME make colored post processing work public void
     * testColorPostProcessionWithColor() throws Exception { String input =
     * ColorUtil.CC + "##" + down + down + down + down + "##" + up + up + up +
     * up + "##"; final Object[] parameters = { input }; String output =
     * (String) invokePrivateMethod(engine, "postProcessColoredRow",
     * parameters); assertEquals(input, output); }
     */

    /*
     * FIXME make colored post processing work public void
     * testColorPostProcessionWithoutColor() throws Exception { String input =
     * "##" + down + down + down + down + "##" + up + up + up + up + "##"; final
     * Object[] parameters = { input }; String output = (String)
     * invokePrivateMethod(engine, "postProcessColoredRow", parameters);
     * assertEquals("##" + down + hLine + hLine + down + "##" + up + hLine +
     * hLine + up + "##", output); }
     */

    public final void testNoKick() throws Exception {
        final Object[] parameters = { Kick.Off };
        invokePrivateMethod(engine, "applyKicks", parameters);
        final Integer x = (Integer) retrievePrivateField(engine, "startX");
        final Integer y = (Integer) retrievePrivateField(engine, "startY");
        assertEquals(x.intValue(), 0);
        assertEquals(y.intValue(), 0);
    }

    public final void testXKick() throws Exception {
        final Object[] parameters = { Kick.X };
        invokePrivateMethod(engine, "applyKicks", parameters);
        final Integer x = (Integer) retrievePrivateField(engine, "startX");
        final Integer y = (Integer) retrievePrivateField(engine, "startY");
        assertEquals(x.intValue(), 1);
        assertEquals(y.intValue(), 0);
    }

    public final void testYKick() throws Exception {
        final Object[] parameters = { Kick.Y };
        invokePrivateMethod(engine, "applyKicks", parameters);
        final Integer x = (Integer) retrievePrivateField(engine, "startX");
        final Integer y = (Integer) retrievePrivateField(engine, "startY");
        assertEquals(x.intValue(), 0);
        assertEquals(y.intValue(), 1);
    }

    public final void testXyKick() throws Exception {
        final Object[] parameters = { Kick.XY };
        invokePrivateMethod(engine, "applyKicks", parameters);
        final Integer x = (Integer) retrievePrivateField(engine, "startX");
        final Integer y = (Integer) retrievePrivateField(engine, "startY");
        assertEquals(x.intValue(), 1);
        assertEquals(y.intValue(), 1);
    }

    public final void testIsUpOrDownExtremeValues() throws Exception {
        final int offset = 0;
        final int[] top = { 0, 0, 0 };
        final int[] bottom = { 255, 255, 255 };
        final Object[] parameters = { top, bottom, offset };
        final Boolean upOutput = (Boolean) invokePrivateMethod(engine, "isUp", parameters);
        assertTrue(upOutput.booleanValue());
        final Boolean downOutput = (Boolean) invokePrivateMethod(engine, "isDown", parameters);
        assertFalse(downOutput.booleanValue());
    }

    public final void testIsDownOrUpExtremeValues() throws Exception {
        final int offset = 0;
        final int[] top = { 255, 255, 255 };
        final int[] bottom = { 0, 0, 0 };
        final Object[] parameters = { top, bottom, offset };
        final Boolean downOutput = (Boolean) invokePrivateMethod(engine, "isDown", parameters);
        assertTrue(downOutput.booleanValue());
        final Boolean upOutput = (Boolean) invokePrivateMethod(engine, "isUp", parameters);
        assertFalse(upOutput.booleanValue());
    }

    public final void testIsUpOrDownBoundaryValues() throws Exception {
        final int offset = 0;
        final int[] top = { 126, 126, 126 };
        final int[] bottom = { 128, 128, 128 };
        final Object[] parameters = { top, bottom, offset };
        final Boolean upOutput = (Boolean) invokePrivateMethod(engine, "isUp", parameters);
        assertTrue(upOutput.booleanValue());
        final Boolean downOutput = (Boolean) invokePrivateMethod(engine, "isDown", parameters);
        assertFalse(downOutput.booleanValue());
    }

    public final void testIsDownOrUpBoundaryValues() throws Exception {
        final int offset = 0;
        final int[] top = { 128, 128, 128 };
        final int[] bottom = { 126, 126, 126 };
        final Object[] parameters = { top, bottom, offset };
        final Boolean downOutput = (Boolean) invokePrivateMethod(engine, "isDown", parameters);
        assertTrue(downOutput.booleanValue());
        final Boolean upOutput = (Boolean) invokePrivateMethod(engine, "isUp", parameters);
        assertFalse(upOutput.booleanValue());
    }

    public final void testIsUpOrDownOffsetValues() throws Exception {
        final int offset = 100;
        final int[] top = { 26, 26, 26 };
        final int[] bottom = { 228, 228, 228 };
        final Object[] parameters = { top, bottom, offset };
        final Boolean upOutput = (Boolean) invokePrivateMethod(engine, "isUp", parameters);
        assertTrue(upOutput.booleanValue());
        final Boolean downOutput = (Boolean) invokePrivateMethod(engine, "isDown", parameters);
        assertFalse(downOutput.booleanValue());
    }

    public final void testIsDownOrUpOffsetValues() throws Exception {
        final int offset = 100;
        final int[] top = { 228, 228, 228 };
        final int[] bottom = { 26, 26, 26 };
        final Object[] parameters = { top, bottom, offset };
        final Boolean downOutput = (Boolean) invokePrivateMethod(engine, "isDown", parameters);
        assertTrue(downOutput.booleanValue());
        final Boolean upOutput = (Boolean) invokePrivateMethod(engine, "isUp", parameters);
        assertFalse(upOutput.booleanValue());
    }

    public final void testEmptyLineRemover() throws Exception {
        final String[] input = { "X", "", "Y", null, "Z" };
        final Object[] parameters = { input };
        final String[] output = (String[]) invokePrivateMethod(engine, "removeEmptyLines", parameters);
        final String[] expected = { "X", "Y", "Z" };
        assertEquals(expected.length, output.length);
        int index = 0;
        for (final String exp : expected) {
            assertEquals(exp, output[index]);
            index++;
        }
    }

}
