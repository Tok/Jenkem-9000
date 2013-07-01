package jenkem.js

import scala.collection.mutable.ArrayBuffer
import org.junit.runner.RunWith
import com.vaadin.shared.ui.JavaScriptComponentState
import jenkem.AbstractTester
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class CropperSuite extends AbstractTester {
  val cropper = new Cropper

  test("Constants") {
    assert(!cropper.isReady)
  }

  test("Listeners") {
    assert(cropper.listeners.isInstanceOf[ArrayBuffer[CropperChangeListener]])
    assert(cropper.listeners.isEmpty)
    cropper.addListener(new CropperChangeListener {
      override def valueChange(c: Crops) { Unit }
    })
    assert(!cropper.listeners.isEmpty)
    cropper.listeners.clear
    assert(cropper.listeners.isEmpty)
  }

  test("Image Src") {
    val src = " "
    cropper.setImageSrc(src)
    assert(cropper.getImageSrc === src)
  }

  test("State") {
    assert(cropper.getState.isInstanceOf[CropperState])
    assert(cropper.getState.isInstanceOf[JavaScriptComponentState])
    val state = new CropperState
    assert(state.imageSrc === "")
    val newSrc = "###"
    state.setImageSrc(newSrc)
    assert(state.getImageSrc === newSrc)
  }

  test("Crops") {
    val x = 10
    val y = 20
    val x2 = 90
    val y2 = 80
    val w = 80
    val h = 60
    val crops = new Crops(x, y, x2, y2, w, h)
    assert(crops.isInstanceOf[Crops])
  }
}
