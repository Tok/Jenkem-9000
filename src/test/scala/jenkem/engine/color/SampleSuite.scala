package jenkem.engine.color

import org.junit.runner.RunWith
import jenkem.AbstractTester
import org.scalatest.junit.JUnitRunner
import org.scalatest.PrivateMethodTester.PrivateMethod
import org.scalatest.PrivateMethodTester.anyRefToInvoker

@RunWith(classOf[JUnitRunner])
class SampleSuite extends AbstractTester {
  val blackMap: Color.RgbMap = makeColorMap((0, 0, 0))
  val whiteMap: Color.RgbMap = makeColorMap((255, 255, 255))
  val grayMap: Color.RgbMap = makeColorMap((127, 127, 127))
  val redMap: Color.RgbMap = makeColorMap((255, 0, 0))
  val tetraMap: Color.RgbMap = Map(
        (0, 0) -> (255, 255, 0), (0, 1) -> (0, 0, 255),
        (1, 0) -> (0, 255, 0), (1, 1) -> (255, 0, 0))

  val allBlack = Sample.makeColorSample(blackMap, 0, 0, 1, 0)
  val allWhite = Sample.makeColorSample(whiteMap, 0, 0, 1, 0)
  val allGray = Sample.makeColorSample(grayMap, 0, 0, 1, 0)
  val allRed = Sample.makeColorSample(redMap, 0, 0, 1, 0)
  val allFour = Sample.makeColorSample(tetraMap, 0, 0, 1, 0)

  test("Gray Sample") {
    assert(Sample.makeGreySample(blackMap, 0, 0, 1, 0) === (0, 0, 0, 0))
    assert(Sample.makeGreySample(whiteMap, 0, 0, 1, 0) === (255, 255, 255, 255))
    assert(Sample.makeGreySample(grayMap, 0, 0, 1, 0) === (127, 127, 127, 127))
    assert(Sample.makeGreySample(redMap, 0, 0, 1, 0) === (84, 84, 84, 84))
    assert(Sample.makeGreySample(tetraMap, 0, 0, 1, 0) === (170, 84, 84, 84))
  }

  test("Color Sample") {
    assert(allBlack === ((0, 0, 0), (0, 0, 0), (0, 0, 0), (0, 0, 0)))
    assert(allWhite === ((255, 255, 255), (255, 255, 255), (255, 255, 255), (255, 255, 255)))
    assert(allGray === ((127, 127, 127), (127, 127, 127), (127, 127, 127), (127, 127, 127)))
    assert(allRed === ((255, 0, 0), (255, 0, 0), (255, 0, 0), (255, 0, 0)))
    assert(allFour === ((255, 255, 0), (0, 0, 255), (0, 255, 0), (255, 0, 0)))
  }

  test("RGB Difference") {
    def test(dir: Sample.Dir): Unit = {
      assert(Sample.calcRgbDiff(allBlack, dir) === 0)
      assert(Sample.calcRgbDiff(allWhite, dir) === 0)
      assert(Sample.calcRgbDiff(allGray, dir) === 0)
      assert(Sample.calcRgbDiff(allRed, dir) === 0)
    }
    Sample.dirs.foreach(test(_))
    assert(Sample.calcRgbDiff(allFour, Sample.TOP) === 255)
    assert(Sample.calcRgbDiff(allFour, Sample.BOT) === 170)
    assert(Sample.calcRgbDiff(allFour, Sample.LEFT) === 85)
    assert(Sample.calcRgbDiff(allFour, Sample.RIGHT) === 170)
  }

  test("RGB Mean") {
    def test(dir: Sample.Dir): Unit = {
      assert(Sample.calcRgbMean(allBlack, dir) === (0, 0, 0))
      assert(Sample.calcRgbMean(allWhite, dir) === (255, 255, 255))
      assert(Sample.calcRgbMean(allGray, dir) === (127, 127, 127))
      assert(Sample.calcRgbMean(allRed, dir) === (255, 0, 0))
    }
    Sample.dirs.foreach(test(_))
    assert(Sample.calcRgbMean(allFour, Sample.TOP) === (127, 127, 127))
    assert(Sample.calcRgbMean(allFour, Sample.BOT) === (127, 127, 0))
    assert(Sample.calcRgbMean(allFour, Sample.LEFT) === (127, 255, 0))
    assert(Sample.calcRgbMean(allFour, Sample.RIGHT) === (127, 0, 127))
  }

  test("Contrast and Brightness") {
    assert(Sample.calcCol(0, 0, -100) === 0)
    assert(Sample.calcCol(0, 0, 0) === 0)
    assert(Sample.calcCol(0, 0, 100) === 100)
    assert(Sample.calcCol(0, -100, 0) === 127)
    assert(Sample.calcCol(0, 0, 0) === 0)
    assert(Sample.calcCol(0, 100, 0) === 0)
    assert(Sample.calcCol(127, 0, -100) === 27)
    assert(Sample.calcCol(127, 0, 0) === 127)
    assert(Sample.calcCol(127, 0, 100) === 227)
    assert(Sample.calcCol(127, -100, 0) === 127)
    assert(Sample.calcCol(127, 0, 0) === 127)
    assert(Sample.calcCol(127, 100, 0) === 127)
    assert(Sample.calcCol(255, 0, -100) === 155)
    assert(Sample.calcCol(255, 0, 0) === 255)
    assert(Sample.calcCol(255, 0, 100) === 255)
    assert(Sample.calcCol(255, -100, 0) === 127)
    assert(Sample.calcCol(255, 0, 0) === 255)
    assert(Sample.calcCol(255, 100, 0) === 255)
    assert(Sample.calcCol(50, 0, -100) === 0)
    assert(Sample.calcCol(50, 0, 0) === 50)
    assert(Sample.calcCol(50, 0, 100) === 150)
    assert(Sample.calcCol(50, -100, 0) === 127)
    assert(Sample.calcCol(50, 0, 0) === 50)
    assert(Sample.calcCol(50, 100, 0) === 0)
  }

