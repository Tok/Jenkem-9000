package jenkem.util

object ImageUtil {

  def makeGreyPixel(imageRgb: Map[(Int, Int), (Short, Short, Short)], x: Int, y: Int): Short = {
    (getPixelRgbSum(imageRgb, x, y) / 3).shortValue
  }

  private def getPixelRgbSum(imageRgb: Map[(Int, Int), (Short, Short, Short)], x: Int, y: Int): Short = {
    imageRgb.get((y, x)) match {
      case Some((r, g, b)) => (r + g + b).shortValue
      case None => 0
    }
  }

  def getPixels(imageRgb: Map[(Int, Int), (Short, Short, Short)], x: Int, y: Int): (Short, Short, Short) = {
    imageRgb.get((y, x)) match {
      case Some((r, g, b)) => (r, g, b)
      case None => (0, 0, 0)
    }
  }
}
