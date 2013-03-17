package jenkem.util

import jenkem.engine.ConversionMethod
import jenkem.engine.Pal
import jenkem.engine.color.Scheme

object InitUtil {
  val AVERAGE_RGB = 127
  val COLOR_TOLERANCE = 15 //arbitrary value (absolute RGB)
  val MIN_BRIGHTNESS = -20
  val MAX_BRIGHTNESS = 30
  val MIN_CONTRAST = -5
  val MAX_CONTRAST = 30

  /**
   * Returns the default brightness based on the number of black and white pixels
   * compared to the total pixel count in the image.
   */
  def getDefaultBrightness(imageRgb: Map[(Int, Int), (Short, Short, Short)]): Int = {
    val mean = getMeanRgb(imageRgb)
    Math.min(MAX_BRIGHTNESS, Math.max(MIN_BRIGHTNESS, AVERAGE_RGB - mean))
  }

  /**
   * Returns the default contrast based on the mean of the estimated
   * distance of the pixels from the center of the color space cube.
   */
  def getDefaultContrast(imageRgb: Map[(Int, Int), (Short, Short, Short)]): Int = {
    //bigger value --> the more contrast --> reduce
    val dev = getMeanDev(imageRgb) // 0 < dev < 127
    val diff = (AVERAGE_RGB / 2) - dev
    Math.min(MAX_CONTRAST, Math.max(MIN_CONTRAST, diff))
  }

  def getDefaults(imageRgb: Map[(Int, Int), (Short, Short, Short)]):
      (ConversionMethod.Value, Scheme.Value, Pal.Charset) = {
    val grey = imageRgb.map(pixel => isPixelGrey(pixel._2)).toList
    val bw = imageRgb.map(pixel => isPixelBlackOrWhite(pixel._2)).toList
    val gRatio: Double = grey.filter(_ == true).length.doubleValue / grey.length.doubleValue
    val bwRatio: Double = bw.filter(_ == true).length.doubleValue / bw.length.doubleValue
    val scheme = if (gRatio > 0.9D) { Scheme.Bwg } else { Scheme.Default }
    if (bwRatio > 0.9D) { (ConversionMethod.Stencil, scheme, Pal.HCrude) }
    else { (ConversionMethod.Vortacular, scheme, Pal.Ansi) }
  }

  private def isPixelGrey(rgb: (Short, Short, Short)): Boolean = {
      (!(Math.abs(rgb._1 - rgb._2) > COLOR_TOLERANCE)
    && !(Math.abs(rgb._2 - rgb._3) > COLOR_TOLERANCE)
    && !(Math.abs(rgb._3 - rgb._1) > COLOR_TOLERANCE))
  }

  private def isPixelBlackOrWhite(rgb: (Short, Short, Short)): Boolean = {
    val mean = (rgb._1 + rgb._2 + rgb._3).doubleValue / 3
    mean < 30 || mean > 225
  }

  /**
   * Returns the mean RGB value of all pixels in the image.
   */
  private def getMeanRgb(imageRgb: Map[(Int, Int), (Short, Short, Short)]): Int = {
    val means = imageRgb.values.map(rgb => (rgb._1 + rgb._2 + rgb._3) / 3).toList
    means.sum / means.length
  }

  /**
   * Returns the mean RGB value of all pixels in the image.
   */
  private def getMeanDev(imageRgb: Map[(Int, Int), (Short, Short, Short)]): Int = {
    val devs = imageRgb.values.map(rgb => (
        Math.abs(rgb._1 - AVERAGE_RGB) +
        Math.abs(rgb._2 - AVERAGE_RGB) +
        Math.abs(rgb._3 - AVERAGE_RGB)) / 3).toList
    devs.sum / devs.length
  }

}
