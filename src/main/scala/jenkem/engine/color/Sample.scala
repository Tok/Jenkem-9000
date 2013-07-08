/*
 * #%L
 * Sample.scala - Jenkem - Tok - 2012
 * %%
 * Copyright (C) 2012 - 2013 Lukas Steiger <lsteiger4@gmail.com>
 * %%
 * This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar.
 * See http://www.wtfpl.net/ for more details.
 * #L%
 */
package jenkem.engine.color

import jenkem.util.ImageUtil

object Sample {
  type Grey = (Short, Short, Short, Short)
  type Colored = (Color.Rgb, Color.Rgb, Color.Rgb, Color.Rgb)

  abstract class Dir
  sealed abstract class Ydir extends Dir
  case object TOP extends Ydir
  case object BOT extends Ydir
  sealed abstract class Xdir extends Dir
  case object LEFT extends Xdir
  case object RIGHT extends Xdir
  val dirs: List[Dir] = List(TOP, BOT, LEFT, RIGHT)

  def makeGreySample(imageRgb: Color.RgbMap, x: Int, y: Int, c: Int, b: Int): Grey = {
    val topLeft = calcCol(ImageUtil.makeGreyPixel(imageRgb, x, y), c, b)
    val topRight = calcCol(ImageUtil.makeGreyPixel(imageRgb, x + 1, y), c, b)
    val bottomLeft = calcCol(ImageUtil.makeGreyPixel(imageRgb, x, y + 1), c, b)
    val bottomRight = calcCol(ImageUtil.makeGreyPixel(imageRgb, x + 1, y + 1), c, b)
    (topLeft, topRight, bottomLeft, bottomRight)
  }

  //TL, TR, BL, BR
  def makeColorSample(imageRgb: Color.RgbMap, x: Int, y: Int, c: Int, b: Int): Colored = {
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
    def calcDiff(first: Color.Rgb, second: Color.Rgb): Short = {
      val redDiff = Math.abs(first._1 - second._1)
      val greenDiff = Math.abs(first._2 - second._2)
      val blueDiff = Math.abs(first._3 - second._3)
      ((redDiff + greenDiff + blueDiff) / 3).shortValue
    }
    def calcHorRgbDiff(sample: Colored, xDir: Xdir): Short = {
      val xTop = get[Color.Rgb](sample, xDir, TOP)
      val xBot = get[Color.Rgb](sample, xDir, BOT)
      calcDiff(xTop, xBot)
    }
    def calcVertRgbDiff(sample: Colored, yDir: Ydir): Short = {
      val yLeft = get[Color.Rgb](sample, LEFT, yDir)
      val yRight = get[Color.Rgb](sample, RIGHT, yDir)
      calcDiff(yLeft, yRight)
    }
    dir match {
      case TOP => calcVertRgbDiff(sample, TOP)
      case BOT => calcVertRgbDiff(sample, BOT)
      case LEFT => calcHorRgbDiff(sample, LEFT)
      case RIGHT => calcHorRgbDiff(sample, RIGHT)
    }
  }

  def calcRgbMean(sample: Colored, dir: Dir): Color.Rgb = {
    def calcHorRgbMean(sample: Colored, xDir: Xdir): Color.Rgb = {
      val xTop = get[Color.Rgb](sample, xDir, TOP)
      val xBot = get[Color.Rgb](sample, xDir, BOT)
      calcMean(xTop, xBot)
    }
    def calcVertRgbMean(sample: Colored, yDir: Ydir): Color.Rgb = {
      val yLeft = get[Color.Rgb](sample, LEFT, yDir)
      val yRight = get[Color.Rgb](sample, RIGHT, yDir)
      calcMean(yLeft, yRight)
    }
    dir match {
      case TOP => calcVertRgbMean(sample, TOP)
      case BOT => calcVertRgbMean(sample, BOT)
      case LEFT => calcHorRgbMean(sample, LEFT)
      case RIGHT => calcHorRgbMean(sample, RIGHT)
    }
  }

  def calcCol(input: Short, contrast: Int, brightness: Int): Short = {
    keepInRange(correctDistance(input, contrast) + brightness.shortValue).shortValue
  }

  def getDirectedGrey(sample: Grey, dir: Dir): Short = {
    val sum = dir match {
      case TOP => get[Short](sample, LEFT, TOP) + get[Short](sample, RIGHT, TOP)
      case BOT => get[Short](sample, LEFT, BOT) + get[Short](sample, RIGHT, BOT)
      case LEFT => get[Short](sample, LEFT, TOP) + get[Short](sample, LEFT, BOT)
      case RIGHT => get[Short](sample, RIGHT, TOP) + get[Short](sample, RIGHT, BOT)
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

  def getAllRgb(col: Colored): Color.Rgb = {
    val r = ((col._1._1 + col._2._1 + col._3._1 + col._4._1) / 4).shortValue
    val g = ((col._1._2 + col._2._2 + col._3._2 + col._4._2) / 4).shortValue
    val b = ((col._1._3 + col._2._3 + col._3._3 + col._4._3) / 4).shortValue
    (r, g, b)
  }

  def calcMean(first: Color.Rgb, second: Color.Rgb): Color.Rgb = {
    val redMean = (first._1 + second._1) / 2
    val greenMean = (first._2 + second._2) / 2
    val blueMean = (first._3 + second._3) / 2
    (redMean.shortValue, greenMean.shortValue, blueMean.shortValue)
  }

  private def correctDistance(input: Int, contrast: Int): Int = {
    val distanceFromCenter = Math.abs(input - Color.CENTER)
    val contrastedDist: Double = distanceFromCenter * (1D + (contrast.doubleValue / 100))
    val result = if (input < Color.CENTER) { Color.CENTER - contrastedDist } else { Color.CENTER + contrastedDist }
    result.intValue
  }

  def getMeanGrey(grey: Grey): Double = (grey._1 + grey._2 + grey._3 + grey._4) / 4D

  private def keepInRange(colorComponent: Int): Int = Math.max(0, Math.min(colorComponent, Color.MAX))
}
