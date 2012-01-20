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
		Object[] upParameters = { upInput, true };
		String upOutput = (String) invokePrivateMethod(engine, "postProcessVert", upParameters);
		assertEquals("##" + up + hLine + hLine + up + "##", upOutput);		
	}

	public void testDownPostProcession() throws Exception {
		String downInput = "##" + down + down + down + down + "##";
		Object[] downParameters = { downInput, false };
		String downOutput = (String) invokePrivateMethod(engine, "postProcessVert", downParameters);
		assertEquals("##" + down + hLine + hLine + down + "##", downOutput);
	}
	
	public void testPostProcession() throws Exception {
		String input = "##" + down + down + down + down + "##" + up + up + up + up + "##";
		Object[] parameters = { input };
		String output = (String) invokePrivateMethod(engine, "postProcessRow", parameters);
		assertEquals("##" + down + hLine + hLine + down + "##" + up + hLine + hLine + up + "##", output);
	}

	/* FIXME make colored post processing work
	public void testColorPostProcessionWithColor() throws Exception {
		String input = ColorUtil.CC + "##" + down + down + down + down + "##" + up + up + up + up + "##";
		Object[] parameters = { input };
		String output = (String) invokePrivateMethod(engine, "postProcessColoredRow", parameters);
		assertEquals(input, output);
	}
	*/

	/* FIXME make colored post processing work
	public void testColorPostProcessionWithoutColor() throws Exception {
		String input = "##" + down + down + down + down + "##" + up + up + up + up + "##";
		Object[] parameters = { input };
		String output = (String) invokePrivateMethod(engine, "postProcessColoredRow", parameters);
		assertEquals("##" + down + hLine + hLine + down + "##" + up + hLine + hLine + up + "##", output);
	}
	*/
	
	public void testNoKick() throws Exception {		
		Object[] parameters = { Kick.Off };
		invokePrivateMethod(engine, "applyKicks", parameters);
		Integer x = (Integer) retrievePrivateField(engine, "startX");
		Integer y = (Integer) retrievePrivateField(engine, "startY");
		assertEquals(x.intValue(), 0);
		assertEquals(y.intValue(), 0);
	}
	
	public void testXKick() throws Exception {		
		Object[] parameters = { Kick.X };
		invokePrivateMethod(engine, "applyKicks", parameters);
		Integer x = (Integer) retrievePrivateField(engine, "startX");
		Integer y = (Integer) retrievePrivateField(engine, "startY");
		assertEquals(x.intValue(), 1);
		assertEquals(y.intValue(), 0);
	}

	public void testYKick() throws Exception {		
		Object[] parameters = { Kick.Y };
		invokePrivateMethod(engine, "applyKicks", parameters);
		Integer x = (Integer) retrievePrivateField(engine, "startX");
		Integer y = (Integer) retrievePrivateField(engine, "startY");
		assertEquals(x.intValue(), 0);
		assertEquals(y.intValue(), 1);
	}

	public void testXyKick() throws Exception {		
		Object[] parameters = { Kick.XY };
		invokePrivateMethod(engine, "applyKicks", parameters);
		Integer x = (Integer) retrievePrivateField(engine, "startX");
		Integer y = (Integer) retrievePrivateField(engine, "startY");
		assertEquals(x.intValue(), 1);
		assertEquals(y.intValue(), 1);
	}

	public void testIsUpOrDownExtremeValues() throws Exception {
		int offset = 0;
		Object[] parameters = { 0, 0, 0, 255, 255, 255, offset };
		Boolean upOutput = (Boolean) invokePrivateMethod(engine, "isUp", parameters);
		assertTrue(upOutput.booleanValue());
		Boolean downOutput = (Boolean) invokePrivateMethod(engine, "isDown", parameters);
		assertFalse(downOutput.booleanValue());
	}

	public void testIsDownOrUpExtremeValues() throws Exception {
		int offset = 0;
		Object[] parameters = { 255, 255, 255, 0, 0, 0, offset };
		Boolean downOutput = (Boolean) invokePrivateMethod(engine, "isDown", parameters);
		assertTrue(downOutput.booleanValue());
		Boolean upOutput = (Boolean) invokePrivateMethod(engine, "isUp", parameters);
		assertFalse(upOutput.booleanValue());
	}

	public void testIsUpOrDownBoundaryValues() throws Exception {
		int offset = 0;
		Object[] parameters = { 126, 126, 126, 128, 128, 128, offset };
		Boolean upOutput = (Boolean) invokePrivateMethod(engine, "isUp", parameters);
		assertTrue(upOutput.booleanValue());
		Boolean downOutput = (Boolean) invokePrivateMethod(engine, "isDown", parameters);
		assertFalse(downOutput.booleanValue());
	}

	public void testIsDownOrUpBoundaryValues() throws Exception {
		int offset = 0;
		Object[] parameters = { 128, 128, 128, 126, 126, 126, offset };
		Boolean downOutput = (Boolean) invokePrivateMethod(engine, "isDown", parameters);
		assertTrue(downOutput.booleanValue());
		Boolean upOutput = (Boolean) invokePrivateMethod(engine, "isUp", parameters);
		assertFalse(upOutput.booleanValue());
	}

	public void testIsUpOrDownOffsetValues() throws Exception {
		int offset = 100;
		Object[] parameters = { 26, 26, 26, 228, 228, 228, offset };
		Boolean upOutput = (Boolean) invokePrivateMethod(engine, "isUp", parameters);
		assertTrue(upOutput.booleanValue());
		Boolean downOutput = (Boolean) invokePrivateMethod(engine, "isDown", parameters);
		assertFalse(downOutput.booleanValue());
	}

	public void testIsDownOrUpOffsetValues() throws Exception {
		int offset = 100;
		Object[] parameters = { 228, 228, 228, 26, 26, 26, offset };
		Boolean downOutput = (Boolean) invokePrivateMethod(engine, "isDown", parameters);
		assertTrue(downOutput.booleanValue());
		Boolean upOutput = (Boolean) invokePrivateMethod(engine, "isUp", parameters);
		assertFalse(upOutput.booleanValue());
	}
	
	public void testEmptyLineRemover() throws Exception {
		String[] input = { "X", "", "Y", null, "Z" };
		Object[] parameters = { input };
		String[] output = (String[]) invokePrivateMethod(engine, "removeEmptyLines", parameters);
		String[] expected = { "X", "Y", "Z" };
		assertEquals(expected.length, output.length);
		int index = 0;
		for (String exp : expected) {
			assertEquals(exp, output[index]);
			index++;
		}
	}
	
}
