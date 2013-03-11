package jenkem.ui

import com.vaadin.shared.ui.label.ContentMode
import com.vaadin.ui.Label
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.Alignment

class Inline extends VerticalLayout {
  //setSizeFull

  val spacer = new Label
  spacer.setWidth(500 + "px")
  spacer.setHeight(0 + "px")
  addComponent(spacer)
  setComponentAlignment(spacer, Alignment.TOP_LEFT)

  val preview = new Label
  preview.setContentMode(ContentMode.HTML)
  addComponent(preview)
  setComponentAlignment(preview, Alignment.TOP_LEFT)

  def setValue(value: String) = preview.setValue(value)
}
