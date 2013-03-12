package jenkem

import java.awt.Color
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL
import scala.collection.JavaConversions._
import com.vaadin.server.StreamResource
import javax.imageio.ImageIO
import jenkem.shared.ConversionMethod
import jenkem.shared.Kick
import sun.misc.BASE64Decoder
import sun.misc.BASE64Encoder

object AwtImageUtil {
  val defaultCrops = (0, 100, 0, 100) //xs, xe, ys, ye
  val colorWhite = new Color(255, 255, 255)
  val colorBlack = new Color(0, 0, 0)

  def bufferImage(url: String, bg: String): BufferedImage = {
    bufferImage(url, bg, defaultCrops)
  }
  def bufferImage(url: String, bg: String, crops: (Int, Int, Int, Int)): BufferedImage = {
    val image = ImageIO.read(new URL(url))
    val color = if (bg.equalsIgnoreCase("white")) { colorWhite } else { colorBlack }
    val xs = crops._1
    val xe = crops._2
    val ys = 100 - crops._4 //switched to translate..
    val ye = 100 - crops._3 //..from bottom left to top left
    val cropX = (image.getWidth * xs) / 100
    val cropY = (image.getHeight * ys) / 100
    val cropW = (image.getWidth * (xe - xs)) / 100
    val cropH = (image.getHeight * (ye - ys)) / 100
    val buffered = new BufferedImage(cropW, cropH, BufferedImage.TYPE_INT_ARGB)
    buffered.getGraphics match {
      case g2d:Graphics2D =>
        g2d.setBackground(color)
        g2d.clearRect(0, 0, cropW, cropH)
        val subimage = image.getSubimage(cropX, cropY, cropW, cropH)
        g2d.drawImage(subimage, null, null)
    }
    buffered
  }
  def getImageRgb(img: BufferedImage, width: Int, height: Int, kick: Kick): java.util.Map[String, Array[java.lang.Integer]] = {
    def getRgb(img: BufferedImage, x: Int, y: Int): Array[java.lang.Integer] = {
      val argb = img.getRGB(x, y)
      Array((argb >> 16) & 0xff, (argb >> 8) & 0xff, (argb) & 0xff)
    }
    val scaled = resize(img, width, height)
    val imageRgb = new java.util.HashMap[String, Array[java.lang.Integer]]()
    for {
      y <- 0 until height - (2 * kick.getYOffset)
      x <- 0 until width - (2 * kick.getXOffset)
    } yield imageRgb.put(y + ":" + x, getRgb(scaled, x + kick.getXOffset, y + kick.getYOffset))
    imageRgb //TODO use immutable Map
  }
  def calculateNewSize(method: ConversionMethod, lineWidth: Int,
          originalWidth: Int, originalHeight: Int): (Int, Int) = {
      val factor = if (method.equals(ConversionMethod.Vortacular)) { 2 } else { 1 }
      val width = lineWidth * factor
      val divisor = if (method.hasKick || method.equals(ConversionMethod.Vortacular)) { 1 } else { 2 }
      val height = ((lineWidth / divisor) * originalHeight) / originalWidth
      (width, height)
  }
  def resize(img: BufferedImage, width: Int, height: Int) = {
      val resized = new BufferedImage(width, height, img.getType)
      val g = resized.createGraphics
      g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
      g.drawImage(img, 0, 0, width, height, 0, 0, img.getWidth, img.getHeight, null);
      g.dispose
      resized
    }
  def makeIcon(url: String, bg: String, crops: (Int, Int, Int, Int)) = {
    val buffered = bufferImage(url, bg, crops)
    resize(buffered, 32, 32)
  }
  def makeVaadinResource(img: BufferedImage, name: String) = {
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
    new StreamResource(new IconSource, name + ".png")
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
}
