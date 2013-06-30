package jenkem.util

import jenkem.engine.color.Color

object ImageUtil {
  def makeGreyPixel(imageRgb: Color.RgbMap, x: Int, y: Int): Short = {
    (getPixelRgbSum(imageRgb, x, y) / 3).shortValue
  }

  private def getPixelRgbSum(imageRgb: Color.RgbMap, x: Int, y: Int): Short = {
    imageRgb.get((y, x)) match {
      case Some(rgb) => (rgb._1 + rgb._2 + rgb._3).shortValue
      case _ => 0
    }
  }

  def getPixels(imageRgb: Color.RgbMap, x: Int, y: Int): Color.Rgb = {
    imageRgb.get((y, x)) match {
      case Some(rgb) => (rgb._1, rgb._2, rgb._3)
      case _ => (0, 0, 0)
    }
  }
}
