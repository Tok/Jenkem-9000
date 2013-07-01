package jenkem.engine

import scala.util.Random

sealed abstract class Pal(val ascii: String, val ansi: String)

object Pal {
  val MAX_RGB = 255.shortValue

  case object UP extends Pal("\"", "▀▼")
  case object DOWN extends Pal("_", "▄▲")
  case object LEFT extends Pal("([{", "▌►")
  case object RIGHT extends Pal(")]}", "▐◄")
  case object UP_DOWN extends Pal("\\", "\\")
  case object DOWN_UP extends Pal("/", "/")
  case object LEFT_UP extends Pal("pF", "┌╔")
  case object LEFT_DOWN extends Pal("bL", "└╚")
  case object RIGHT_UP extends Pal("q", "┐╗")
  case object RIGHT_DOWN extends Pal("dJ", "┘╝")
  case object H_LINE extends Pal("-", "▬")
  case object V_LINE extends Pal("|", "│║")
  val values = List(UP, DOWN, LEFT, RIGHT, UP_DOWN, DOWN_UP, LEFT_UP, LEFT_DOWN, RIGHT_UP, RIGHT_DOWN, H_LINE, V_LINE)
  val pairs = List((UP_DOWN, DOWN_UP), (DOWN_UP, UP_DOWN), (LEFT_UP, RIGHT_UP), (RIGHT_UP, LEFT_UP), (LEFT_DOWN, RIGHT_DOWN), (RIGHT_DOWN, LEFT_DOWN))

  sealed abstract class Charset(val name: String, val chars: String)
  case object Hard extends Charset("Hard", " -+xX#")
  case object Soft extends Charset("Soft", " .:oO@")
  case object Ansi extends Charset("Ansi", " ░▒") //"▓" makes FG > BG and should not be used
  case object Party extends Charset("Party", " ♪♫☺☻")
  case object HCrude extends Charset("HCrude", " #")
  case object SCrude extends Charset("SCrude", " @")
  case object ACrude extends Charset("ACrude", " ▒")
  case object Mixed extends Charset("Mixed", "  .-:+oxOX@#")
  case object Letters extends Charset("Letters", "  ivozaxIVOAHZSXWM")
  case object Chaos extends Charset("Chaos", "  .'-:;~+=ox*OX&%$@#")
  val allCharsets = List(Hard, Soft, Ansi, Party, HCrude, SCrude, ACrude, Mixed, Letters, Chaos)
  val plainCharsets = List(Hard, Soft, Party, Mixed, Letters, Chaos)
  val stencilCharsets = List(HCrude, SCrude)
  val allAnsi = ("░▒▓▀▄▐▌╔╗╚╝┌┐└┘▬║│♪♫☺♥☻▼▲►◄")

  def valueOf(name: String): Option[Charset] = allCharsets.find(_.name.equalsIgnoreCase(name))

  def hasAnsi(s: String): Boolean = {
    allAnsi.toCharArray.toList.map(s.indexOf(_) >= 0).contains(true)
  }

  def getCharsetForMethod(method: Method): List[Charset] = {
    if (method.equals(Method.Plain)) { plainCharsets }
    else if (method.equals(Method.Stencil)) { stencilCharsets }
    else { allCharsets }
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

  def get(value: Pal, hasAnsi: Boolean, charset: String): String = {
    if (charset.equals(Chaos.chars)) {
      val chars = getValChars(value, hasAnsi).toCharArray.toList
      if (chars.length > 1) { Random.shuffle(chars).head.toString }
      else { chars.head.toString }
    } else if (charset.equals(Party.chars)) {
       getValChars(value, hasAnsi).takeRight(1)
    } else if (getValChars(value, hasAnsi).length > 1 &&
        (charset.equals(Hard.chars) || charset.equals(HCrude.chars) ||
        charset.equals(Mixed.chars) || charset.equals(Letters.chars) ||
        charset.takeRight(1).equals("#"))) {
      getValChars(value, hasAnsi).substring(1, 2)
    } else {
      getValChars(value, hasAnsi).take(1)
    }
  }

  def getValChars(pal: Pal, hasAnsi: Boolean): String = {
    if (hasAnsi) { pal.ansi } else { pal.ascii }
  }

  def isDark(charset: String, compare: String): Boolean = {
    charset.indexOf(compare) > ((charset.length - 1) / 2)
  }

  def isBright(charset: String, compare: String): Boolean = {
    val index = charset.indexOf(compare)
    index >= 0 && index < (charset.length / 2)
  }

  def getForMethod(method: Method): Charset = {
    if (method.equals(Method.Plain)) { Soft }
    else if (method.equals(Method.Stencil)) { HCrude }
    else { Ansi }
  }

  def darkest(charset: String): String = charset.last.toString
  def brightest(charset: String): String = charset.head.toString
}
