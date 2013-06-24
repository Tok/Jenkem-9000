package jenkem.ui

import com.vaadin.shared.ui.label.ContentMode
import com.vaadin.ui.Alignment
import com.vaadin.ui.Label
import com.vaadin.ui.VerticalLayout

class Inline extends VerticalLayout {
  val spacer = new Label
  spacer.setWidth(500 + "px")
  spacer.setHeight(0 + "px")
  addComponent(spacer)
  setComponentAlignment(spacer, Alignment.TOP_LEFT)

  val preview = new Label
  preview.setContentMode(ContentMode.HTML)
  addComponent(preview)
  setComponentAlignment(preview, Alignment.TOP_LEFT)

  def setValue(value: String): Unit = preview.setValue(value)
}
