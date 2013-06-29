package jenkem

import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import jenkem.engine.color.Color
import jenkem.engine.Pal
import jenkem.engine.Method
import jenkem.util.InitUtil
import jenkem.engine.color.Scheme
import org.scalatest.PrivateMethodTester.PrivateMethod
import org.scalatest.PrivateMethodTester._

@RunWith(classOf[JUnitRunner])
class InitUtilSuite extends FunSuite {
  val blackPixel: Color.Rgb = (0, 0, 0)
  val whitePixel: Color.Rgb = (255, 255, 255)
  val grayPixel: Color.Rgb = (127, 127, 127)
  val redPixel: Color.Rgb = (255, 0, 0)
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
    assert(scheme === Scheme.Default)
    assert(charsetOpt === None)
  }

  test("Gray detection") {
    val isPixelGray = PrivateMethod[Boolean]('isPixelGray)
    assert(InitUtil.invokePrivate(isPixelGray(grayPixel)))
    assert(InitUtil.invokePrivate(isPixelGray(blackPixel)))
    assert(InitUtil.invokePrivate(isPixelGray(whitePixel)))
    assert(!InitUtil.invokePrivate(isPixelGray(redPixel)))
    assert(!InitUtil.invokePrivate(isPixelGray(darkGreenPixel)))
  }

  test("Black and White detection") {
    val isPixelBlackOrWhite = PrivateMethod[Boolean]('isPixelBlackOrWhite)
    assert(InitUtil.invokePrivate(isPixelBlackOrWhite(blackPixel)))
    assert(InitUtil.invokePrivate(isPixelBlackOrWhite(whitePixel)))
    assert(!InitUtil.invokePrivate(isPixelBlackOrWhite(grayPixel)))
    assert(!InitUtil.invokePrivate(isPixelBlackOrWhite(redPixel)))
    assert(!InitUtil.invokePrivate(isPixelBlackOrWhite(darkGreenPixel)))
  }

  test("Mean RGB calculation") {
    val getMeanRgb = PrivateMethod[Int]('getMeanRgb)
    assert(InitUtil.invokePrivate(getMeanRgb(blackPixelMap)) === 0)
    assert(InitUtil.invokePrivate(getMeanRgb(whitePixelMap)) === 255)
    assert(InitUtil.invokePrivate(getMeanRgb(grayPixelMap)) === 127)
    assert(InitUtil.invokePrivate(getMeanRgb(redPixelMap)) === 85)
    assert(InitUtil.invokePrivate(getMeanRgb(darkGreenPixelMap)) === 42)
  }

  test("Mean Dev Calculation") {
    val getMeanDev = PrivateMethod[Int]('getMeanDev)
    assert(InitUtil.invokePrivate(getMeanDev(grayPixelMap)) === 0)
    assert((127 to 128).contains(InitUtil.invokePrivate(getMeanDev(blackPixelMap))))
    assert((127 to 128).contains(InitUtil.invokePrivate(getMeanDev(whitePixelMap))))
    assert((127 to 128).contains(InitUtil.invokePrivate(getMeanDev(redPixelMap))))
    assert((84 to 85).contains(InitUtil.invokePrivate(getMeanDev(darkGreenPixelMap))))
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

  test("Find Least Common Multiple") {
    val findLeastCommonMultiple = PrivateMethod[Int]('findLeastCommonMultiple)
    assert(InitUtil.invokePrivate(findLeastCommonMultiple(1, 1)) === 1)
    assert(InitUtil.invokePrivate(findLeastCommonMultiple(4, 6)) === 12)
    assert(InitUtil.invokePrivate(findLeastCommonMultiple(12, 18)) === 36)
    assert(InitUtil.invokePrivate(findLeastCommonMultiple(2, 0)) === 0)
    assert(InitUtil.invokePrivate(findLeastCommonMultiple(-6, 14)) === 42)
  }

  test("Find Greatest Common Divisor") {
    val findGreatestCommonDivisor = PrivateMethod[Int]('findGreatestCommonDivisor)
    assert(InitUtil.invokePrivate(findGreatestCommonDivisor(1, 1)) === 1)
    assert(InitUtil.invokePrivate(findGreatestCommonDivisor(4, 6)) === 2)
    assert(InitUtil.invokePrivate(findGreatestCommonDivisor(18, 84)) === 6)
    assert(InitUtil.invokePrivate(findGreatestCommonDivisor(48, 180)) === 12)
  }

  test("Calculate Proportional Size") {
    assert(InitUtil.calculateProportionalSize(Method.Pwntari, 72, 144, 144) === (48, 48))
    assert(InitUtil.calculateProportionalSize(Method.Pwntari, 72, 360, 360) === (60, 60))
    assert(InitUtil.calculateProportionalSize(Method.Pwntari, 72, 419, 419) === (69, 69))
    assert(InitUtil.calculateProportionalSize(Method.Pwntari, 72, 418, 418) === (69, 69))
    assert(InitUtil.calculateProportionalSize(Method.Pwntari, 72, 503, 503) === (71, 71))
    assert(InitUtil.calculateProportionalSize(Method.Pwntari, 72, 503, 419) === (71, 59))
    assert(InitUtil.calculateProportionalSize(Method.Pwntari, 72, 72, 72) === (72, 72))
    assert(InitUtil.calculateProportionalSize(Method.Pwntari, 72, 36, 36) === (72, 72))
    assert(InitUtil.calculateProportionalSize(Method.Pwntari, 72, 18, 18) === (72, 72))
    assert(InitUtil.calculateProportionalSize(Method.Pwntari, 72, 15, 15) === (60, 60))
    assert(InitUtil.calculateProportionalSize(Method.Pwntari, 72, 40, 40) === (40, 40))
    assert(InitUtil.calculateProportionalSize(Method.Pwntari, 72, 30, 30) === (60, 60))
    assert(InitUtil.calculateProportionalSize(Method.Pwntari, 72, 35, 45) === (70, 90))
    assert(InitUtil.calculateProportionalSize(Method.Vortacular, 72, 144, 144) === (96, 48))
    assert(InitUtil.calculateProportionalSize(Method.Vortacular, 72, 360, 360) === (120, 60))
    assert(InitUtil.calculateProportionalSize(Method.Vortacular, 72, 419, 419) === (138, 69))
    assert(InitUtil.calculateProportionalSize(Method.Vortacular, 72, 418, 418) === (138, 69))
    assert(InitUtil.calculateProportionalSize(Method.Vortacular, 72, 503, 503) === (142, 71))
    assert(InitUtil.calculateProportionalSize(Method.Vortacular, 72, 503, 419) === (142, 59))
    assert(InitUtil.calculateProportionalSize(Method.Vortacular, 72, 72, 72) === (144, 72))
    assert(InitUtil.calculateProportionalSize(Method.Vortacular, 72, 36, 36) === (144, 72))
    assert(InitUtil.calculateProportionalSize(Method.Vortacular, 72, 18, 18) === (144, 72))
    assert(InitUtil.calculateProportionalSize(Method.Vortacular, 72, 15, 15) === (120, 60))
    assert(InitUtil.calculateProportionalSize(Method.Vortacular, 72, 40, 40) === (80, 40))
    assert(InitUtil.calculateProportionalSize(Method.Vortacular, 72, 30, 30) === (120, 60))
    assert(InitUtil.calculateProportionalSize(Method.Vortacular, 72, 35, 45) === (140, 90))
  }
}
