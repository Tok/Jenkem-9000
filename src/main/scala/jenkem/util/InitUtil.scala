/*
 * #%L
 * InitUtil.scala - Jenkem - Tok - 2012
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
    def depend(p: (Int, Int)): (Int, Int) = {
      if (!method.equals(Method.Pwntari)) { (Math.max(1, p._1 * 2), Math.max(1, p._2)) }
      else { (Math.max(1, p._1), Math.max(1, p._2)) }
    }
    if (oWidth <= lineWidth) {
      val ratio: Int = lineWidth / oWidth
      depend((oWidth * ratio, oHeight * ratio))
    } else {
      val range = ((lineWidth / 2) to lineWidth)
      val wMods = range.filter(oWidth % _ == 0)
      val hMods = range.filter(oHeight % _ == 0)
      val both = wMods.filter(hMods.contains(_))
      if (!both.isEmpty) {
        val divisor = oWidth / both.last
        depend((oWidth / divisor, oHeight / divisor))
      } else if (!wMods.isEmpty) {
        val divisor = oWidth / wMods.last
        depend((oWidth / divisor, oHeight / divisor))
      } else {
        calculateNewSize(method, lineWidth, oWidth, oHeight)
      }
    }
  }

  def getDefaults(imageRgb: Color.RgbMap): (Option[Method], Scheme, Option[Pal.Charset]) = {
    val grey = imageRgb.map(pixel => isPixelGray(pixel._2)).toList
    val bw = imageRgb.map(pixel => isPixelBlackOrWhite(pixel._2)).toList
    val gRatio: Double = grey.filter(_ == true).length.doubleValue / grey.length.doubleValue
    val bwRatio: Double = bw.filter(_ == true).length.doubleValue / bw.length.doubleValue
    val scheme = if (gRatio > 0.9D) { Scheme.Bwg } else { Scheme.default }
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
}
