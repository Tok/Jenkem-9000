/*
 * #%L
 * ImageUtilSuite.scala - Jenkem - Tok - 2012
 * %%
 * Copyright (C) 2012 - 2013 Lukas Steiger
 *                 <lsteiger4@gmail.com>
 * %%
 * This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar.
 * See http://www.wtfpl.net/ for more details.
 * #L%
 */
package jenkem.util

import org.junit.runner.RunWith
import org.scalatest.PrivateMethodTester.PrivateMethod
import org.scalatest.PrivateMethodTester.anyRefToInvoker
import jenkem.AbstractTester
import jenkem.engine.color.Color
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ImageUtilSuite extends AbstractTester {
  val blackPixel: Color.Rgb = (0, 0, 0)
  val whitePixel: Color.Rgb = (255, 255, 255)
  val grayPixel: Color.Rgb = (127, 127, 127)
  val redPixel: Color.Rgb = (255, 0, 0)
  val darkGreenPixel: Color.Rgb = (0, 127, 0)
  val blackPixelMap: Color.RgbMap = Map((0, 0) -> blackPixel)
  val whitePixelMap: Color.RgbMap = Map((0, 0) -> whitePixel)
  val grayPixelMap: Color.RgbMap = Map((0, 0) -> grayPixel)
  val redPixelMap: Color.RgbMap = Map((0, 0) -> redPixel)
  val darkGreenPixelMap: Color.RgbMap = Map((0, 0) -> darkGreenPixel)

  test("Make Pixel Gray") {
    assert(ImageUtil.makeGreyPixel(blackPixelMap, 0, 0) === 0)
    assert(ImageUtil.makeGreyPixel(whitePixelMap, 0, 0) === 255)
    assert(ImageUtil.makeGreyPixel(grayPixelMap, 0, 0) === 127)
    assert(ImageUtil.makeGreyPixel(redPixelMap, 0, 0) === 85)
    assert(ImageUtil.makeGreyPixel(darkGreenPixelMap, 0, 0) === 42)
    assert(ImageUtil.makeGreyPixel(blackPixelMap, 1, 0) === 0)
    assert(ImageUtil.makeGreyPixel(blackPixelMap, 0, 1) === 0)
  }

  test("Pixel Sum") {
    val getPixelRgbSum = PrivateMethod[Short]('getPixelRgbSum)
    assert(ImageUtil.invokePrivate(getPixelRgbSum(blackPixelMap, 0, 0)) === 0)
    assert(ImageUtil.invokePrivate(getPixelRgbSum(whitePixelMap, 0, 0)) === 765)
    assert(ImageUtil.invokePrivate(getPixelRgbSum(grayPixelMap, 0, 0)) === 381)
    assert(ImageUtil.invokePrivate(getPixelRgbSum(redPixelMap, 0, 0)) === 255)
    assert(ImageUtil.invokePrivate(getPixelRgbSum(darkGreenPixelMap, 0, 0)) === 127)
    assert(ImageUtil.invokePrivate(getPixelRgbSum(blackPixelMap, 1, 0)) === 0)
    assert(ImageUtil.invokePrivate(getPixelRgbSum(whitePixelMap, 1, 0)) === 0)
    assert(ImageUtil.invokePrivate(getPixelRgbSum(whitePixelMap, 0, 1)) === 0)
    assert(ImageUtil.invokePrivate(getPixelRgbSum(whitePixelMap, 1, 1)) === 0)
    assert(ImageUtil.invokePrivate(getPixelRgbSum(whitePixelMap, 10, 10)) === 0)
  }

  test("Get Pixel") {
    assert(ImageUtil.getPixels(blackPixelMap, 0, 0) === blackPixel)
    assert(ImageUtil.getPixels(whitePixelMap, 0, 0) === whitePixel)
    assert(ImageUtil.getPixels(grayPixelMap, 0, 0) === grayPixel)
    assert(ImageUtil.getPixels(redPixelMap, 0, 0) === redPixel)
    assert(ImageUtil.getPixels(darkGreenPixelMap, 0, 0) === darkGreenPixel)
    assert(ImageUtil.getPixels(blackPixelMap, 1, 0) === blackPixel)
    assert(ImageUtil.getPixels(whitePixelMap, 1, 0) === blackPixel)
    assert(ImageUtil.getPixels(whitePixelMap, 0, 1) === blackPixel)
    assert(ImageUtil.getPixels(whitePixelMap, 1, 1) === blackPixel)
  }
}
