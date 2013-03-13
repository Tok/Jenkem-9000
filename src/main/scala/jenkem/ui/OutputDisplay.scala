package jenkem.ui

import com.vaadin.event.FieldEvents.FocusEvent
import com.vaadin.event.FieldEvents.FocusListener
import com.vaadin.ui.Label
import com.vaadin.ui.TextArea
import com.vaadin.ui.VerticalLayout

class OutputDisplay extends VerticalLayout {
  setSpacing(true)
  setWidth("400px")

  val outputCaptionLabel = new Label("Binary Output For IRC: ")
  val ircText = new TextArea
  ircText.setWidth("400px")
  ircText.setWordwrap(false)
  //ircText.setReadOnly(true)
  ircText.addFocusListener(new FocusListener {
    override def focus(event: FocusEvent) { ircText.selectAll }
  })
  addComponent(outputCaptionLabel)
  addComponent(ircText)

  def addIrcOutput(ircOutput: List[String]) {
    val lines = new StringBuilder(ircOutput.size)
    ircOutput.foreach(line => lines.append(line))
    ircText.setValue(lines.toString)
  }
}
