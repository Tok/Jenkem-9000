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
  def bufferImage(url: String, bg: String): BufferedImage = {
    val image = ImageIO.read(new URL(url))
    val color = if (bg.equalsIgnoreCase("white")) {
      new Color(255, 255, 255)
    } else {
      new Color(0, 0, 0)
    }
    val buffered = new BufferedImage(image.getWidth, image.getHeight, BufferedImage.TYPE_INT_ARGB)
    buffered.getGraphics match {
        case g2d:Graphics2D =>
            g2d.setBackground(color)
            g2d.clearRect(0, 0, image.getWidth, image.getHeight)
            g2d.drawImage(image, null, null)
    }
    buffered
  }
  def getImageSize(url: String, bg: String): (Int, Int) = {
    val image = bufferImage(url, bg)
    (image.getWidth, image.getHeight)
  }
  def getImageRgb(url: String, bg: String, width: Int, height: Int, kick: Kick): java.util.Map[String, Array[java.lang.Integer]] = {
    def getRgb(img: BufferedImage, x: Int, y: Int): Array[java.lang.Integer] = {
      val argb = img.getRGB(x, y)
      Array((argb >> 16) & 0xff, (argb >> 8) & 0xff, (argb) & 0xff)
    }
    val scaled = resize(bufferImage(url, bg), width, height)
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
      val width = math.min(lineWidth * factor, originalWidth)
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
  def makeIcon(url: String, bg: String) = {
    val buffered = bufferImage(url, bg)
    resize(buffered, 32, 32)
  }
  def makeVaadinResource(img: BufferedImage, name: String) = {
    class IconSource extends StreamResource.StreamSource {
      //val imagebuffer: ByteArrayOutputStream = null
      //val reloads = 0
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
