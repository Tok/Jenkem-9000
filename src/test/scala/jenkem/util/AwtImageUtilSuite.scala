/*
 * #%L
 * AwtImageUtilSuite.scala - Jenkem - Tok - 2012
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

import java.awt.image.BufferedImage
import org.junit.runner.RunWith
import com.vaadin.server.StreamResource
import jenkem.AbstractTester
import jenkem.engine.Kick
import org.scalatest.junit.JUnitRunner
import java.net.URL
import java.io.IOException
import java.net.Socket
import java.net.ServerSocket
import org.mortbay.jetty.Server

@RunWith(classOf[JUnitRunner])
class AwtImageUtilSuite extends AbstractTester {
  val blackImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB)
  val base64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAIAAACQd1PeAAAADElEQVR42mNgYGAAAAAEAAHI6uv5AAAAAElFTkSuQmCC"

  test("Constants") {
    assert(AwtImageUtil.iconSize === 32)
    assert(AwtImageUtil.defaultCrops === (0, 100, 0, 100))
    assert(AwtImageUtil.colorWhite === new java.awt.Color(255, 255, 255))
    assert(AwtImageUtil.colorBlack === new java.awt.Color(0, 0, 0))
  }

  test("Buffered Image RGB") {
    val img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB)
    assert(AwtImageUtil.getRgb(blackImg, 0, 0) === (0, 0, 0))
    val e = intercept[ArrayIndexOutOfBoundsException] {
      AwtImageUtil.getRgb(blackImg, 0, 1)
    }
    assert(e.isInstanceOf[ArrayIndexOutOfBoundsException])
  }

  test("Scaled Image RGB") {
    val colorMap = AwtImageUtil.getImageRgb(blackImg)
    assert(colorMap.size === 1)
    assert(colorMap.get((0, 0)) === Some((0, 0, 0)))
    assert(colorMap.get((1, 0)) === None)
  }

  test("Make Vaadin Resource") {
    val name = "name"
    val resource = AwtImageUtil.makeVaadinResource(blackImg, name)
    assert(resource.isInstanceOf[StreamResource])
    assert(resource.getMIMEType.equalsIgnoreCase("image/png"))
    val biggerArgbImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB)
    val biggerResource = AwtImageUtil.makeVaadinResource(biggerArgbImage, name)
    assert(biggerResource.isInstanceOf[StreamResource])
    assert(biggerResource.getMIMEType.equalsIgnoreCase("image/png"))
  }

  test("Make Icon") {
    val url = "http://127.0.0.1/fail"
    val crops = (0, 0, 1, 1)
    intercept[javax.imageio.IIOException] { AwtImageUtil.makeIcon(url, "black", false, crops) }
    intercept[javax.imageio.IIOException] { AwtImageUtil.makeIcon(url, "black", true, crops) }
    intercept[javax.imageio.IIOException] { AwtImageUtil.makeIcon(url, "white", false, crops) }
    intercept[javax.imageio.IIOException] { AwtImageUtil.makeIcon(url, "white", true, crops) }
  }

  test("Base 64 Encoding") {
    val encoded = AwtImageUtil.encodeToBase64(blackImg)
    assert(encoded.isInstanceOf[String])
  }

  test("Base 64 Decoding") {
    val decoded = AwtImageUtil.decodeFromBase64(base64)
    assert(decoded.isInstanceOf[BufferedImage])
  }

  test("Scaling") {
    val scaled = AwtImageUtil.getScaled(blackImg, 10, 10)
    assert(scaled.getWidth === 10)
    assert(scaled.getHeight === 10)
  }

  test("Scaling With Brightness And Contrast") {
    val brightness = 0
    val contrast = 1
    val scaled = AwtImageUtil.getScaled(blackImg, 10, 10, Kick.OFF, brightness, contrast)
    assert(scaled.getWidth === 10)
    assert(scaled.getHeight === 10)
  }
}
