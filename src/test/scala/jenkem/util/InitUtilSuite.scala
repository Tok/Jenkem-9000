package jenkem.util

import org.junit.runner.RunWith
import org.scalatest.PrivateMethodTester.PrivateMethod
import org.scalatest.PrivateMethodTester.anyRefToInvoker
import jenkem.AbstractTester
import jenkem.engine.Method
import jenkem.engine.Pal
import jenkem.engine.color.Color
import jenkem.engine.color.Scheme
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class InitUtilSuite extends AbstractTester {
  val blackPixel: Color.Rgb = (0, 0, 0)
  val whitePixel: Color.Rgb = (255, 255, 255)
  val grayPixel: Color.Rgb = (127, 127, 127)
  val redPixel: Color.Rgb = (255, 0, 0)
  val greenPixel: Color.Rgb = (0, 255, 0)
  val bluePixel: Color.Rgb = (0, 0, 255)
  val cyanPixel: Color.Rgb = (0, 255, 255)
  val yellowPixel: Color.Rgb = (255, 255, 0)
  val magentaPixel: Color.Rgb = (255, 0, 255)
  val darkGreenPixel: Color.Rgb = (0, 127, 0)
  val blackPixelMap: Color.RgbMap = Map((0, 0) -> blackPixel)
  val whitePixelMap: Color.RgbMap = Map((0, 0) -> whitePixel)
  val grayPixelMap: Color.RgbMap = Map((0, 0) -> grayPixel)
  val redPixelMap: Color.RgbMap = Map((0, 0) -> redPixel)
  val darkGreenPixelMap: Color.RgbMap = Map((0, 0) -> darkGreenPixel)

  test("Black Pixel") {
    val (methodOpt, scheme, charsetOpt) = InitUtil.getDefaults(blackPixelMap)
    assert(methodOpt.get === Method.Stencil)
    assert(scheme === Scheme.Bwg)
    assert(charsetOpt.get === Pal.HCrude)
  }

  test("White Pixel") {
    val (methodOpt, scheme, charsetOpt) = InitUtil.getDefaults(whitePixelMap)
    assert(methodOpt.get === Method.Stencil)
    assert(scheme === Scheme.Bwg)
    assert(charsetOpt.get === Pal.HCrude)
  }

  test("Gray Pixel") {
    val (methodOpt, scheme, charsetOpt) = InitUtil.getDefaults(grayPixelMap)
    assert(methodOpt === None)
    assert(scheme === Scheme.Bwg)
    assert(charsetOpt === None)
  }

  test("Red Pixel") {
    val (methodOpt, scheme, charsetOpt) = InitUtil.getDefaults(redPixelMap)
    assert(methodOpt === None)
    assert(scheme === Scheme.default)
    assert(charsetOpt === None)
  }

  test("Gray detection") {
    val isPixelGray = PrivateMethod[Boolean]('isPixelGray)
    assert(InitUtil.invokePrivate(isPixelGray(grayPixel)))
    assert(InitUtil.invokePrivate(isPixelGray(blackPixel)))
    assert(InitUtil.invokePrivate(isPixelGray(whitePixel)))
    assert(!InitUtil.invokePrivate(isPixelGray(redPixel)))
    assert(!InitUtil.invokePrivate(isPixelGray(greenPixel)))
    assert(!InitUtil.invokePrivate(isPixelGray(bluePixel)))
    assert(!InitUtil.invokePrivate(isPixelGray(cyanPixel)))
    assert(!InitUtil.invokePrivate(isPixelGray(yellowPixel)))
    assert(!InitUtil.invokePrivate(isPixelGray(magentaPixel)))
    assert(!InitUtil.invokePrivate(isPixelGray(darkGreenPixel)))
    val tol: Short = InitUtil.COLOR_TOLERANCE
    val twotol: Short = (tol * 2).shortValue
    assert(!InitUtil.invokePrivate(isPixelGray((0.shortValue, tol, twotol))))
    assert(!InitUtil.invokePrivate(isPixelGray((0.shortValue, twotol, tol))))
    assert(!InitUtil.invokePrivate(isPixelGray((tol, 0.shortValue, twotol))))
    assert(!InitUtil.invokePrivate(isPixelGray((twotol, 0.shortValue, tol))))
    assert(!InitUtil.invokePrivate(isPixelGray((tol, twotol, 0.shortValue))))
    assert(!InitUtil.invokePrivate(isPixelGray((twotol, tol, 0.shortValue))))
  }

  test("Black and White detection") {
    val isPixelBlackOrWhite = PrivateMethod[Boolean]('isPixelBlackOrWhite)
    assert(InitUtil.invokePrivate(isPixelBlackOrWhite(blackPixel)))
    assert(InitUtil.invokePrivate(isPixelBlackOrWhite(whitePixel)))
    assert(!InitUtil.invokePrivate(isPixelBlackOrWhite(grayPixel)))
    assert(!InitUtil.invokePrivate(isPixelBlackOrWhite(redPixel)))
    assert(!InitUtil.invokePrivate(isPixelBlackOrWhite(darkGreenPixel)))
  }

  test("Calculate Initial Size") {
    val (pw, ph) = InitUtil.calculateNewSize(Method.Pwntari, 72, 1000, 1000)
    val (vw, vh) = InitUtil.calculateNewSize(Method.Vortacular, 72, 1000, 1000)
    assert(pw === 72)
    assert(ph === 72)
    assert(vw === 144)
    assert(vh === 72)
    val (pw2, ph2) = InitUtil.calculateNewSize(Method.Pwntari, 72, 1000, 500)
    val (vw2, vh2) = InitUtil.calculateNewSize(Method.Vortacular, 72, 1000, 500)
    assert(pw2 === 72)
    assert(ph2 === 36)
    assert(vw2 === 144)
    assert(vh2 === 36)
    val (pw3, ph3) = InitUtil.calculateNewSize(Method.Pwntari, 72, 500, 1000)
    val (vw3, vh3) = InitUtil.calculateNewSize(Method.Vortacular, 72, 500, 1000)
    assert(pw3 === 72)
    assert(ph3 === 144)
    assert(vw3 === 144)
    assert(vh3 === 144)
  }

  test("Calculate Proportional Size") {
    assert(InitUtil.calculateProportionalSize(Method.Pwntari, 72, 72, 72) === (72, 72))
    assert(InitUtil.calculateProportionalSize(Method.Pwntari, 72, 144, 144) === (72, 72))
    assert(InitUtil.calculateProportionalSize(Method.Pwntari, 72, 360, 360) === (72, 72))
    assert(InitUtil.calculateProportionalSize(Method.Pwntari, 72, 419, 419) === (72, 72))
    assert(InitUtil.calculateProportionalSize(Method.Pwntari, 72, 418, 418) === (38, 38))
    assert(InitUtil.calculateProportionalSize(Method.Pwntari, 72, 503, 503) === (72, 72))
    assert(InitUtil.calculateProportionalSize(Method.Pwntari, 72, 503, 419) === (72, 59))

    assert(InitUtil.calculateProportionalSize(Method.Pwntari, 72, 72, 72) === (72, 72))
    assert(InitUtil.calculateProportionalSize(Method.Pwntari, 72, 36, 36) === (72, 72))
    assert(InitUtil.calculateProportionalSize(Method.Pwntari, 72, 18, 18) === (72, 72))
    assert(InitUtil.calculateProportionalSize(Method.Pwntari, 72, 15, 15) === (60, 60))
    assert(InitUtil.calculateProportionalSize(Method.Pwntari, 72, 40, 40) === (40, 40))
    assert(InitUtil.calculateProportionalSize(Method.Pwntari, 72, 30, 30) === (60, 60))
    assert(InitUtil.calculateProportionalSize(Method.Pwntari, 72, 35, 45) === (70, 90))

    assert(InitUtil.calculateProportionalSize(Method.Vortacular, 72, 144, 144) === (144, 72))
    assert(InitUtil.calculateProportionalSize(Method.Vortacular, 72, 360, 360) === (144, 72))
    assert(InitUtil.calculateProportionalSize(Method.Vortacular, 72, 419, 419) === (144, 72))
    assert(InitUtil.calculateProportionalSize(Method.Vortacular, 72, 418, 418) === (76, 38))
    assert(InitUtil.calculateProportionalSize(Method.Vortacular, 72, 503, 503) === (144, 72))
    assert(InitUtil.calculateProportionalSize(Method.Vortacular, 72, 503, 419) === (144, 59))

    assert(InitUtil.calculateProportionalSize(Method.Vortacular, 72, 72, 72) === (144, 72))
    assert(InitUtil.calculateProportionalSize(Method.Vortacular, 72, 36, 36) === (144, 72))
    assert(InitUtil.calculateProportionalSize(Method.Vortacular, 72, 18, 18) === (144, 72))
    assert(InitUtil.calculateProportionalSize(Method.Vortacular, 72, 15, 15) === (120, 60))
    assert(InitUtil.calculateProportionalSize(Method.Vortacular, 72, 40, 40) === (80, 40))
    assert(InitUtil.calculateProportionalSize(Method.Vortacular, 72, 30, 30) === (120, 60))
    assert(InitUtil.calculateProportionalSize(Method.Vortacular, 72, 35, 45) === (140, 90))

    assert(InitUtil.calculateProportionalSize(Method.Vortacular, 72, 1000, 1000) === (100, 50))
    assert(InitUtil.calculateProportionalSize(Method.Vortacular, 72, 10000, 10000) === (100, 50))
  }

  test("Calculate Many Proportional Size") {
    val limit = 72
    val xRange = (1 to 300)
    val yRange = (1 to 300)
    for {
      x <- xRange
      y <- yRange
    } yield {
      val pair = InitUtil.calculateProportionalSize(Method.Pwntari, limit, x, y)
      assert(pair._1 !== 0)
      assert(pair._2 !== 0)
      assert(pair._1 <= limit)
    }
  }

  test("Default Width") {
    assert(InitUtil.MIN_WIDTH < InitUtil.MAX_WIDTH)
    assert(InitUtil.DEFAULT_WIDTH <= InitUtil.MAX_WIDTH)
    assert(InitUtil.MIN_WIDTH <= InitUtil.DEFAULT_WIDTH)
  }
}
