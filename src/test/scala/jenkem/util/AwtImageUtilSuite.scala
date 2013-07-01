package jenkem.util

import java.awt.image.BufferedImage
import org.junit.runner.RunWith
import com.vaadin.server.StreamResource
import jenkem.AbstractTester
import jenkem.engine.Kick
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class AwtImageUtilSuite extends AbstractTester {
  val blackImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB)
  val base64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAIAAACQd1PeAAAADElEQVR42mNgYGAAAAAEAAHI6uv5AAAAAElFTkSuQmCC"

  test("Constants") {
    assert(AwtImageUtil.iconSize === 32)
    assert(AwtImageUtil.defaultCrops === (0, 100, 0, 100))
    assert(AwtImageUtil.colorWhite === new java.awt.Color(255, 255, 255))
    assert(AwtImageUtil.colorBlack === new java.awt.Color(0, 0, 0))
  }

  test("Buffered Image RGB") {
    val img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB)
    assert(AwtImageUtil.getRgb(blackImg, 0, 0) === (0, 0, 0))
    val e = intercept[ArrayIndexOutOfBoundsException] {
      AwtImageUtil.getRgb(blackImg, 0, 1)
    }
    assert(e.isInstanceOf[ArrayIndexOutOfBoundsException])
  }

  test("Scaled Image RGB") {
    val colorMap = AwtImageUtil.getImageRgb(blackImg)
    assert(colorMap.size === 1)
    assert(colorMap.get((0, 0)) === Some((0, 0, 0)))
    assert(colorMap.get((1, 0)) === None)
  }

  test("Make Vaading Resource") {
    val name = "name"
    val resource = AwtImageUtil.makeVaadinResource(blackImg, name)
    assert(resource.isInstanceOf[StreamResource])
    assert(resource.getMIMEType.equalsIgnoreCase("image/png"))
  }

  test("Base 64 Encoding") {
    val encoded = AwtImageUtil.encodeToBase64(blackImg)
    assert(encoded.isInstanceOf[String])
  }

  test("Base 64 Decoding") {
    val decoded = AwtImageUtil.decodeFromBase64(base64)
    assert(decoded.isInstanceOf[BufferedImage])
  }

  test("Scaling") {
    val scaled = AwtImageUtil.getScaled(blackImg, 10, 10)
    assert(scaled.getWidth === 10)
    assert(scaled.getHeight === 10)
  }

  test("Scaling With Brightness And Contrast") {
    val brightness = 0
    val contrast = 1
    val scaled = AwtImageUtil.getScaled(blackImg, 10, 10, Kick.OFF, brightness, contrast)
    assert(scaled.getWidth === 10)
    assert(scaled.getHeight === 10)
  }
}
