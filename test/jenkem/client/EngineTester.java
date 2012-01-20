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
	
	
	public void testUpPostProcession() throws Exception {
		String upInput = "##" + up + up + up + up + "##";
		final Object[] upParameters = { upInput, true };
		String upOutput = (String) invokePrivateMethod(engine, "postProcessVert", upParameters);
		assertEquals("##" + up + hLine + hLine + up + "##", upOutput);		
	}

	public void testDownPostProcession() throws Exception {
		String downInput = "##" + down + down + down + down + "##";
		final Object[] downParameters = { downInput, false };
		String downOutput = (String) invokePrivateMethod(engine, "postProcessVert", downParameters);
		assertEquals("##" + down + hLine + hLine + down + "##", downOutput);
	}
	
	public void testPostProcession() throws Exception {
		String input = "##" + down + down + down + down + "##" + up + up + up + up + "##";
		final Object[] parameters = { input };
		String output = (String) invokePrivateMethod(engine, "postProcessRow", parameters);
		assertEquals("##" + down + hLine + hLine + down + "##" + up + hLine + hLine + up + "##", output);
	}

	/* FIXME make colored post processing work
	public void testColorPostProcessionWithColor() throws Exception {
		String input = ColorUtil.CC + "##" + down + down + down + down + "##" + up + up + up + up + "##";
		final Object[] parameters = { input };
		String output = (String) invokePrivateMethod(engine, "postProcessColoredRow", parameters);
		assertEquals(input, output);
	}
	*/

	/* FIXME make colored post processing work
	public void testColorPostProcessionWithoutColor() throws Exception {
		String input = "##" + down + down + down + down + "##" + up + up + up + up + "##";
		final Object[] parameters = { input };
		String output = (String) invokePrivateMethod(engine, "postProcessColoredRow", parameters);
		assertEquals("##" + down + hLine + hLine + down + "##" + up + hLine + hLine + up + "##", output);
	}
	*/
	
	public void testNoKick() throws Exception {		
		final Object[] parameters = { Kick.Off };
		invokePrivateMethod(engine, "applyKicks", parameters);
		final Integer x = (Integer) retrievePrivateField(engine, "startX");
		final Integer y = (Integer) retrievePrivateField(engine, "startY");
		assertEquals(x.intValue(), 0);
		assertEquals(y.intValue(), 0);
	}
	
	public void testXKick() throws Exception {		
		final Object[] parameters = { Kick.X };
		invokePrivateMethod(engine, "applyKicks", parameters);
		final Integer x = (Integer) retrievePrivateField(engine, "startX");
		final Integer y = (Integer) retrievePrivateField(engine, "startY");
		assertEquals(x.intValue(), 1);
		assertEquals(y.intValue(), 0);
	}

	public void testYKick() throws Exception {		
		final Object[] parameters = { Kick.Y };
		invokePrivateMethod(engine, "applyKicks", parameters);
		final Integer x = (Integer) retrievePrivateField(engine, "startX");
		final Integer y = (Integer) retrievePrivateField(engine, "startY");
		assertEquals(x.intValue(), 0);
		assertEquals(y.intValue(), 1);
	}

	public void testXyKick() throws Exception {		
		final Object[] parameters = { Kick.XY };
		invokePrivateMethod(engine, "applyKicks", parameters);
		final Integer x = (Integer) retrievePrivateField(engine, "startX");
		final Integer y = (Integer) retrievePrivateField(engine, "startY");
		assertEquals(x.intValue(), 1);
		assertEquals(y.intValue(), 1);
	}

	public void testIsUpOrDownExtremeValues() throws Exception {
		final int offset = 0;
		final Object[] parameters = { 0, 0, 0, 255, 255, 255, offset };
		final Boolean upOutput = (Boolean) invokePrivateMethod(engine, "isUp", parameters);
		assertTrue(upOutput.booleanValue());
		final Boolean downOutput = (Boolean) invokePrivateMethod(engine, "isDown", parameters);
		assertFalse(downOutput.booleanValue());
	}

	public void testIsDownOrUpExtremeValues() throws Exception {
		final int offset = 0;
		final Object[] parameters = { 255, 255, 255, 0, 0, 0, offset };
		final Boolean downOutput = (Boolean) invokePrivateMethod(engine, "isDown", parameters);
		assertTrue(downOutput.booleanValue());
		final Boolean upOutput = (Boolean) invokePrivateMethod(engine, "isUp", parameters);
		assertFalse(upOutput.booleanValue());
	}

	public void testIsUpOrDownBoundaryValues() throws Exception {
		final int offset = 0;
		final Object[] parameters = { 126, 126, 126, 128, 128, 128, offset };
		final Boolean upOutput = (Boolean) invokePrivateMethod(engine, "isUp", parameters);
		assertTrue(upOutput.booleanValue());
		final Boolean downOutput = (Boolean) invokePrivateMethod(engine, "isDown", parameters);
		assertFalse(downOutput.booleanValue());
	}

	public void testIsDownOrUpBoundaryValues() throws Exception {
		final int offset = 0;
		final Object[] parameters = { 128, 128, 128, 126, 126, 126, offset };
		final Boolean downOutput = (Boolean) invokePrivateMethod(engine, "isDown", parameters);
		assertTrue(downOutput.booleanValue());
		final Boolean upOutput = (Boolean) invokePrivateMethod(engine, "isUp", parameters);
		assertFalse(upOutput.booleanValue());
	}

	public void testIsUpOrDownOffsetValues() throws Exception {
		final int offset = 100;
		final Object[] parameters = { 26, 26, 26, 228, 228, 228, offset };
		final Boolean upOutput = (Boolean) invokePrivateMethod(engine, "isUp", parameters);
		assertTrue(upOutput.booleanValue());
		final Boolean downOutput = (Boolean) invokePrivateMethod(engine, "isDown", parameters);
		assertFalse(downOutput.booleanValue());
	}

	public void testIsDownOrUpOffsetValues() throws Exception {
		final int offset = 100;
		final Object[] parameters = { 228, 228, 228, 26, 26, 26, offset };
		final Boolean downOutput = (Boolean) invokePrivateMethod(engine, "isDown", parameters);
		assertTrue(downOutput.booleanValue());
		final Boolean upOutput = (Boolean) invokePrivateMethod(engine, "isUp", parameters);
		assertFalse(upOutput.booleanValue());
	}
	
	public void testEmptyLineRemover() throws Exception {
		final String[] input = { "X", "", "Y", null, "Z" };
		final Object[] parameters = { input };
		final String[] output = (String[]) invokePrivateMethod(engine, "removeEmptyLines", parameters);
		final String[] expected = { "X", "Y", "Z" };
		assertEquals(expected.length, output.length);
		int index = 0;
		for (String exp : expected) {
			assertEquals(exp, output[index]);
			index++;
		}
	}
	
}
