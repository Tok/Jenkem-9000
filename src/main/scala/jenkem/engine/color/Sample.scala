package jenkem.engine.color

import jenkem.util.ImageUtil

object Sample {
  val MAX_RGB: Short = 255
  val HALF_RGB: Short = 127

  type Coords = (Int, Int)
  type Rgb = (Short, Short, Short)
  type Grey = (Short, Short, Short, Short)
  type Colored = (Rgb, Rgb, Rgb, Rgb)

  abstract class Dir
  sealed abstract class Ydir extends Dir
  case object TOP extends Ydir
  case object BOT extends Ydir
  sealed abstract class Xdir extends Dir
  case object LEFT extends Xdir
  case object RIGHT extends Xdir
  val dirs: List[Dir] = List(TOP, BOT, LEFT, RIGHT)

  def makeGreySample(imageRgb: Map[Coords, Rgb], x: Int, y: Int, c: Int, b: Int): Grey = {
    val topLeft = calcCol(ImageUtil.makeGreyPixel(imageRgb, x, y), c, b)
    val topRight = calcCol(ImageUtil.makeGreyPixel(imageRgb, x + 1, y), c, b)
    val bottomLeft = calcCol(ImageUtil.makeGreyPixel(imageRgb, x, y + 1), c, b)
    val bottomRight = calcCol(ImageUtil.makeGreyPixel(imageRgb, x + 1, y + 1), c, b)
    (topLeft, topRight, bottomLeft, bottomRight)
  }

  //TL, TR, BL, BR
  def makeColorSample(imageRgb: Map[Coords, Rgb], x: Int, y: Int, c: Int, b: Int): Colored = {
    val tl = ImageUtil.getPixels(imageRgb, x, y)
    val tr = ImageUtil.getPixels(imageRgb, x + 1, y)
    val bl = ImageUtil.getPixels(imageRgb, x, y + 1)
    val br = ImageUtil.getPixels(imageRgb, x + 1, y + 1)
    ((calcCol(tl._1, c, b), calcCol(tl._2, c, b), calcCol(tl._3, c, b)),
     (calcCol(tr._1, c, b), calcCol(tr._2, c, b), calcCol(tr._3, c, b)),
     (calcCol(bl._1, c, b), calcCol(bl._2, c, b), calcCol(bl._3, c, b)),
     (calcCol(br._1, c, b), calcCol(br._2, c, b), calcCol(br._3, c, b)))
  }

  def calcRgbDiff(sample: Colored, dir: Dir): Short = {
    def calcDiff(first: Rgb, second: Rgb): Short = {
      val redDiff = Math.abs(first._1 - second._1)
      val greenDiff = Math.abs(first._2 - second._2)
      val blueDiff = Math.abs(first._3 - second._3)
      ((redDiff + greenDiff + blueDiff) / 3).shortValue
    }
    def calcHorRgbDiff(sample: Colored, xDir: Xdir): Short = {
      val xTop = getRgb(sample, xDir, TOP)
      val xBot = getRgb(sample, xDir, BOT)
      calcDiff(xTop, xBot)
    }
    def calcVertRgbDiff(sample: Colored, yDir: Ydir): Short = {
      val yLeft = getRgb(sample, LEFT, yDir)
      val yRight = getRgb(sample, RIGHT, yDir)
      calcDiff(yLeft, yRight)
    }
    dir match {
      case LEFT => calcHorRgbDiff(sample, LEFT)
      case RIGHT => calcHorRgbDiff(sample, RIGHT)
      case TOP => calcVertRgbDiff(sample, TOP)
      case BOT => calcVertRgbDiff(sample, BOT)
    }
  }

  def calcRgbMean(sample: Colored, dir: Dir): Rgb = {
    def calcHorRgbMean(sample: Colored, xDir: Xdir): Rgb = {
      val xTop = getRgb(sample, xDir, TOP)
      val xBot = getRgb(sample, xDir, BOT)
      calcMean(xTop, xBot)
    }
    def calcVertRgbMean(sample: Colored, yDir: Ydir): Rgb = {
      val yLeft = getRgb(sample, LEFT, yDir)
      val yRight = getRgb(sample, RIGHT, yDir)
      calcMean(yLeft, yRight)
    }
    dir match {
      case LEFT => calcHorRgbMean(sample, LEFT)
      case RIGHT => calcHorRgbMean(sample, RIGHT)
      case TOP => calcVertRgbMean(sample, TOP)
      case BOT => calcVertRgbMean(sample, BOT)
    }
  }

  def calcCol(input: Short, contrast: Int, brightness: Int): Short = {
    keepInRange(correctDistance(input, contrast) + brightness.shortValue).shortValue
  }

  def getDirectedGrey(sample: Grey, dir: Dir): Short = {
    val sum = dir match {
      case TOP => getGrey(sample, LEFT, TOP) + getGrey(sample, RIGHT, TOP)
      case BOT => getGrey(sample, LEFT, BOT) + getGrey(sample, RIGHT, BOT)
      case LEFT => getGrey(sample, LEFT, TOP) + getGrey(sample, LEFT, BOT)
      case RIGHT => getGrey(sample, RIGHT, TOP) + getGrey(sample, RIGHT, BOT)
    }
    (sum / 2).shortValue
  }

  def get[T](sample: (T, T, T, T), xDir: Xdir, yDir: Ydir): T = {
    yDir match {
      case TOP => xDir match {
        case LEFT => sample._1
        case RIGHT => sample._2
      }
      case BOT => xDir match {
        case LEFT => sample._3
         case RIGHT => sample._4
      }
    }
  }

  def getAllRgb(col: Colored): Rgb = {
    val r = ((col._1._1 + col._2._1 + col._3._1 + col._4._1) / 4).shortValue
    val g = ((col._1._2 + col._2._2 + col._3._2 + col._4._2) / 4).shortValue
    val b = ((col._1._3 + col._2._3 + col._3._3 + col._4._3) / 4).shortValue
    (r, g, b)
  }

  private def calcMean(first: Rgb, second: Rgb): Rgb = {
    val redMean = (first._1 + second._1) / 2
    val greenMean = (first._2 + second._2) / 2
    val blueMean = (first._3 + second._3) / 2
    (redMean.shortValue, greenMean.shortValue, blueMean.shortValue)
  }

  private def correctDistance(input: Int, contrast: Int): Int = {
    val distanceFromCenter = if (input < HALF_RGB) { HALF_RGB - input } else { input - HALF_RGB }
    val contrastedDist: Double = distanceFromCenter * (1D + (contrast.doubleValue / 100))
    val result = if (input < HALF_RGB) { HALF_RGB - contrastedDist } else { HALF_RGB + contrastedDist }
    result.intValue
  }

  def getMeanGrey(grey: Grey): Double = (grey._1 + grey._2 + grey._3 + grey._4) / 4D

  private def getRgb(sample: Colored, xDir: Xdir, yDir: Ydir): (Short, Short, Short) = get[Rgb](sample, xDir, yDir)
  private def getGrey(sample: Grey, xDir: Xdir, yDir: Ydir): Short = get[Short](sample, xDir, yDir)
  private def keepInRange(colorComponent: Int): Int = Math.max(0, Math.min(colorComponent, MAX_RGB))
}
