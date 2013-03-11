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

class OutputHandler(eventBus: HandlerManager) extends VerticalLayout {
  val maxNameLength = 40
  var disable = false
  setSpacing(true)
  setWidth("400px")

  val nameLayout = new HorizontalLayout
  val nameCaptionLabel = new Label("Name: ")
  nameCaptionLabel.setWidth(150 + "px")
  nameLayout.addComponent(nameCaptionLabel)
  val nameTextField = new TextField
  nameTextField.setWidth(250 + "px")
  nameTextField.setImmediate(true)
  nameTextField.addFocusListener(new FocusListener {
    override def focus(event: FocusEvent) = nameTextField.selectAll
  })
  nameTextField.addValueChangeListener(new Property.ValueChangeListener {
    override def valueChange(event: ValueChangeEvent) {
      if (!disable) {
        if (nameTextField.getValue.size > maxNameLength) {
          disable = true
          nameTextField.setValue(nameTextField.getValue.substring(0, maxNameLength))
        }
      }
      disable = false
    }
  })
  nameLayout.addComponent(nameTextField)
  addComponent(nameLayout)

  val layout = new HorizontalLayout
  val captionLabel = new Label("Submit Conversion: ")
  captionLabel.setWidth(150 + "px")
  layout.addComponent(captionLabel)
  layout.setComponentAlignment(captionLabel, Alignment.MIDDLE_LEFT)
  val submitButton = new Button("Submit To Gallery")
  submitButton.addClickListener(new Button.ClickListener {
    override def buttonClick(event: ClickEvent) {
      eventBus.fireEvent(new SaveImageEvent)
    }
  })
  layout.addComponent(submitButton)
  layout.setComponentAlignment(submitButton, Alignment.MIDDLE_LEFT)

  val imageLayout = new GridLayout
  imageLayout.setRows(1)
  imageLayout.setColumns(1)
  layout.addComponent(imageLayout)
  layout.setComponentAlignment(imageLayout, Alignment.MIDDLE_LEFT)
  addComponent(layout)

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
  def addIcon(img: BufferedImage) = {
    val image = new Image
    image.setSource(AwtImageUtil.makeVaadinResource(img, "Icon"))
    image.setWidth("32px")
    image.setHeight("32px")
    imageLayout.removeAllComponents
    imageLayout.addComponent(image, 0, 0)
  }
  def setName(name: String) = {
    if (name.size > maxNameLength) {
      nameTextField.setValue(name.substring(0, maxNameLength))
    } else {
      nameTextField.setValue(name)
    }
  }
  def getName() = nameTextField.getValue
}
