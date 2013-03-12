package jenkem.server

import java.awt.image.BufferedImage
import java.util.HashMap

import scala.Array.canBuildFrom

import jenkem.AwtImageUtil
import jenkem.shared.CharacterSet
import jenkem.shared.ColorScheme
import jenkem.shared.ConversionMethod
import jenkem.shared.Engine
import jenkem.shared.ProcessionSettings
import jenkem.shared.color.IrcColor

/**
 * Converts images to colored ASCII on server.
 */
class ServerAsciiEngine {
  val TOTAL_PERCENT = 100
  val engine = new Engine
  var contrast = 0
  var brightness = 0

  def ServerAsciiEngine() {}

  def generate(url: String, cs: ConversionSettings): List[String] = {
    val lastIndex = prepare(url, cs)
    val ircOutput = List[String]()
    val step = cs.method.getStep
    def generate0(index: Int): List[String] = {
      if (index + step > lastIndex) Nil
      else engine.generateLine(cs.method, index) :: generate0(index + step)
    }
    val message = if (!cs.method.equals(ConversionMethod.Plain)) {
      List("Mode: " + cs.method + ", Scheme: " + cs.schemeName
        + ", Brightness: " + brightness + ", Contrast: " + contrast)
    } else Nil
    message ::: generate0(0)
  }

  def prepare(url: String, cs: ConversionSettings): Int = {
    val originalImage = AwtImageUtil.bufferImage(url, "black")
    val originalWidth = originalImage.getWidth
    val originalHeight = originalImage.getHeight
    val (width, height) = AwtImageUtil.calculateNewSize(cs.method, cs.width, originalWidth, originalHeight)
    val imageRgb = AwtImageUtil.getImageRgb(originalImage, width, height, cs.kick)

    //TODO make changeable from bot
    val ps: ProcessionSettings = new ProcessionSettings(32, true, true, true, true, false)
    val chars = cs.chars.replaceAll("[,0-9]", "")

    //TODO reimplement method and scheme overriding
    //val method = jenkem.shared.ImageUtil.getDefaultMethod(imageRgb, CharacterSet.hasAnsi(chars), width, height)
    //val scheme = jenkem.shared.ImageUtil.getDefaultColorScheme(imageRgb, width, height)

    val contrast = jenkem.shared.ImageUtil.getDefaultContrast(imageRgb, width, height) - 100
    val brightness = jenkem.shared.ImageUtil.getDefaultBrightness(imageRgb, width, height) - 100

    engine.setParams(imageRgb, width, chars, contrast, brightness, ps)
    if (!cs.method.equals(ConversionMethod.Plain)) {
      engine.prepareEngine(cs.colorMap, cs.power)
    }
    height
  }
}
