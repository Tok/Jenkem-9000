package jenkem.util

import jenkem.engine.Method
import jenkem.engine.Pal
import jenkem.engine.color.Scheme
import jenkem.engine.color.Color

object InitUtil {
  val COLOR_TOLERANCE = 15 //arbitrary value (absolute RGB)
  val MIN_BRIGHTNESS = -20
  val MAX_BRIGHTNESS = 10
  val MIN_CONTRAST = -5
  val MAX_CONTRAST = 20

  /**
   * Returns the default brightness based on the number of black and white pixels
   * compared to the total pixel count in the image.
   */
  @deprecated("Not used anymore", "2013-06-27")
  def getDefaultAsciiBrightness(imageRgb: Color.RgbMap): Int = {
    val mean = getMeanRgb(imageRgb)
    Math.min(MAX_BRIGHTNESS, Math.max(MIN_BRIGHTNESS, Color.CENTER - mean))
  }

  /**
   * Returns the default contrast based on the mean of the estimated
   * distance of the pixels from the center of the color space cube.
   */
  @deprecated("Not used anymore", "2013-06-27")
  def getDefaultAsciiContrast(imageRgb: Color.RgbMap): Int = {
    //bigger value --> the more contrast --> reduce
    val dev = getMeanDev(imageRgb) // 0 < dev < 127
    val diff = (Color.CENTER / 2) - dev
    Math.min(MAX_CONTRAST, Math.max(MIN_CONTRAST, diff))
  }

  def getDefaults(imageRgb: Color.RgbMap):
      (Option[Method], Scheme, Option[Pal.Charset]) = {
    val grey = imageRgb.map(pixel => isPixelGray(pixel._2)).toList
    val bw = imageRgb.map(pixel => isPixelBlackOrWhite(pixel._2)).toList
    val gRatio: Double = grey.filter(_ == true).length.doubleValue / grey.length.doubleValue
    val bwRatio: Double = bw.filter(_ == true).length.doubleValue / bw.length.doubleValue
    val scheme = if (gRatio > 0.9D) { Scheme.Bwg } else { Scheme.Default }
    if (bwRatio > 0.9D) {
      (Some(Method.Stencil), scheme, Some(Pal.HCrude)) }
    else {
      (None, scheme, None)
    }
  }

  private def isPixelGray(rgb: Color.Rgb): Boolean = {
      (!(Math.abs(rgb._1 - rgb._2) > COLOR_TOLERANCE)
    && !(Math.abs(rgb._2 - rgb._3) > COLOR_TOLERANCE)
    && !(Math.abs(rgb._3 - rgb._1) > COLOR_TOLERANCE))
  }

  private def isPixelBlackOrWhite(rgb: Color.Rgb): Boolean = {
    val mean = (rgb._1 + rgb._2 + rgb._3).doubleValue / 3
    mean < 30 || mean > 225
  }

  /**
   * Returns the mean RGB value of all pixels in the image.
   */
  private def getMeanRgb(imageRgb: Color.RgbMap): Int = {
    val means = imageRgb.values.map(rgb => (rgb._1 + rgb._2 + rgb._3) / 3).toList
    means.sum / means.length
  }

  /**
   * Returns the mean RGB value of all pixels in the image.
   */
  private def getMeanDev(imageRgb: Color.RgbMap): Int = {
    val devs = imageRgb.values.map(rgb => ((
            Math.abs(rgb._1 - Color.CENTER) +
            Math.abs(rgb._2 - Color.CENTER) +
            Math.abs(rgb._3 - Color.CENTER)).toDouble / 3D).toInt).toList
    devs.sum / devs.length
  }

}
