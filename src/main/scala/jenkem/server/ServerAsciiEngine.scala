package jenkem.server

import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.net.URL
import java.util.HashMap
import scala.Array.canBuildFrom
import javax.imageio.ImageIO
import jenkem.shared.ConversionMethod
import jenkem.shared.Engine
import jenkem.shared.ProcessionSettings
import jenkem.shared.color.IrcColor
import jenkem.shared.ColorScheme

/**
 * Converts images to colored ASCII on server.
 */
class ServerAsciiEngine {
  val TOTAL_PERCENT = 100
  val engine = new Engine
  var contrast = 0
  var brightness = 0
  var overrideMethod = ConversionMethod.FullHd
  var overrideScheme = ColorScheme.Default

  def ServerAsciiEngine() {}

  def generate(url: String, cs: ConversionSettings): List[String] = {
    val lastIndex = prepare(url, cs)
    val ircOutput = List[String]()
    val step = cs.method.getStep
    def generate0(index: Int, accu: List[String]): List[String] = {
      if (index + step <= lastIndex) {
        val line = engine.generateLine(cs.method, index)
        if (line != null) {
          generate0(index + step, line :: accu)
        } else {
          Nil
        }
      } else accu.reverse.filter(line => line != Nil)
    }
    val message = if (!cs.method.equals(ConversionMethod.Plain)) {
      List("Mode: " + cs.method + ", Scheme: " + cs.scheme
          + ", Brightness: " + (brightness - 100) + ", Contrast: " + (contrast - 100))
    } else Nil
    message ::: generate0(0, Nil)
  }

  def prepare(url: String, cs: ConversionSettings): Int = {
    val img = ImageIO.read(new URL(url))
    val actualWidth = math.min(cs.width, img.getWidth)
    val divisor = if (cs.method.hasKick) { 1 } else { 2 }
    val actualHeight = ((cs.width / divisor) * img.getHeight) / img.getWidth
    val scaled = resize(img, actualWidth, actualHeight)

    //TODO make changeable from bot
    val ps: ProcessionSettings = new ProcessionSettings(32, true, true, true, true);

    val imageRgb: java.util.Map[String, Array[java.lang.Integer]] = new HashMap[String, Array[java.lang.Integer]]()
    for {
      y <- 0 until actualHeight
      x <- 0 until actualWidth
    } yield imageRgb.put(y + ":" + x, getRgb(scaled, x, y))

    contrast = jenkem.shared.ImageUtil.getDefaultContrast(imageRgb, actualWidth, actualHeight)
    brightness = jenkem.shared.ImageUtil.getDefaultBrightness(imageRgb, actualWidth, actualHeight)
    overrideMethod = jenkem.shared.ImageUtil.getDefaultMethod(imageRgb, actualWidth, actualHeight)
    overrideScheme = jenkem.shared.ImageUtil.getDefaultColorScheme(imageRgb, actualWidth, actualHeight)

    if (!overrideScheme.name.equalsIgnoreCase(cs.scheme.name) && overrideScheme.name.equalsIgnoreCase(ColorScheme.Bwg.name)) {
      cs.scheme = overrideScheme
      prepare(url, cs) //restart
    } else if (!overrideMethod.name.equalsIgnoreCase(cs.method.name) && overrideMethod.name.equalsIgnoreCase(ConversionMethod.Plain.name)) {
      cs.method = overrideMethod
      prepare(url, cs) //restart
    } else {
      engine.setParams(imageRgb, actualWidth, cs.charset, contrast, brightness, ps)
      if (!overrideMethod.equals(ConversionMethod.Plain)) {
        val colorMap: java.util.Map[IrcColor, java.lang.Integer] = new HashMap[IrcColor, java.lang.Integer]
        IrcColor.values.map(ic => colorMap.put(ic, ic.getOrder(cs.scheme)))
        engine.prepareEngine(colorMap, cs.power)
      }
      actualHeight
    }
  }

  def getRgb(img: BufferedImage, x: Int, y: Int): Array[java.lang.Integer] = {
    val argb = img.getRGB(x, y)
    Array((argb >> 16) & 0xff, (argb >> 8) & 0xff, (argb) & 0xff)
  }

  def resize(img: BufferedImage, width: Int, height: Int): BufferedImage = {
    val resized = new BufferedImage(width, height, img.getType)
    val g = resized.createGraphics
    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
    g.drawImage(img, 0, 0, width, height, 0, 0, img.getWidth, img.getHeight, null);
    g.dispose
    resized
  }
}
