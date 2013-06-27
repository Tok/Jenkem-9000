package jenkem.util

import jenkem.engine.color.Scheme

object ColorUtil {
  val BC = String.valueOf('\u0002') // bold character for IRC
  val CC = String.valueOf('\u0003') // color character for IRC

  /**
   * Converts an IRC color to a CSS color.
   * @param ircColor
   * @return cssColor
   */
  def ircToCss(ircColor: String): String = ircToCss(ircColor.toShort)

  /**
   * Converts an IRC color to a CSS color.
   * @param ircColor
   * @return cssColor
   */
  def ircToCss(ircColor: Short): String = {
    Scheme.valuOfIrcColor(ircColor) match {
      case Some(ircColor) => rgbToCss(ircColor.rgb)
      case None => "#000000"
    }
  }

  /**
   * Converts an RGB color to a CSS color.
   * @param rgb
   * @return cssColor
   */
  private def rgbToCss(rgb: (Short, Short, Short)): String = {
    "#" + toHex(rgb._1) + toHex(rgb._2) + toHex(rgb._3)
  }

  /**
   * Converts the provided interger to hex.
   * @param i
   * @return hex
   */
  private def toHex(i: Int): String = {
    val ret = Integer.toHexString(i)
    if (ret.length == 1) { "0" + ret } else { ret }
  }

  def makePwnIrc(bot: Short, top: Short): String = CC + bot + "," + top + "▄"

  private def commaSplit(s: String): Array[String] = s.split(",")
  def getFg(s: String): Int = commaSplit(s)(0).tail.toInt
  def getBgString(s: String): String = commaSplit(s)(1).init
  def getBg(s: String): Int = getBgString(s).toInt
}
