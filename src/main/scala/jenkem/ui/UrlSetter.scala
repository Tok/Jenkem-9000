package jenkem.ui

import com.vaadin.ui.Image
import com.vaadin.ui.GridLayout
import com.vaadin.ui.Label
import com.vaadin.ui.TextField
import com.vaadin.ui.Button
import com.vaadin.ui.Alignment
import com.vaadin.server.ExternalResource
import com.google.gwt.event.shared.HandlerManager
import com.vaadin.ui.Button.ClickEvent
import jenkem.client.event.DoConversionEvent
import com.vaadin.server.Page
import com.vaadin.data.Property
import com.vaadin.data.Property.ValueChangeEvent
import com.vaadin.server.UserError
import com.vaadin.event.FieldEvents.FocusListener
import com.vaadin.event.FieldEvents.FocusEvent
import jenkem.UrlOptionizer
import com.google.gwt.user.client.ui.Grid
import java.net.URL

class UrlSetter(eventBus: HandlerManager) extends GridLayout {
  val mainImage = new Image
  mainImage.setWidth("80px")

  val imageGrid = new GridLayout
  imageGrid.setWidth("80px")
  imageGrid.setRows(1)
  imageGrid.setColumns(1)
  imageGrid.addComponent(mainImage, 0, 0)

  val statusLabel = new Label("Enter URL to an image:")
  val inputTextField = new TextField
  inputTextField.setWidth("712px")
  inputTextField.focus
  inputTextField.setImmediate(true)
  val convButton = new Button("Convert Image")
  bind

  //  0 1   2
  //0 # #####
  //  #
  //1 # ### #

  setRows(2)
  setColumns(3)

  //addComponent(component, column1, row1, column2, row2)
  addComponent(imageGrid, 0, 0, 0, 1)
  setComponentAlignment(imageGrid, Alignment.TOP_LEFT)
  addComponent(statusLabel, 1, 0, 2, 0)
  setComponentAlignment(statusLabel, Alignment.MIDDLE_LEFT)
  addComponent(inputTextField, 1, 1)
  setComponentAlignment(inputTextField, Alignment.MIDDLE_LEFT)
  addComponent(convButton, 2, 1)
  setComponentAlignment(convButton, Alignment.MIDDLE_LEFT)

  setSpacing(true)
  setMargin(true)

  def bind() {
    inputTextField.addValueChangeListener(new Property.ValueChangeListener {
      override def valueChange(event: ValueChangeEvent) = replaceUrl(eventBus)
    })
    inputTextField.addFocusListener(new FocusListener {
      override def focus(event: FocusEvent) = inputTextField.selectAll
    })
    convButton.addClickListener(new Button.ClickListener {
      override def buttonClick(event: ClickEvent) = replaceUrl(eventBus)
    })
  }

  def replaceImage(url: URL) {
    imageGrid.removeAllComponents
    val newImage = new Image
    newImage.setSource(new ExternalResource(url))
    newImage.setWidth("80px")
    imageGrid.addComponent(newImage, 0, 0)
  }

  def setLink(link: String) {
    jenkem.UrlOptionizer.extract(link) match {
      case Some(u) =>
        inputTextField.setValue(link)
        replaceImage(u)
        Page.getCurrent.setUriFragment("main/" + link)
      case None => statusLabel.setValue("URL is not valid. Please enter URL to an image: ")
    }
  }

  def replaceUrl(eventBus: HandlerManager) {
    val currentFrag = Page.getCurrent.getUriFragment
    val currentUrl = inputTextField.getValue
    if (currentFrag != null && !currentFrag.endsWith(currentUrl)) {
      jenkem.UrlOptionizer.extract(currentUrl) match {
        case Some(u) =>
          Page.getCurrent.setUriFragment("main/" + currentUrl)
          replaceImage(u)
          eventBus.fireEvent(new DoConversionEvent(true, true))
        case None => statusLabel.setValue(currentUrl + " is not a valid URL. Please enter URL to an image: ")
      }
    } else {
      eventBus.fireEvent(new DoConversionEvent(true, true))
    }
  }

  def focusShowButton = inputTextField.focus
  def getUrl: Option[String] = {
    UrlOptionizer.extract(inputTextField.getValue) match {
      case Some(u) => Option(u.toString)
      case None => setError("URL is not Valid. Please enter URL to an image: "); None
    }
  }
  def getImage = mainImage.getData
  def setStatus(status: String) = updateLabel(status, null)
  def setError(error: String) = updateLabel(error, error)
  def updateLabel(status: String, error: String) = {
    if (error == null) { statusLabel.setComponentError(null) }
    else { statusLabel.setComponentError(new UserError(error)) }
    statusLabel.setValue(status)
    inputTextField.focus
  }
}
