package jenkem.util

import java.awt.Color
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.awt.image.RescaleOp
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

object AwtImageUtil {
  //scalastyle: NullChecker and IllegalImportsChecker warnings
  //may be ignored for this object because awt is used.

  val iconSize = 32
  val defaultCrops = (0, 100, 0, 100) //xs, xe, ys, ye
  val colorWhite = new Color(255, 255, 255)
  val colorBlack = new Color(0, 0, 0)

  def bufferImage(url: String, bg: String, invert: Boolean): BufferedImage = {
    bufferImage(url, bg, invert, defaultCrops)
  }
  def bufferImage(url: String, bg: String, invert: Boolean, crops: (Int, Int, Int, Int)): BufferedImage = {
    val color = if (bg.equalsIgnoreCase("white")) {
      if (invert) { colorBlack } else { colorWhite }
    } else {
      if (invert) { colorWhite } else { colorBlack }
    }
    val buffered = doBuffer(url, bg, color, crops)
    if (invert) { new RescaleOp(-1F, 255F, null).filter(buffered, null) }
    else { buffered }
  }
  private def doBuffer(url: String, bg: String, color: Color, crops: (Int, Int, Int, Int)): BufferedImage = {
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
    buffered.getGraphics match {
      case g2d: Graphics2D =>
        g2d.setBackground(color)
        g2d.clearRect(0, 0, cropW, cropH)
        //val transform: AffineTransform = g2d.getTransform
        val subimage = image.getSubimage(cropX, cropY, cropW, cropH)
        g2d.drawImage(subimage, null, null)
        g2d.dispose
    }
    buffered
  }
  def getRgb(img: BufferedImage, x: Int, y: Int): (Short, Short, Short) = {
    val argb = img.getRGB(x.intValue, y.intValue)
    (((argb >> 16) & 0xff).toShort, ((argb >> 8) & 0xff).toShort, ((argb) & 0xff).toShort)
  }
  def getImageRgb(img: BufferedImage, width: Int, height: Int, kick: Kick): Map[(Int, Int), (Short, Short, Short)] = {
    val xo = kick.xOffset
    val yo = kick.yOffset
    val keys = for {
       y <- 0 until height - (2 * yo)
       x <- 0 until width - (2 * xo)
    } yield { (y, x) }
    val scaled = resize(img, width, height)
    keys.map(t => ((t._1, t._2), getRgb(scaled, t._2 + xo, t._1 + yo))).toMap
  }
  def calculateNewSize(lineWidth: Int, originalWidth: Int, originalHeight: Int): (Int, Int) = {
    val width = lineWidth * 2
    val height = lineWidth * originalHeight / originalWidth
    (width, height)
  }
  def makeIcon(url: String, bg: String, invert: Boolean, crops: (Int, Int, Int, Int)): BufferedImage = {
    val buffered = bufferImage(url, bg, invert, crops)
    resize(buffered, iconSize, iconSize)
  }
  def makeVaadinResource(img: BufferedImage, name: String): Resource = {
    class IconSource extends StreamResource.StreamSource {
      def getStream(): InputStream = {
        try {
          val imagebuffer = new ByteArrayOutputStream
          ImageIO.write(img, "png", imagebuffer)
          new ByteArrayInputStream(imagebuffer.toByteArray)
        } catch {
          case ioe: IOException => null
        }
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
  private def resize(img: BufferedImage, width: Int, height: Int) = {
    val resized = new BufferedImage(width, height, img.getType)
    val g = resized.createGraphics
    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
    g.drawImage(img, 0, 0, width, height, 0, 0, img.getWidth, img.getHeight, null);
    g.dispose
    resized
  }
}
