package jenkem.engine

import org.junit.runner.RunWith
import jenkem.AbstractTester
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class PalSuite extends AbstractTester {
  val bright = " "
  val center = "+"
  val o = "o"
  val dark = "#"
  val chars = " -+X#"

  test("Constants") {
    assert(Pal.MAX_RGB === 255)
    assert(Pal.values.size === 12)
    assert(Pal.pairs.size === 6)
    assert(Pal.allCharsets.size >= Pal.plainCharsets.size)
    assert(Pal.allCharsets.size >= Pal.stencilCharsets.size)
    assert(Pal.hasAnsi(Pal.allAnsi))
  }

  test("Has ANSI") {
    assert(Pal.hasAnsi("░"))
    assert(Pal.hasAnsi("▒"))
    assert(Pal.hasAnsi("▓"))
    assert(Pal.hasAnsi("▀"))
    assert(Pal.hasAnsi("▄"))
    assert(Pal.hasAnsi("▐"))
    assert(Pal.hasAnsi("▌"))
    assert(!Pal.hasAnsi("X"))
    assert(!Pal.hasAnsi("Y"))
    assert(!Pal.hasAnsi(" "))
    assert(!Pal.hasAnsi(""))
  }

  test("Charsets For Method") {
    assert(Pal.getCharsetForMethod(Method.Vortacular) === Pal.allCharsets)
    assert(Pal.getCharsetForMethod(Method.Pwntari) === Pal.allCharsets)
    assert(Pal.getCharsetForMethod(Method.Plain) === Pal.plainCharsets)
    assert(Pal.getCharsetForMethod(Method.Stencil) === Pal.stencilCharsets)
  }

  test("Palette For Method") {
    assert(Pal.getForMethod(Method.Vortacular) === Pal.Ansi)
    assert(Pal.getForMethod(Method.Pwntari) === Pal.Ansi)
    assert(Pal.getForMethod(Method.Plain) === Pal.Soft)
    assert(Pal.getForMethod(Method.Stencil) === Pal.HCrude)
  }

  test("Char Abs") {
    assert(Pal.getCharAbs(chars, 255D).equals(bright))
    assert(Pal.getCharAbs(chars, 0D).equals(dark))
    assert(Pal.getCharAbs(chars, 127D).equals(center))
    assert(Pal.getChar(chars, 1D).equals(dark))
    assert(Pal.getChar(chars, 0D).equals(bright))
    assert(Pal.getChar(chars, 0.5D).equals(center))
  }

  test("Get Character String") {
    assert(Pal.get(Pal.UP, false, Pal.Hard.chars).equals("\""))
    assert(Pal.get(Pal.DOWN, false, Pal.Hard.chars).equals("_"))
    assert(Pal.get(Pal.UP, true, Pal.Ansi.chars).equals("▀"))
    assert(Pal.get(Pal.DOWN, true, Pal.Ansi.chars).equals("▄"))
    assert(Pal.get(Pal.UP, true, Pal.Party.chars).equals("▼"))
    assert(Pal.get(Pal.DOWN, true, Pal.Party.chars).equals("▲"))
    val mixed = " ░▒#"
    assert(Pal.get(Pal.LEFT, true, mixed).equals("►"))
    assert(Pal.get(Pal.RIGHT, true, mixed).equals("◄"))
    def test(p: Pal): Unit = {
      def testCharset(c: Pal.Charset): Unit = {
        val hasAnsi = Pal.hasAnsi(c.chars)
        val char = Pal.get(p, hasAnsi, c.chars)
        assert(char.size === 1)
        assert(Pal.hasAnsi(char) == hasAnsi || hasAnsi)
      }
      Pal.allCharsets.foreach(testCharset(_))
    }
    Pal.values.foreach(test(_))
  }

  test("Get Val Chars") {
    assert(Pal.getValChars(Pal.H_LINE, false).equals("-"))
    assert(Pal.getValChars(Pal.H_LINE, true).equals("▬"))
  }

  test("Dark") {
    def test(c: Pal.Charset): Unit = {
      assert(Pal.isDark(c.chars, c.chars.takeRight(1)))
      assert(!Pal.isDark(c.chars, c.chars.take(1)))
    }
    Pal.allCharsets.foreach(test(_))
    assert(!Pal.isDark(chars, o)) //char is not in charset
  }

  test("Bright") {
    def test(c: Pal.Charset): Unit = {
      assert(Pal.isBright(c.chars, c.chars.take(1)))
      assert(!Pal.isBright(c.chars, c.chars.takeRight(1)))
    }
    Pal.allCharsets.foreach(test(_))
    assert(!Pal.isBright(chars, o)) //char is not in charset
  }

  test("Darkest") {
    def test(c: Pal.Charset): Unit = {
      assert(Pal.darkest(c.chars).equals(c.chars.takeRight(1)))
      assert(!Pal.darkest(c.chars).equals(c.chars.take(1)))
    }
    Pal.allCharsets.foreach(test(_))
  }

  test("Brightest") {
    def test(c: Pal.Charset): Unit = {
      assert(Pal.brightest(c.chars).equals(c.chars.take(1)))
      assert(!Pal.brightest(c.chars).equals(c.chars.takeRight(1)))
    }
    Pal.allCharsets.foreach(test(_))
  }

  test("Pointless") {
    Pal.allCharsets.foreach(testAny(_))
    Pal.values.foreach(testAny(_))
  }

}
