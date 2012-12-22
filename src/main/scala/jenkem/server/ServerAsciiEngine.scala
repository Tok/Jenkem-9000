package jenkem.server

import com.google.gwt.canvas.client.Canvas
import com.google.gwt.canvas.dom.client.ImageData
import com.google.gwt.dom.client.ImageElement
import com.google.gwt.user.client.ui.Image
import jenkem.shared.CharacterSet
import jenkem.shared.ColorScheme
import jenkem.shared.ConversionMethod
import jenkem.shared.Engine
import jenkem.shared.Kick
import jenkem.shared.Power
import jenkem.shared.color.Sample
import jenkem.shared.color.IrcColor
import java.util.HashMap

/**
 * Converts images to colored ASCII on server.
 */
class ServerAsciiEngine {
  val TOTAL_PERCENT = 100
  val engine = new Engine
  var width = 72 //TODO make immutable
  var contrast = 0
  var brightness = 0
  var scheme = ColorScheme.Full
  var preset = CharacterSet.Hard
  var method = ConversionMethod.SuperHybrid
  var kick = Kick.Off

  def ServerAsciiEngine() {}

  def generate(url: String): List[String] = {
    val lastIndex = prepare(url)
    val ircOutput = List[String]()
    val startLine = if (method.hasKick) { engine.getStartY() } else { 0 }
    def generate0(index: Int, accu: List[String]): List[String] = {
      if (index > lastIndex) {
        accu
      } else {
        generate0(index + 1, generateLine(method, index) :: accu)
      }
    }
    generate0(0, Nil)
  }

  def generateLine(method: ConversionMethod, index: Int): String = {
    if (method.equals(ConversionMethod.FullHd)) {
      engine.generateHighDefLine(index);
    } else if (method.equals(ConversionMethod.SuperHybrid)) {
      engine.generateSuperHybridLine(index);
    } else if (method.equals(ConversionMethod.Pwntari)) {
      engine.generatePwntariLine(index);
    } else if (method.equals(ConversionMethod.Hybrid)) {
      engine.generateHybridLine(index);
    } else if (method.equals(ConversionMethod.Plain)) {
      engine.generatePlainLine(index);
    } else {
      throw new IllegalArgumentException("Method unknown: " + method)
    }
  }

  //XXX if conversion doesn't work, try to fix this
  def prepare(url: String): Int = {
    val image = new Image
    image.setUrl(url)
    val currentImage = ImageElement.as(image.getElement)
    val actualWidth = math.min(width, currentImage.getWidth)
    val divisor = if (method.hasKick) { 1 } else { 2 }
    val height = ((width / divisor) * currentImage.getHeight) / currentImage.getWidth
    val canvas = Canvas.createIfSupported //let's hope this works
    canvas.setWidth(String.valueOf(actualWidth) + "px")
    canvas.setHeight(String.valueOf(height) + "px")
    canvas.getContext2d.fillRect(0, 0, actualWidth, height) //resets the canvas with black bg
    canvas.getContext2d.drawImage(currentImage, 0, 0, actualWidth, height)
    val id = canvas.getContext2d.getImageData(0, 0, actualWidth, height)

    val sensitivity: Double = Sample.HALF_RGB / TOTAL_PERCENT
    val actualContrast: Int = ((contrast * sensitivity) + Sample.HALF_RGB).asInstanceOf[Int]
    engine.setParams(id, preset, kick, contrast, brightness)
    if (!method.equals(ConversionMethod.Plain)) {
      val colorMap: java.util.Map[IrcColor, java.lang.Integer] = new HashMap[IrcColor, java.lang.Integer];
      IrcColor.values.map(ic => colorMap.put(ic, 100)); //TODO implement
      engine.prepareEngine(colorMap, Power.Linear);
    }
    id.getHeight
  }

  def setMethod(method: ConversionMethod) = this.method = method
  def setKick(kick: Kick) = this.kick = kick
  def setCharacterSet(preset: CharacterSet) = this.preset = preset
  def setColorScheme(scheme: ColorScheme) = this.scheme = scheme
  def setContrast(contrast: Int) = this.contrast = contrast
  def setBrightness(brightness: Int) = this.brightness = brightness
  def setWidth(width: Int) = this.width = width
}
