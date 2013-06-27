package jenkem.ui

import com.vaadin.server.Resource
import com.vaadin.shared.ui.label.ContentMode
import com.vaadin.ui.Alignment
import com.vaadin.ui.Button
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.GridLayout
import com.vaadin.ui.Image
import com.vaadin.ui.Label
import com.vaadin.ui.VerticalLayout
import java.awt.image.BufferedImage
import jenkem.util.AwtImageUtil
import jenkem.engine.Method

class Inline extends VerticalLayout {
  val spacer = new Label
  spacer.setWidth(500 + "px")
  spacer.setHeight(0 + "px")

  val previewLayout = new GridLayout
  previewLayout.setRows(2)
  previewLayout.setColumns(1)
  previewLayout.addComponent(spacer, 0, 0)
  previewLayout.setComponentAlignment(spacer, Alignment.TOP_LEFT)
  addComponent(previewLayout)
  setComponentAlignment(previewLayout, Alignment.TOP_LEFT)

  val preview = new Label
  preview.setContentMode(ContentMode.HTML)

  val image = new Image

  val overlayButton = new Button
  overlayButton.setImmediate(true)
  overlayButton.addClickListener(new Button.ClickListener {
    override def buttonClick(event: ClickEvent): Unit = { switchContent }
  })
  addComponent(overlayButton)
  setComponentAlignment(overlayButton, Alignment.TOP_LEFT)

  var isShowHtml = false //gets switched on load
  private def switchContent: Unit = {
    previewLayout.removeComponent(0, 1)
    previewLayout.addComponent(if (isShowHtml) { image } else { preview }, 0, 1)
    overlayButton.setCaption(if (isShowHtml) { "Show HTML" } else { "Show Image" })
    isShowHtml = !isShowHtml
  }

  switchContent

  def setIntermediate(img: BufferedImage, method: Method): Unit = {
    val widthFactor = if (!method.equals(Method.Pwntari)) { 3 } else { 6 }
    val intermediate = AwtImageUtil.getScaled(img, img.getWidth * widthFactor, img.getHeight * 6)
    val resource = AwtImageUtil.makeVaadinResource(intermediate, "Intermediate")
    image.setSource(resource)
  }
  def setValue(value: String): Unit = preview.setValue(value)
  def reset: Unit = if (!isShowHtml) { switchContent }
}
