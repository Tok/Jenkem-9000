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

  sealed abstract class Charset(val chars: String)
  case object Hard extends Charset(" -+xX#")
  case object Soft extends Charset(" .:oO@")
  case object Ansi extends Charset(" ░▒") //"▓" makes FG > BG and should not be used
  case object HCrude extends Charset(" #")
  case object SCrude extends Charset(" @")
  case object ACrude extends Charset(" ▒")
  case object Mixed extends Charset("  .-:+oxOX@#")
  case object Letters extends Charset("  ivozaxIVOAHZSXWM")
  case object Chaos extends Charset("  .'-:;~+=ox*OX&%$@#")
  val charsets = List(Hard, Soft, Ansi, HCrude, SCrude, ACrude, Mixed, Letters, Chaos)
  val allAnsi = ("░▒▓▀▄▐▌╔╗╚╝▬│")

  def valueOf(name: String): Charset = {
    name.toUpperCase match {
      case "HARD" => Hard
      case "SOFT" => Soft
      case "ANSI" => Ansi
      case "HCRUDE" => HCrude
      case "SCRUDE" => SCrude
      case "ACRUDE" => ACrude
      case "MIXED" => Mixed
      case "LETTERS" => Letters
      case "CHAOS" => Chaos
      case _ => throw new IllegalArgumentException("CharSet must be one of: Hard, Soft, Ansi, HCrude, SCrude, ACrude, Mixed, Letters or Chaos.")
    }
  }

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
}
