package jenkem.util

import jenkem.engine.Method
import jenkem.engine.Pal
import jenkem.engine.color.Scheme
import jenkem.engine.color.Color

object InitUtil {
  val DEFAULT_WIDTH = 68
  val MIN_WIDTH = 16
  val MAX_WIDTH = 74

  val COLOR_TOLERANCE: Short = 15 //arbitrary value (absolute RGB)
  val BLACK_LIMIT: Short = 30
  val WHITE_LIMIT: Short = 225

  def calculateNewSize(method: Method, lineWidth: Int, originalWidth: Int, originalHeight: Int): (Int, Int) = {
    val width = if (!method.equals(Method.Pwntari)) { lineWidth * 2 } else { lineWidth }
    val height = lineWidth * originalHeight / originalWidth
    (Math.max(1, width), Math.max(1, height))
  }

  def calculateProportionalSize(method: Method, lineWidth: Int, oWidth: Int, oHeight: Int): (Int, Int) = {
    def depend(p: (Int, Int)): (Int, Int) = if (!method.equals(Method.Pwntari)) { (p._1 * 2, p._2) } else { (p._1, p._2) }
    if (oWidth <= lineWidth) {
      val ratio: Int = lineWidth / oWidth
      depend((oWidth * ratio, oHeight * ratio))
    } else {
      val range = ((lineWidth / 2) to lineWidth)
      def calc(oW: Int, oH: Int): (Int, Int) = {
        val wMods = range.filter(oW % _ == 0).toList
        val hMods = range.filter(oH % _ == 0).toList
        val both = wMods.filter(hMods.contains(_)).toList
        if (!both.isEmpty) {
          val divisor = oW / both.last
          depend((oW / divisor, oH / divisor))
        } else {
          if (oW % 2 == 1 || oH % 2 == 1) {
            calc(oW + (oW % 2), oH + (oH % 2))
          } else {
            calculateNewSize(method, lineWidth, oWidth, oHeight)
          }
        }
      }
      calc(oWidth, oHeight)
    }
  }

  def getDefaults(imageRgb: Color.RgbMap): (Option[Method], Scheme, Option[Pal.Charset]) = {
    val grey = imageRgb.map(pixel => isPixelGray(pixel._2)).toList
    val bw = imageRgb.map(pixel => isPixelBlackOrWhite(pixel._2)).toList
    val gRatio: Double = grey.filter(_ == true).length.doubleValue / grey.length.doubleValue
    val bwRatio: Double = bw.filter(_ == true).length.doubleValue / bw.length.doubleValue
    val scheme = if (gRatio > 0.9D) { Scheme.Bwg } else { Scheme.Default }
    if (bwRatio > 0.9D) {
      (Some(Method.Stencil), scheme, Some(Pal.HCrude))
    } else {
      (None, scheme, None)
    }
  }

  private def isPixelGray(rgb: Color.Rgb): Boolean = {
    ((Math.abs(rgb._1 - rgb._2) <= COLOR_TOLERANCE)
      && (Math.abs(rgb._1 - rgb._3) <= COLOR_TOLERANCE)
      && (Math.abs(rgb._2 - rgb._3) <= COLOR_TOLERANCE))
  }

  private def isPixelBlackOrWhite(rgb: Color.Rgb): Boolean = {
    val mean = (rgb._1 + rgb._2 + rgb._3).doubleValue / 3
    mean < BLACK_LIMIT || mean > WHITE_LIMIT
  }

  /**
   * Returns the mean RGB value of all pixels in the image.
   */
  @deprecated("not used for now", "2013-06-30")
  private def getMeanRgb(imageRgb: Color.RgbMap): Int = {
    val means = imageRgb.values.map(rgb => (rgb._1 + rgb._2 + rgb._3) / 3).toList
    means.sum / means.length
  }

  /**
   * Returns the mean RGB value of all pixels in the image.
   */
  @deprecated("not used for now", "2013-06-30")
  private def getMeanDev(imageRgb: Color.RgbMap): Int = {
    val devs = imageRgb.values.map(rgb => ((
      Math.abs(rgb._1 - Color.CENTER) +
      Math.abs(rgb._2 - Color.CENTER) +
      Math.abs(rgb._3 - Color.CENTER)).toDouble / 3D).toInt).toList
    devs.sum / devs.length
  }

}
