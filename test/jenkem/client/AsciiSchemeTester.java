package jenkem.client;

import jenkem.shared.AsciiScheme;
import jenkem.shared.CharacterSet;

public class AsciiSchemeTester extends AbstractReflectionTestCase {
    private final AsciiScheme scheme = new AsciiScheme();

    public final void testNonRandomization() throws Exception {
        final AsciiScheme changedScheme = (AsciiScheme) changePrivateBooleanField(scheme, "randomized", false);
        final String input = "ABC";
        final Object[] parameters = {input};
        final String output = (String) invokePrivateMethod(changedScheme, "randomize", parameters);
        assertEquals("A", output);
        assertEquals(1, output.length());
    }

    public final void testRandomizerLength() throws Exception {
        final AsciiScheme changedScheme = (AsciiScheme) changePrivateBooleanField(scheme, "randomized", true);
        final String input = "ABC";
        final Object[] parameters = {input};
        final String output = (String) invokePrivateMethod(changedScheme, "randomize", parameters);
        assertEquals(1, output.length());
    }

    public final void testNotRandomizeSix() throws Exception {
        final AsciiScheme changedScheme = (AsciiScheme) changePrivateBooleanField(scheme, "randomized", false);
        final String input = "\\\\\"_',";
        final Object[] parameters = {input};
        final String output = (String) invokePrivateMethod(changedScheme, "randomizeSix", parameters);
        assertEquals(2, output.length());
        assertEquals("\\\\", output);
    }

    public final void testRandomizeSixLength() throws Exception {
        final AsciiScheme changedScheme = (AsciiScheme) changePrivateBooleanField(scheme, "randomized", true);
        final String input = "\\\\\"_',";
        final Object[] parameters = {input};
        final String output = (String) invokePrivateMethod(changedScheme, "randomizeSix", parameters);
        assertEquals(2, output.length());
    }

    public final void testReplace() throws Exception {
        assertEquals("###-", scheme.replace("###X", "-"));
        assertEquals("-", scheme.replace("X", "-"));
    }

    public final void testShortUnFormat() throws Exception {
        final String input = "X";
        final Object[] parameters = {input};
        final String output = (String) invokePrivateMethod(scheme, "unFormat", parameters);
        assertEquals(input, output);
    }

    public final void testLongUnFormat() throws Exception {
        final Object[] parameters = {"###X"};
        final String output = (String) invokePrivateMethod(scheme, "unFormat", parameters);
        assertEquals("X", output);
    }

    public final void testIsCharacterBright() throws Exception {
        for (final CharacterSet set : CharacterSet.values()) {
            final String trueResult = set.getCharacters().substring(0, 1);
            assertTrue(scheme.isCharacterBright(trueResult, set));
            final String secondTrueResult = set.getCharacters().substring(1, 2);
            assertTrue(scheme.isCharacterBright(secondTrueResult, set));
            // XXX ANSI character sets omitted
            if (!set.equals(CharacterSet.Ansi)
                    && !set.equals(CharacterSet.DoubleAnsi)
                    && !set.equals(CharacterSet.HalfAnsi)) {
                final String falseResult =
                        set.getCharacters().substring(
                        set.getCharacters().length() - 1,
                        set.getCharacters().length());
                assertFalse(scheme.isCharacterBright(falseResult, set));
            }
        }
    }

    public final void testIsCharacterDark() throws Exception {
        for (final CharacterSet set : CharacterSet.values()) {
            final String trueResult =
                    set.getCharacters().substring(
                    set.getCharacters().length() - 1,
                    set.getCharacters().length());
            assertTrue(scheme.isCharacterDark(trueResult, set));
            final String falseResult = set.getCharacters().substring(0, 1);
            assertFalse(scheme.isCharacterDark(falseResult, set));
            final String secondFalseResult = set.getCharacters().substring(1, 2);
            assertFalse(scheme.isCharacterDark(secondFalseResult, set));
        }
    }

    public final void testDarkestCharacter() throws Exception {
        for (final CharacterSet set : CharacterSet.values()) {
            final String expected =
                    set.getCharacters().substring(
                    set.getCharacters().length() - 1,
                    set.getCharacters().length());
            final String result = scheme.getDarkestCharacter(set);
            assertEquals(expected, result);
            assertEquals(1, result.length());
        }
    }

    public final void testDarkestGetChar() throws Exception {
        for (final CharacterSet set : CharacterSet.values()) {
            final Object[] parameters = {1.0d, set};
            final String output = (String) invokePrivateMethod(scheme, "getChar", parameters);
            final String expected =
                    set.getCharacters().substring(
                    set.getCharacters().length() - 1,
                    set.getCharacters().length());
            assertEquals(expected, output);
            assertNotSame("!", output);
        }
    }

    public final void testBrightestGetChar() throws Exception {
        for (final CharacterSet set : CharacterSet.values()) {
            final Object[] parameters = {0.0d, set};
            final String output = (String) invokePrivateMethod(scheme, "getChar", parameters);
            final String expected = set.getCharacters().substring(0, 1);
            assertEquals(expected, output);
            assertNotSame("!", output);
        }
    }

}
