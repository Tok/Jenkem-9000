package jenkem.engine

import java.awt.image.BufferedImage
import org.junit.runner.RunWith
import jenkem.AbstractTester
import jenkem.bot.ConversionSettings
import jenkem.engine.color.Scheme
import jenkem.util.AwtImageUtil
import jenkem.util.ColorUtil
import org.scalatest.junit.JUnitRunner
import scala.util.Random
import jenkem.SlowTest
import jenkem.engine.color.Color

@RunWith(classOf[JUnitRunner])
class EngineSuite extends AbstractTester {
  val comma = ","

  test("Vortacular Line") {
    //val paramsBlack = getParams(Method.Vortacular, 0, 0, 0)
    //val lineBlack = Engine.generateLine(paramsBlack, 0)

    //val paramsWhite = getParams(Method.Vortacular, 255, 255, 255)
    //val lineWhite = Engine.generateLine(paramsWhite, 0)

    val paramsRed = getParams(Method.Vortacular, 255, 0, 0)
    val lineRed = Engine.generateLine(paramsRed, 0)
    assert(lineRed.startsWith(ColorUtil.CC))
    assert((lineRed.split(comma))(1).take(1).toInt === Scheme.Red.irc)

    val paramsGreen = getParams(Method.Vortacular, 0, 255, 0)
    val lineGreen = Engine.generateLine(paramsGreen, 0)
    assert(lineGreen.startsWith(ColorUtil.CC))
    assert((lineGreen.split(comma))(1).take(1).toInt === Scheme.Green.irc)

    val paramsBlue = getParams(Method.Vortacular, 0, 0, 255)
    val lineBlue = Engine.generateLine(paramsBlue, 0)
    assert(lineBlue.startsWith(ColorUtil.CC))
    assert((lineBlue.split(comma))(1).take(2).toInt === Scheme.Blue.irc)
  }

  test("Pwntari Line") {
    val paramsRed = getParams(Method.Pwntari, 255, 0, 0)
    val lineRed = Engine.generateLine(paramsRed, 0)
    assert(lineRed.startsWith(ColorUtil.CC))
    assert((lineRed.split(comma))(1).take(1).toInt === Scheme.Red.irc)

    val paramsGreen = getParams(Method.Pwntari, 0, 255, 0)
    val lineGreen = Engine.generateLine(paramsGreen, 0)
    assert(lineGreen.startsWith(ColorUtil.CC))
    assert((lineGreen.split(comma))(1).take(1).toInt === Scheme.Green.irc)

    val paramsBlue = getParams(Method.Pwntari, 0, 0, 255)
    val lineBlue = Engine.generateLine(paramsBlue, 0)
    assert(lineBlue.startsWith(ColorUtil.CC))
    assert((lineBlue.split(comma))(1).take(2).toInt === Scheme.Blue.irc)
  }

  test("Plain Line") {
    val paramsRed = getParams(Method.Plain, 255, 0, 0)
    val lineRed = Engine.generateLine(paramsRed, 0)
    assert(!lineRed.startsWith(ColorUtil.CC))
    assert(lineRed.length === 1)
    assert(paramsRed.characters.contains(lineRed))

    val paramsGreen = getParams(Method.Plain, 0, 255, 0)
    val lineGreen = Engine.generateLine(paramsGreen, 0)
    assert(!lineGreen.startsWith(ColorUtil.CC))
    assert(lineGreen.length === 1)
    assert(paramsRed.characters.contains(lineGreen))

    val paramsBlue = getParams(Method.Plain, 0, 0, 255)
    val lineBlue = Engine.generateLine(paramsBlue, 0)
    assert(!lineBlue.startsWith(ColorUtil.CC))
    assert(lineBlue.length === 1)
    assert(paramsRed.characters.contains(lineBlue))
  }

  test("Stencil Line") {
    val paramsRed = getParams(Method.Stencil, 255, 0, 0)
    val lineRed = Engine.generateLine(paramsRed, 0)
    assert(!lineRed.startsWith(ColorUtil.CC))
    assert(lineRed.length === 1)
    assert(paramsRed.characters.contains(lineRed))

    val paramsGreen = getParams(Method.Stencil, 0, 255, 0)
    val lineGreen = Engine.generateLine(paramsGreen, 0)
    assert(!lineGreen.startsWith(ColorUtil.CC))
    assert(lineGreen.length === 1)
    assert(paramsRed.characters.contains(lineGreen))

    val paramsBlue = getParams(Method.Stencil, 0, 0, 255)
    val lineBlue = Engine.generateLine(paramsBlue, 0)
    assert(!lineBlue.startsWith(ColorUtil.CC))
    assert(lineBlue.length === 1)
    assert(paramsRed.characters.contains(lineBlue))
  }

  private def getParams(method: Method, r: Int, g: Int, b: Int): Engine.Params = {
    val imageRgb = AwtImageUtil.getImageRgb(getImage(2, 2, r, g, b))
    val settings = new ConversionSettings
    settings.setMethod(method)
    settings.getParams(imageRgb)
  }

  private def getImage(w: Int, h: Int, r: Int, g: Int, b: Int): BufferedImage = {
    val img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB)
    val rgb = (r << 16) + (g << 8) + b
    for { x <- 0 until w; y <- 0 until h } yield { img.setRGB(x, y, rgb) }
    img
  }

  test("Random Huge Image", SlowTest) {
    def getHugeRandomImage(w: Int, h: Int): BufferedImage = {
      val img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB)
      for { x <- 0 until w; y <- 0 until h } yield {
        val r: Int = Random.nextInt(256)
        val g: Int = Random.nextInt(256)
        val b: Int = Random.nextInt(256)
        val rgb = (r << 16) + (g << 8) + b
        img.setRGB(x, y, rgb)
      }
      img
    }
    val w = 300
    val h = w
    val hugeImg = getHugeRandomImage(w, h)
    val imageRgb = AwtImageUtil.getImageRgb(hugeImg)
    def generate(m: Method, irgb: Color.RgbMap): Unit = {
      def generate0(par: Engine.Params, index: Int): List[String] = {
        if (index + 2 <= h) {
          val line = Engine.generateLine(par, index)
          assert(line.size >= w / 2)
          generate0(par, index + 2)
        } else { Nil }
      }
      val settings = new ConversionSettings
      settings.setMethod(m)
      val params = settings.getParams(irgb)
      generate0(params, 0)
    }
    Method.values.foreach(generate(_, imageRgb))
  }
}
