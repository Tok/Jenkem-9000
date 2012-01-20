package jenkem.client;

import jenkem.shared.AsciiScheme;
import jenkem.shared.CharacterSet;

public class AsciiSchemeTester extends AbstractReflectionTestCase {
	private final AsciiScheme scheme = new AsciiScheme();	

	public void testNonRandomization() throws Exception {
		AsciiScheme changedScheme = (AsciiScheme) changePrivateBooleanField(scheme, "randomized", false);
		String input = "ABC";
		final Object[] parameters = { input };
		String output = (String) invokePrivateMethod(changedScheme, "randomize", parameters);
		assertEquals("A", output);
		assertEquals(1, output.length());
	}

	public void testRandomizerLength() throws Exception {
		AsciiScheme changedScheme = (AsciiScheme) changePrivateBooleanField(scheme, "randomized", true);
		String input = "ABC";
		final Object[] parameters = { input };
		String output = (String) invokePrivateMethod(changedScheme, "randomize", parameters);
		assertEquals(1, output.length());
	}
	
	public void testNotRandomizeSix() throws Exception {
		AsciiScheme changedScheme = (AsciiScheme) changePrivateBooleanField(scheme, "randomized", false);
		String input = "\\\\\"_',";
		final Object[] parameters = { input };
		String output = (String) invokePrivateMethod(changedScheme, "randomizeSix", parameters);
		assertEquals(2, output.length());
		assertEquals("\\\\", output);
	}
	
	public void testRandomizeSixLength() throws Exception {
		AsciiScheme changedScheme = (AsciiScheme) changePrivateBooleanField(scheme, "randomized", true);
		String input = "\\\\\"_',";
		final Object[] parameters = { input };
		String output = (String) invokePrivateMethod(changedScheme, "randomizeSix", parameters);
		assertEquals(2, output.length());
	}
	
	public void testReplace() throws Exception {
		assertEquals("###-", scheme.replace("###X", "-"));
		assertEquals("-", scheme.replace("X", "-"));
	}
	
	public void testShortUnFormat() throws Exception {
		String input = "X";
		final Object[] parameters = { input };
		String output = (String) invokePrivateMethod(scheme, "unFormat", parameters);
		assertEquals(input, output);
	}
	
	public void testLongUnFormat() throws Exception {
		final Object[] parameters = { "###X" };
		String output = (String) invokePrivateMethod(scheme, "unFormat", parameters);
		assertEquals("X", output);
	}

	public void testIsCharacterBright() throws Exception {
		for (final CharacterSet set : CharacterSet.values()) {
			String trueResult = set.getCharacters().substring(0, 1);
			assertTrue(scheme.isCharacterBright(trueResult, set));
			String secondTrueResult = set.getCharacters().substring(1, 2);
			assertTrue(scheme.isCharacterBright(secondTrueResult, set));
			//XXX ANSI character sets omitted
			if (!set.equals(CharacterSet.Ansi) && !set.equals(CharacterSet.DoubleAnsi) && !set.equals(CharacterSet.HalfAnsi)) {
				String falseResult = set.getCharacters().substring(set.getCharacters().length() -1, set.getCharacters().length());
				assertFalse(scheme.isCharacterBright(falseResult, set));
			}
		}
	}

	public void testIsCharacterDark() throws Exception {
		for (final CharacterSet set : CharacterSet.values()) {
			String trueResult = set.getCharacters().substring(set.getCharacters().length() -1, set.getCharacters().length());
			assertTrue(scheme.isCharacterDark(trueResult, set));
			String falseResult = set.getCharacters().substring(0, 1);
			assertFalse(scheme.isCharacterDark(falseResult, set));
			String secondFalseResult = set.getCharacters().substring(1, 2);
			assertFalse(scheme.isCharacterDark(secondFalseResult, set));
		}
	}

	public void testDarkestCharacter() throws Exception {
		for (final CharacterSet set : CharacterSet.values()) {
			String expected = set.getCharacters().substring(set.getCharacters().length() -1, set.getCharacters().length());
			String result = scheme.getDarkestCharacter(set);
			assertEquals(expected, result);
			assertEquals(1, result.length());
		}
	}
	
	public void testDarkestGetChar() throws Exception {
		for (final CharacterSet set : CharacterSet.values()) {
			final Object[] parameters = { 1.0d, set };
			String output = (String) invokePrivateMethod(scheme, "getChar", parameters);
			String expected = set.getCharacters().substring(set.getCharacters().length() -1, set.getCharacters().length());
			assertEquals(expected, output);
			assertNotSame("!", output);
		}
	}
	
	public void testBrightestGetChar() throws Exception {
		for (final CharacterSet set : CharacterSet.values()) {
			final Object[] parameters = { 0.0d, set };
			String output = (String) invokePrivateMethod(scheme, "getChar", parameters);
			String expected = set.getCharacters().substring(0, 1);
			assertEquals(expected, output);
			assertNotSame("!", output);
		}
	}

}
