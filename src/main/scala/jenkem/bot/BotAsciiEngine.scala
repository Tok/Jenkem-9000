package jenkem.bot

import jenkem.engine.ConversionMethod
import jenkem.engine.Engine
import jenkem.engine.Pal
import jenkem.engine.ProcSettings
import jenkem.util.AwtImageUtil

/**
 * Converts images to colored ASCII on server.
 */
class ServerAsciiEngine {
  val TOTAL_PERCENT = 100
  val engine = new Engine
  var contrast = 0
  var brightness = 0
  val defaultProcessing = 32

  def generate(url: String, cs: ConversionSettings): List[String] = {
    val lastIndex = prepare(url, cs)
    val ircOutput = List[String]()
    def generate0(index: Int): List[String] = {
      if (index + 2 > lastIndex) { Nil }
      else { engine.generateLine(cs.method, index) :: generate0(index + 2) }
    }
    val message = if (cs.method.equals(ConversionMethod.Vortacular)) {
      List("Mode: " + cs.method + ", Scheme: " + cs.schemeName
        + ", Brightness: " + brightness + ", Contrast: " + contrast)
    } else { Nil }
    message ::: generate0(0)
  }

  private def prepare(url: String, cs: ConversionSettings): Int = {
    val invert = false
    val originalImage = AwtImageUtil.bufferImage(url, "black", invert)
    val originalWidth = originalImage.getWidth
    val originalHeight = originalImage.getHeight
    //val (width, height) = AwtImageUtil.calculateNewSize(cs.method, cs.width, originalWidth, originalHeight)
    val (width, height) = AwtImageUtil.calculateNewSize(cs.width, originalWidth, originalHeight)
    val imageRgb = AwtImageUtil.getImageRgb(originalImage, width, height, cs.kick)

    //TODO make changeable from bot
    val chars = cs.chars.replaceAll("[,0-9]", "")
    val ps = ProcSettings.getInitial(Pal.hasAnsi(chars))

    //TODO reimplement method and scheme overriding
    //val method = jenkem.shared.ImageUtil.getDefaultMethod(imageRgb, CharacterSet.hasAnsi(chars), width, height)
    //val scheme = jenkem.shared.ImageUtil.getDefaultColorScheme(imageRgb, width, height)

    //FIXME replace this and use imageRgb directly!!!
    val legacyImageRgb = engine.makeLegacy(imageRgb)
    val contrast = jenkem.shared.ImageUtil.getDefaultContrast(legacyImageRgb, width, height) - 100
    val brightness = jenkem.shared.ImageUtil.getDefaultBrightness(legacyImageRgb, width, height) - 100
    //////////////////

    engine.setParams(imageRgb, width, chars, contrast, brightness, ps)
    if (cs.method.equals(ConversionMethod.Vortacular)) {
      engine.prepareEngine(cs.colorMap, cs.power)
    }
    height
  }
}
