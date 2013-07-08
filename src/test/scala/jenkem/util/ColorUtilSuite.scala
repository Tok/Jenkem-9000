package jenkem.util

import org.junit.runner.RunWith
import org.scalatest.PrivateMethodTester.PrivateMethod
import org.scalatest.PrivateMethodTester.anyRefToInvoker
import jenkem.AbstractTester
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ColorUtilSuite extends AbstractTester {
  val whiteCss = "#FFFFFF"
  val blackCss = "#000000"
  val redCss = "#FF0000"

  test("Constants") {
    assert(ColorUtil.BC === String.valueOf('\u0002'))
    assert(ColorUtil.CC === String.valueOf('\u0003'))
  }

  test("IRC String to CSS") {
    assert(ColorUtil.ircToCss("0").equalsIgnoreCase(whiteCss))
    assert(ColorUtil.ircToCss("1").equalsIgnoreCase(blackCss))
    assert(ColorUtil.ircToCss("4").equalsIgnoreCase(redCss))
    assert(ColorUtil.ircToCss("-1").equalsIgnoreCase(blackCss))
    assert(ColorUtil.ircToCss("16").equalsIgnoreCase(blackCss))
    val e = intercept[NumberFormatException] {
      ColorUtil.ircToCss(" ")
    }
    assert(e.isInstanceOf[NumberFormatException])
  }

  test("IRC Short to CSS") {
    assert(ColorUtil.ircToCss(0.toShort).equalsIgnoreCase(whiteCss))
    assert(ColorUtil.ircToCss(1.toShort).equalsIgnoreCase(blackCss))
    assert(ColorUtil.ircToCss(4.toShort).equalsIgnoreCase(redCss))
    assert(ColorUtil.ircToCss(-1.toShort).equalsIgnoreCase(blackCss))
    assert(ColorUtil.ircToCss(16.toShort).equalsIgnoreCase(blackCss))
  }

  test("RGB to CSS") {
    val o = 0.toShort
    val x = 255.toShort
    val rgbToCss = PrivateMethod[String]('rgbToCss)
    assert(ColorUtil.invokePrivate(rgbToCss((o, o, o))).equalsIgnoreCase(blackCss))
    assert(ColorUtil.invokePrivate(rgbToCss((x, o, o))).equalsIgnoreCase(redCss))
    assert(ColorUtil.invokePrivate(rgbToCss((o, x, o))).equalsIgnoreCase("#00FF00"))
    assert(ColorUtil.invokePrivate(rgbToCss((o, o, x))).equalsIgnoreCase("#0000FF"))
    assert(ColorUtil.invokePrivate(rgbToCss((x, x, x))).equalsIgnoreCase(whiteCss))
  }

  test("To Hex") {
    val toHex = PrivateMethod[String]('toHex)
    assert(ColorUtil.invokePrivate(toHex(0)).equalsIgnoreCase("00"))
    assert(ColorUtil.invokePrivate(toHex(15)).equalsIgnoreCase("0F"))
    assert(ColorUtil.invokePrivate(toHex(16)).equalsIgnoreCase("10"))
  }

  test("Pwn IRC") {
    assert(ColorUtil.makePwnIrc(1.toShort, 0.toShort) === ColorUtil.CC + "1,0▄")
    assert(ColorUtil.makePwnIrc(0.toShort, 1.toShort) === ColorUtil.CC + "0,1▄")
    assert(ColorUtil.makePwnIrc(10.toShort, 11.toShort) === ColorUtil.CC + "10,11▄")
  }

  test("Comma Split") {
    val commaSplit = PrivateMethod[Array[String]]('commaSplit)
    val xo = ColorUtil.invokePrivate(commaSplit("X,O"))
    assert(xo(0) === "X")
    assert(xo(1) === "O")
    val x = ColorUtil.invokePrivate(commaSplit("X,"))
    assert(x(0) === "X")
    val o = ColorUtil.invokePrivate(commaSplit(",O"))
    assert(o(0) === "")
    assert(o(1) === "O")
    val c = ColorUtil.invokePrivate(commaSplit(","))
    assert(c.isEmpty)
  }

  test("FG") {
    assert(ColorUtil.getFg(ColorUtil.CC + "3,0▄") === 3)
    assert(ColorUtil.getFg(ColorUtil.CC + "11,0▄▄▄") === 11)
  }

  test("BG String") {
    assert(ColorUtil.getBgString(ColorUtil.CC + "4,0▄") === "0")
    assert(ColorUtil.getBgString(ColorUtil.CC + "0,10▄▄▄") === "10")
  }

  test("BG") {
    assert(ColorUtil.getBg(ColorUtil.CC + "1,0▄") === 0)
    assert(ColorUtil.getBg(ColorUtil.CC + "0,11▄▄▄") === 11)
  }
}
