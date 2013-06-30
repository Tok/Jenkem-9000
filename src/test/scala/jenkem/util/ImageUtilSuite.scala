package jenkem.util

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.PrivateMethodTester.PrivateMethod
import org.scalatest.PrivateMethodTester.anyRefToInvoker
import jenkem.engine.color.Color
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ImageUtilSuite extends FunSuite {
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

  test("Make pixel gray") {
    assert(ImageUtil.makeGreyPixel(blackPixelMap, 0, 0) === 0)
    assert(ImageUtil.makeGreyPixel(whitePixelMap, 0, 0) === 255)
    assert(ImageUtil.makeGreyPixel(grayPixelMap, 0, 0) === 127)
    assert(ImageUtil.makeGreyPixel(redPixelMap, 0, 0) === 85)
    assert(ImageUtil.makeGreyPixel(darkGreenPixelMap, 0, 0) === 42)
    assert(ImageUtil.makeGreyPixel(blackPixelMap, 1, 0) === 0)
    assert(ImageUtil.makeGreyPixel(blackPixelMap, 0, 1) === 0)
  }

  test("Pixel sum") {
    val getPixelRgbSum = PrivateMethod[Short]('getPixelRgbSum)
    assert(ImageUtil.invokePrivate(getPixelRgbSum(blackPixelMap, 0, 0)) === 0)
    assert(ImageUtil.invokePrivate(getPixelRgbSum(whitePixelMap, 0, 0)) === 765)
    assert(ImageUtil.invokePrivate(getPixelRgbSum(grayPixelMap, 0, 0)) === 381)
    assert(ImageUtil.invokePrivate(getPixelRgbSum(redPixelMap, 0, 0)) === 255)
    assert(ImageUtil.invokePrivate(getPixelRgbSum(darkGreenPixelMap, 0, 0)) === 127)
    assert(ImageUtil.invokePrivate(getPixelRgbSum(blackPixelMap, 1, 0)) === 0)
    assert(ImageUtil.invokePrivate(getPixelRgbSum(whitePixelMap, 1, 0)) === 0)
    assert(ImageUtil.invokePrivate(getPixelRgbSum(whitePixelMap, 0, 1)) === 0)
    assert(ImageUtil.invokePrivate(getPixelRgbSum(whitePixelMap, 1, 1)) === 0)
  }

  test("Get Pixel") {
    assert(ImageUtil.getPixels(blackPixelMap, 0, 0) === blackPixel)
    assert(ImageUtil.getPixels(whitePixelMap, 0, 0) === whitePixel)
    assert(ImageUtil.getPixels(grayPixelMap, 0, 0) === grayPixel)
    assert(ImageUtil.getPixels(redPixelMap, 0, 0) === redPixel)
    assert(ImageUtil.getPixels(darkGreenPixelMap, 0, 0) === darkGreenPixel)
    assert(ImageUtil.getPixels(blackPixelMap, 1, 0) === blackPixel)
    assert(ImageUtil.getPixels(whitePixelMap, 1, 0) === blackPixel)
    assert(ImageUtil.getPixels(whitePixelMap, 0, 1) === blackPixel)
    assert(ImageUtil.getPixels(whitePixelMap, 1, 1) === blackPixel)
  }
}
