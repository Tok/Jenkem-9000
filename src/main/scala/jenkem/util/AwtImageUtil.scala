/*
 * #%L
 * AwtImageUtil.scala - Jenkem - Tok - 2012
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

import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.awt.image.RescaleOp
import java.awt.image.LookupOp
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL
import scala.Array.canBuildFrom
import com.vaadin.server.Resource
import com.vaadin.server.StreamResource
import javax.imageio.ImageIO
import jenkem.engine.Kick
import sun.misc.BASE64Decoder
import sun.misc.BASE64Encoder
import java.util.Date
import java.awt.RenderingHints
import jenkem.engine.Method
import java.awt.image.ShortLookupTable
import jenkem.engine.color.Color

object AwtImageUtil {
  type Crops = (Int, Int, Int, Int)
  val iconSize = 32
  val defaultCrops = (0, 100, 0, 100) //xs, xe, ys, ye
  val colorWhite = new java.awt.Color(255, 255, 255)
  val colorBlack = new java.awt.Color(0, 0, 0)

  def bufferImage(url: String, bg: String, invert: Boolean): BufferedImage = {
    bufferImage(url, bg, invert, defaultCrops)
  }

  def bufferImage(url: String, bg: String, invert: Boolean, crops: Crops): BufferedImage = {
    val color = if (bg.equalsIgnoreCase("white")) {
      if (invert) { colorBlack } else { colorWhite }
    } else {
      if (invert) { colorWhite } else { colorBlack }
    }
    val buffered = doBuffer(url, bg, color, crops)
    if (invert) { new RescaleOp(-1F, 255F, None.orNull).filter(buffered, None.orNull) }
    else { buffered }
  }

  private def doBuffer(url: String, bg: String, color: java.awt.Color, crops: Crops): BufferedImage = {
    val image = ImageIO.read(new URL(url))
    val xs = crops._1
    val xe = crops._2
    val ys = crops._3
    val ye = crops._4
    val cropX = (image.getWidth * xs) / 100
    val cropY = (image.getHeight * ys) / 100
    val cropW = (image.getWidth * (xe - xs)) / 100
    val cropH = (image.getHeight * (ye - ys)) / 100
    val buffered = new BufferedImage(cropW, cropH, BufferedImage.TYPE_INT_RGB)
    val g2d = buffered.getGraphics.asInstanceOf[Graphics2D]
    g2d.setBackground(color)
    g2d.clearRect(0, 0, cropW, cropH)
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF)
    val subimage = image.getSubimage(cropX, cropY, cropW, cropH)
    g2d.drawImage(subimage, None.orNull, None.orNull)
    g2d.dispose
    buffered
  }

  def getRgb(img: BufferedImage, x: Int, y: Int): Color.Rgb = {
    val argb = img.getRGB(x.intValue, y.intValue)
    (((argb >> 16) & 0xff).toShort, ((argb >> 8) & 0xff).toShort, ((argb) & 0xff).toShort)
  }

  def getImageRgb(scaled: BufferedImage): Color.RgbMap = {
    val keys = for {
       y <- 0 until scaled.getHeight
       x <- 0 until scaled.getWidth
    } yield { (y, x) }
    keys.map(t => ((t._1, t._2), getRgb(scaled, t._2, t._1))).toMap
  }

  def makeIcon(url: String, bg: String, invert: Boolean, crops: Crops): BufferedImage = {
    val buffered = bufferImage(url, bg, invert, crops)
    getScaled(buffered, iconSize, iconSize)
  }

  def makeVaadinResource(img: BufferedImage, name: String): Resource = {
    class IconSource extends StreamResource.StreamSource {
      def getStream(): InputStream = {
        val imagebuffer = new ByteArrayOutputStream
        ImageIO.write(img, "png", imagebuffer)
        new ByteArrayInputStream(imagebuffer.toByteArray)
      }
    }
    new StreamResource(new IconSource, name + ".png?" + new Date().getTime())
  }

  def encodeToBase64(image: BufferedImage): String = {
    val baos = new ByteArrayOutputStream
    ImageIO.write(image, "png", baos)
    val encoder = new BASE64Encoder
    encoder.encode(baos.toByteArray)
  }

  def decodeFromBase64(base64: String): BufferedImage = {
    val decoder = new BASE64Decoder
    val bytes = decoder.decodeBuffer(base64)
    val scalaBytes: Array[Byte] = bytes.map(b => b.byteValue)
    ImageIO.read(new ByteArrayInputStream(scalaBytes))
  }

  def getScaled(img: BufferedImage, width: Int, height: Int): BufferedImage = {
    getScaled(img, width, height, Kick.OFF, 0, 0)
  }

  def getScaled(img: BufferedImage, width: Int, height: Int, kick: Kick, b: Int, c: Int): BufferedImage = {
    val resized = new BufferedImage(width, height, img.getType)
    val g2d = resized.createGraphics
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF)
    g2d.drawImage(img,
        0, 0, width, height,
        0, 0, img.getWidth, img.getHeight,
        None.orNull)
    g2d.dispose
    val wx = resized.getWidth - (2 * kick.xOffset)
    val hx = resized.getHeight - (2 * kick.yOffset)
    val sub = resized.getSubimage(kick.xOffset, kick.yOffset, wx, hx)
    new LookupOp(brightnessLot(b), None.orNull).filter(sub, sub)
    new LookupOp(contrastLot(c), None.orNull).filter(sub, sub)
    sub
  }

  private def brightnessLot(b: Int): ShortLookupTable = {
    def getValue(v: Int): Short = Math.max(0, Math.min(255, v + b)).toShort
    val table: Array[Short] = (0 to 255).map(getValue(_)).toArray
    new ShortLookupTable(0, table)
  }

  private def contrastLot(c: Int): ShortLookupTable = {
    val cf = (c + 100F).toFloat / 100F
    def getValue(v: Int): Short = Math.max(0, Math.min(255, v * cf)).toShort
    val table: Array[Short] = (0 to 255).map(getValue(_)).toArray
    new ShortLookupTable(0, table)
  }
}
