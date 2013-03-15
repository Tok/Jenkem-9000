package jenkem.util

import jenkem.engine.ProcSettings
import jenkem.shared.Scheme
import jenkem.shared.color.IrcColor

object ColorUtil {
  def BC = String.valueOf('\u0002') // bold character for IRC
  def CC = String.valueOf('\u0003') // color character for IRC

  /**
   * Converts an IRC color to a CSS color.
   * @param ircColor
   * @return cssColor
   */
  def ircToCss(ircColor: String): String = ircToCss(Integer.valueOf(ircColor))

  /**
   * Converts an IRC color to a CSS color.
   * @param ircColor
   * @return cssColor
   */
  def ircToCss(ircColor: Int): String = rgbToCss(IrcColor.getFor(ircColor).getRgb)

  /**
   * Converts an RGB color to a CSS color.
   * @param rgb
   * @return cssColor
   */
  private def rgbToCss(rgb: Array[Int]): String = {
    "#" + toHex(rgb(0)) + toHex(rgb(1)) + toHex(rgb(2))
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
}
