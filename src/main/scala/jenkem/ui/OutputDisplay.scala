package jenkem.ui

import java.awt.image.BufferedImage
import com.google.gwt.event.shared.HandlerManager
import com.vaadin.event.FieldEvents.FocusEvent
import com.vaadin.event.FieldEvents.FocusListener
import com.vaadin.ui.Alignment
import com.vaadin.ui.Button
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Image
import com.vaadin.ui.Label
import com.vaadin.ui.TextArea
import com.vaadin.ui.VerticalLayout
import jenkem.AwtImageUtil
import jenkem.client.event.SaveImageEvent
import com.vaadin.ui.Layout
import com.vaadin.ui.GridLayout
import com.vaadin.ui.TextField
import com.vaadin.data.Property
import com.vaadin.data.Property.ValueChangeEvent

class OutputDisplay extends VerticalLayout {
  setSpacing(true)
  setWidth("400px")

  val outputCaptionLabel = new Label("Binary Output For IRC: ")
  val ircText = new TextArea
  ircText.setWidth("400px")
  ircText.setWordwrap(false)
  //ircText.setReadOnly(true)
  ircText.addFocusListener(new FocusListener {
    override def focus(event: FocusEvent) = ircText.selectAll
  })
  addComponent(outputCaptionLabel)
  addComponent(ircText)

  def addIrcOutput(ircOutput: List[String]) {
    val lines = new StringBuilder(ircOutput.size)
    ircOutput.map(line => lines.append(line))
    ircText.setValue(lines.toString)
  }
}
