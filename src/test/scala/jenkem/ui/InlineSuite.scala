package jenkem.ui

import scala.collection.mutable.ArrayBuffer
import org.junit.runner.RunWith
import org.scalatest.PrivateMethodTester.PrivateMethod
import org.scalatest.PrivateMethodTester.anyRefToInvoker
import com.vaadin.shared.ui.JavaScriptComponentState
import jenkem.AbstractTester
import org.scalatest.junit.JUnitRunner
import com.vaadin.event.EventRouter
import jenkem.js.CropperChangeListener
import jenkem.js.Crops
import com.vaadin.ui.Button
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.data.Property
import com.vaadin.ui.Field
import com.vaadin.ui.Component
import com.vaadin.event.FieldEvents.FocusEvent
import com.vaadin.event.FieldEvents.FocusListener
import java.awt.image.BufferedImage
import java.net.URL
import com.vaadin.data.Property.ValueChangeEvent
import com.vaadin.ui.Image
import jenkem.engine.Method

@RunWith(classOf[JUnitRunner])
class InlineSuite extends AbstractTester {
  val mimeType = "image/png"
  val inline = new Inline
  val no = "()"
  val bogus = "--"

  test("Default") {
    assert(inline.image.isInstanceOf[Image])
    inline.setValue(bogus)
    assert(inline.preview.getValue === bogus)
    inline.isShowHtml = true
    inline.reset
    inline.isShowHtml = false
    inline.reset
  }

  test("Listeners") {
    val overlayListeners = inline.overlayButton .getListeners(classOf[ClickEvent])
    val overlayListener = overlayListeners.iterator.next.asInstanceOf[Button.ClickListener]
    assert(overlayListener.buttonClick(new ClickEvent(inline)).toString === no)
    inline.overlayButton.addClickListener(new Button.ClickListener {
      override def buttonClick(event: ClickEvent) {}
    })
  }

  test("Intermediate") {
    val bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB)
    inline.setIntermediate(bi, Method.Pwntari)
    assert(inline.image.getSource.getMIMEType === mimeType)
    inline.setIntermediate(bi, Method.Vortacular)
    assert(inline.image.getSource.getMIMEType === mimeType)
  }

  test("Switch Content") {
    val switchContent = PrivateMethod[Unit]('switchContent)
    inline.isShowHtml = false
    inline.invokePrivate(switchContent())
    assert(inline.overlayButton.getCaption === "Show Image")
    inline.isShowHtml = true
    inline.invokePrivate(switchContent())
    assert(inline.overlayButton.getCaption === "Show HTML")
  }
}
