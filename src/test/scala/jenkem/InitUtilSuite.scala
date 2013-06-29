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

  test("Mean dev calculation") {
    val getMeanDev = PrivateMethod[Int]('getMeanDev)
    assert(InitUtil.invokePrivate(getMeanDev(grayPixelMap)) === 0)
    assert((127 to 128).contains(InitUtil.invokePrivate(getMeanDev(blackPixelMap))))
    assert((127 to 128).contains(InitUtil.invokePrivate(getMeanDev(whitePixelMap))))
    assert((127 to 128).contains(InitUtil.invokePrivate(getMeanDev(redPixelMap))))
    assert((84 to 85).contains(InitUtil.invokePrivate(getMeanDev(darkGreenPixelMap))))
  }
}