  test("Directed Gray") {
    assert(Sample.getDirectedGrey((0, 0, 0, 0), Sample.TOP) === 0)
    assert(Sample.getDirectedGrey((0, 0, 0, 0), Sample.BOT) === 0)
    assert(Sample.getDirectedGrey((0, 0, 0, 0), Sample.LEFT) === 0)
    assert(Sample.getDirectedGrey((0, 0, 0, 0), Sample.RIGHT) === 0)
    assert(Sample.getDirectedGrey((50, 100, 150, 200), Sample.TOP) === 75)
    assert(Sample.getDirectedGrey((50, 100, 150, 200), Sample.BOT) === 175)
    assert(Sample.getDirectedGrey((50, 100, 150, 200), Sample.LEFT) === 100)
    assert(Sample.getDirectedGrey((50, 100, 150, 200), Sample.RIGHT) === 150)
  }

  test("Get Sample") {
    val tl = (11.shortValue, 12.shortValue, 13.shortValue, 14.shortValue)
    val tr = (21.shortValue, 22.shortValue, 23.shortValue, 24.shortValue)
    val bl = (31.shortValue, 32.shortValue, 33.shortValue, 34.shortValue)
    val br = (41.shortValue, 42.shortValue, 43.shortValue, 44.shortValue)
    val sam = (tl, tr, bl, br)
    assert(Sample.get[Sample.Grey](sam, Sample.LEFT, Sample.TOP) === tl)
    assert(Sample.get[Sample.Grey](sam, Sample.RIGHT, Sample.TOP) === tr)
    assert(Sample.get[Sample.Grey](sam, Sample.LEFT, Sample.BOT) === bl)
    assert(Sample.get[Sample.Grey](sam, Sample.RIGHT, Sample.BOT) === br)
  }

  test("Get All RGB") {
    assert(Sample.getAllRgb(allBlack) === (0, 0, 0))
    assert(Sample.getAllRgb(allWhite) === (255, 255, 255))
    assert(Sample.getAllRgb(allGray) === (127, 127, 127))
    assert(Sample.getAllRgb(allRed) === (255, 0, 0))
    assert(Sample.getAllRgb(allFour) === (127, 127, 63))
  }

  test("Calculate Mean") {
    assert(Sample.calcMean((255, 0, 0), (255, 0, 255)) === (255, 0, 127))
    assert(Sample.calcMean((0, 255, 0), (0, 0, 255)) === (0, 127, 127))
  }

  test("Distance Correction") {
    //TODO is that really what should be done?
    val correctDistance = PrivateMethod[Int]('correctDistance)
    assert(Sample.invokePrivate(correctDistance(0, -100)) === 127)
    assert(Sample.invokePrivate(correctDistance(0, 0)) === 0)
    assert(Sample.invokePrivate(correctDistance(0, 100)) === -127)
    assert(Sample.invokePrivate(correctDistance(255, -100)) === 127)
    assert(Sample.invokePrivate(correctDistance(255, 0)) === 255)
    assert(Sample.invokePrivate(correctDistance(255, 100)) === 383)
    assert(Sample.invokePrivate(correctDistance(127, -100)) === 127)
    assert(Sample.invokePrivate(correctDistance(127, 0)) === 127)
    assert(Sample.invokePrivate(correctDistance(127, 100)) === 127)
    assert(Sample.invokePrivate(correctDistance(50, -100)) === 127)
    assert(Sample.invokePrivate(correctDistance(50, 0)) === 50)
    assert(Sample.invokePrivate(correctDistance(50, 100)) === -27)
  }

  test("Mean Gray") {
    assert(Sample.getMeanGrey((10, 20, 30, 40)) === 25)
  }

  test("RGB") {
    val keepInRange = PrivateMethod[Int]('keepInRange)
    assert(Sample.invokePrivate(keepInRange(0)) === 0)
    assert(Sample.invokePrivate(keepInRange(-100)) === 0)
    assert(Sample.invokePrivate(keepInRange(100)) === 100)
    assert(Sample.invokePrivate(keepInRange(300)) === 255)
    assert(Sample.invokePrivate(keepInRange(255)) === 255)
  }

  test("Pointless") {
    Sample.dirs.foreach(testAny(_, true))
  }

  def makeColorMap(color: Color.Rgb): Color.RgbMap = {
    Map((0, 0) -> color, (0, 1) -> color, (1, 0) -> color, (1, 1) -> color)
  }
}
