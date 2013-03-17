package jenkem.engine

import scala.util.Random

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
  case object HARD extends Charset("Hard", " -+xX#")
  case object SOFT extends Charset("Soft", " .:oO@")
  case object ANSI extends Charset("Ansi", " ░▒") //"▓" makes FG > BG and should not be used
  case object HCRUDE extends Charset("HCrude", " #")
  case object SCRUDE extends Charset("SCrude", " @")
  case object ACRUDE extends Charset("ACrude", " ▒")
  case object MIXED extends Charset("Mixed", "  .-:+oxOX@#")
  case object LETTERS extends Charset("Letters", "  ivozaxIVOAHZSXWM")
  case object CHAOS extends Charset("Chaos", "  .'-:;~+=ox*OX&%$@#")
  val charsets = List(HARD, SOFT, ANSI, HCRUDE, SCRUDE, ACRUDE, MIXED, LETTERS, CHAOS)
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

  def getValChars(value: Value, hasAnsi: Boolean) = getVal(value, hasAnsi)
  def get(value: Value, hasAnsi: Boolean, charset: String): String = {
    if (charset.equals(CHAOS.chars)) {
      val chars = getVal(value, hasAnsi).toCharArray.toList
      if (chars.length > 1) { Random.shuffle(chars).head.toString }
      else { chars.head.toString }
    } else if (charset.equals(HARD.chars) || charset.equals(HCRUDE.chars) ||
        charset.equals(MIXED.chars) || charset.equals(LETTERS.chars) ||
        charset.takeRight(1).equals("#")) {
      if (getVal(value, hasAnsi).length > 1) {
        getVal(value, hasAnsi).toString.substring(1, 2)
      } else {
        getVal(value, hasAnsi).take(1).toString
      }
    } else {
      getVal(value, hasAnsi).take(1).toString
    }
  }

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
    if (method.equals(ConversionMethod.Plain)) { SOFT }
    else if (method.equals(ConversionMethod.Stencil)) { HCRUDE }
    else { Pal.ANSI }
  }
}
