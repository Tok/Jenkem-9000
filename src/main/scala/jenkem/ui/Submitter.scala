package jenkem.ui

import java.awt.image.BufferedImage

import com.vaadin.data.Property
import com.vaadin.data.Property.ValueChangeEvent
import com.vaadin.event.EventRouter
import com.vaadin.event.FieldEvents.FocusEvent
import com.vaadin.event.FieldEvents.FocusListener
import com.vaadin.ui.Alignment
import com.vaadin.ui.Button
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.GridLayout
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Image
import com.vaadin.ui.TextField

import jenkem.util.AwtImageUtil
import jenkem.event.SaveImageEvent

class Submitter(val eventRouter: EventRouter) extends HorizontalLayout {
  val maxNameLength = 40
  var disable = false

  setSpacing(true)

  val nameTextField = new TextField
  nameTextField.setWidth(250 + "px")
  nameTextField.setImmediate(true)
  nameTextField.addFocusListener(new FocusListener {
    override def focus(event: FocusEvent) { nameTextField.selectAll }
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
  addComponent(nameTextField)
  setComponentAlignment(nameTextField, Alignment.MIDDLE_LEFT)

  val submitButton = new Button("Submit To Gallery")
  submitButton.addClickListener(new Button.ClickListener {
    override def buttonClick(event: ClickEvent) {
      eventRouter.fireEvent(new SaveImageEvent)
    }
  })
  addComponent(submitButton)
  setComponentAlignment(submitButton, Alignment.MIDDLE_LEFT)

  val imageLayout = new GridLayout
  imageLayout.setRows(1)
  imageLayout.setColumns(1)
  addComponent(imageLayout)
  setComponentAlignment(imageLayout, Alignment.MIDDLE_LEFT)

  def addIcon(img: BufferedImage) {
    val image = new Image
    image.setSource(AwtImageUtil.makeVaadinResource(img, "Icon"))
    image.setWidth("32px")
    image.setHeight("32px")
    imageLayout.removeAllComponents
    imageLayout.addComponent(image, 0, 0)
  }
  def setName(name: String) {
    if (name.size > maxNameLength) {
      nameTextField.setValue(name.substring(0, maxNameLength))
    } else {
      nameTextField.setValue(name)
    }
  }
  def getName: String = nameTextField.getValue
  def enableSubmission(enabled: Boolean) { submitButton.setEnabled(enabled) }
}
