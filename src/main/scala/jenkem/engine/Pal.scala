package jenkem.engine

object Pal {
  val MAX_RGB = 255.shortValue

  sealed abstract class Value(val ascii: String, val ansi: String)
  case object UP extends Value("\"", "▀")
  case object DOWN extends Value("_", "▄")
  case object LEFT extends Value("([{", "▌")
  case object RIGHT extends Value(")]}", "▐")
  case object UP_DOWN extends Value("\\", "\\")
  case object DOWN_UP extends Value("/", "/")
  case object LEFT_UP extends Value("pF", "╔")
  case object LEFT_DOWN extends Value("bL", "╚")
  case object RIGHT_UP extends Value("q", "╗")
  case object RIGHT_DOWN extends Value("dJ", "╝")
  case object H_LINE extends Value("-", "▬")
  case object V_LINE extends Value("|", "│")
  val values = List(UP, DOWN, LEFT, RIGHT, UP_DOWN, DOWN_UP, LEFT_UP, LEFT_DOWN, RIGHT_UP, RIGHT_DOWN, H_LINE, V_LINE)

  sealed abstract class Charset(val name: String, val chars: String)
  case object Hard extends Charset("Hard", " -+xX#")
  case object Soft extends Charset("Soft", " .:oO@")
  case object Ansi extends Charset("Ansi", " ░▒") //"▓" makes FG > BG and should not be used
  case object HCrude extends Charset("HCrude", " #")
  case object SCrude extends Charset("SCrude", " @")
  case object ACrude extends Charset("ACrude", " ▒")
  case object Mixed extends Charset("Mixed", "  .-:+oxOX@#")
  case object Letters extends Charset("Letters", "  ivozaxIVOAHZSXWM")
  case object Chaos extends Charset("Chaos", "  .'-:;~+=ox*OX&%$@#")
  val charsets = List(Hard, Soft, Ansi, HCrude, SCrude, ACrude, Mixed, Letters, Chaos)
  val allAnsi = ("░▒▓▀▄▐▌╔╗╚╝▬│")

  def valueOf(name: String): Option[Charset] = charsets.find(_.name.equalsIgnoreCase(name))

  def hasAnsi(s: String): Boolean = {
    allAnsi.toCharArray.toList.map(s.indexOf(_) >= 0).contains(true)
  }

  /**
   * Selects a character from the provided characters and selects the
   * character to use, depending on the entered strength value.
   * the palette must be a String like ' .:xX#', from bright to dark.
   */
  def getCharAbs(charset: String, strength: Double): String = {
    getChar(charset, (MAX_RGB - strength) / MAX_RGB)
  }

  def getChar(charset: String, strength: Double): String = {
    val fixedStrenght = (Math.max(0D, Math.min(0.99D, strength)))
    val index: Int =  (fixedStrenght * charset.length).intValue
    charset.substring(index, index + 1)
  }

  def get(value: Value, hasAnsi: Boolean): String = getVal(value, hasAnsi).take(1).toString

  private def getVal(value: Value, hasAnsi: Boolean): String = {
    value match { case v: Value => if (hasAnsi) { v.ansi } else { v.ascii } }
  }

  def isDark(charset: String, compare: String): Boolean = {
    charset.indexOf(compare) >= (charset.length / 2)
  }

  def isBright(charset: String, compare: String): Boolean = {
    val index = charset.indexOf(compare)
    index >= 0 && index <= (charset.length / 2)
  }

  def getForMethod(method: ConversionMethod.Value): Charset = {
    if (method.equals(ConversionMethod.Plain)) { Soft }
    else if (method.equals(ConversionMethod.Stencil)) { HCrude }
    else { Pal.Ansi }
  }
}
