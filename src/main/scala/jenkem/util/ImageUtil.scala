/*
 * #%L
 * ImageUtil.scala - Jenkem - Tok - 2012
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

import jenkem.engine.color.Color

object ImageUtil {
  def makeGreyPixel(imageRgb: Color.RgbMap, x: Int, y: Int): Short =
    (getPixelRgbSum(imageRgb, x, y) / 3).shortValue

  private def getPixelRgbSum(imageRgb: Color.RgbMap, x: Int, y: Int): Short =
    imageRgb.get((y, x)) match {
      case Some(rgb) => (rgb._1 + rgb._2 + rgb._3).shortValue
      case _ => 0.shortValue
    }

  def getPixels(imageRgb: Color.RgbMap, x: Int, y: Int): Color.Rgb =
    imageRgb.get((y, x)) match {
      case Some(rgb) => (rgb._1, rgb._2, rgb._3)
      case _ => (0.shortValue, 0.shortValue, 0.shortValue)
    }
}
