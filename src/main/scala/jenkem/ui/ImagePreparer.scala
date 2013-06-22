package jenkem.ui

import java.awt.image.BufferedImage
import java.net.URL

import com.vaadin.data.Property
import com.vaadin.data.Property.ValueChangeEvent
import com.vaadin.event.EventRouter
import com.vaadin.event.FieldEvents.FocusEvent
import com.vaadin.event.FieldEvents.FocusListener
import com.vaadin.server.Page
import com.vaadin.server.UserError
import com.vaadin.ui.Alignment
import com.vaadin.ui.Button
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.GridLayout
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Label
import com.vaadin.ui.TextField

import jenkem.event.DoConversionEvent
import jenkem.js.Cropper
import jenkem.js.CropperChangeListener
import jenkem.js.Crops
import jenkem.util.UrlOptionizer

class ImagePreparer(val eventRouter: EventRouter) extends GridLayout {
  val captionWidth = "80px"

  var crops = new Crops(0, 0, 100, 100, 100, 100)
  val cropper = new Cropper
  cropper.setWidth("100px");
  cropper.setHeight("150px");

  cropper.addListener(new CropperChangeListener {
    override def valueChange(c: Crops) {
      crops = c;
      eventRouter.fireEvent(new DoConversionEvent(true, true))
    }
  })

  val statusLayout = new HorizontalLayout
  val statusCaptionLabel = new Label("Status: ")
  statusCaptionLabel.setWidth(captionWidth)
  val statusLabel = new Label("Ready...")
  statusLayout.setSpacing(true)
  statusLayout.addComponent(statusCaptionLabel)
  statusLayout.setComponentAlignment(statusCaptionLabel, Alignment.MIDDLE_LEFT)
  statusLayout.addComponent(statusLabel)
  statusLayout.setComponentAlignment(statusLabel, Alignment.MIDDLE_LEFT)

  val urlLayout = new HorizontalLayout
  val urlCaptionLabel = new Label("Image URL: ")
  urlCaptionLabel.setWidth(captionWidth)
  val inputTextField = new TextField
  inputTextField.setWidth("570px")
  inputTextField.focus
  inputTextField.setImmediate(true)
  val convButton = new Button("Convert Image")
  urlLayout.setSpacing(true)
  urlLayout.addComponent(urlCaptionLabel)
  urlLayout.setComponentAlignment(urlCaptionLabel, Alignment.MIDDLE_LEFT)
  urlLayout.addComponent(inputTextField)
  urlLayout.setComponentAlignment(inputTextField, Alignment.MIDDLE_LEFT)
  urlLayout.addComponent(convButton)
  urlLayout.setComponentAlignment(convButton, Alignment.MIDDLE_LEFT)

  val submissionLayout = new HorizontalLayout
  val submissionCaptionLabel = new Label("Name: ")
  submissionCaptionLabel.setWidth(captionWidth)
  val submitter = new Submitter(eventRouter)
  submissionLayout.setSpacing(true)
  submissionLayout.addComponent(submissionCaptionLabel)
  submissionLayout.setComponentAlignment(submissionCaptionLabel, Alignment.MIDDLE_LEFT)
  submissionLayout.addComponent(submitter)
  submissionLayout.setComponentAlignment(submitter, Alignment.MIDDLE_LEFT)

  val cropLayout = new HorizontalLayout
  val cropCaptionLabel = new Label("Cropping: ")
  cropCaptionLabel.setWidth(captionWidth)
  cropLayout.setSpacing(true)
  cropLayout.addComponent(cropCaptionLabel)
  cropLayout.setComponentAlignment(cropCaptionLabel, Alignment.MIDDLE_LEFT)
  val resetButton = new Button("Reset Selection")
  resetButton.addClickListener(new Button.ClickListener {
    override def buttonClick(event: ClickEvent) {
      cropper.setImageSrc(cropper.getImageSrc)
    }
  });
  cropLayout.addComponent(resetButton)
  cropLayout.setComponentAlignment(resetButton, Alignment.MIDDLE_LEFT)

  bind

  setSpacing(true)
  setMargin(true)
  //   0   1
  //0 ### [Status]
  //1 ### [Url   ]
  //2 ### [Sub   ]
  //3 ### [Crop  ]
  setRows(4)
  setColumns(3)

  addComponent(cropper, 0, 0, 0, 3)
  setComponentAlignment(cropper, Alignment.TOP_LEFT)
  addComponent(statusLayout, 1, 0)
  setComponentAlignment(statusLayout, Alignment.MIDDLE_LEFT)
  addComponent(urlLayout, 1, 1)
  setComponentAlignment(urlLayout, Alignment.MIDDLE_LEFT)
  addComponent(submissionLayout, 1, 2)
  setComponentAlignment(submissionLayout, Alignment.MIDDLE_LEFT)
  addComponent(cropLayout, 1, 3)
  setComponentAlignment(cropLayout, Alignment.MIDDLE_LEFT)

  private def bind() {
    inputTextField.addFocusListener(new FocusListener {
      override def focus(event: FocusEvent) { inputTextField.selectAll }
    })
    convButton.addClickListener(new Button.ClickListener {
      override def buttonClick(event: ClickEvent) { replaceUrl }
    })
  }
  private def focusShowButton { inputTextField.focus }
  private def replaceImage(url: URL) {
    cropper.setImageSrc(url.toString)
  }
  private def trigger { eventRouter.fireEvent(new DoConversionEvent(true, true)) }
  private def updateLabel(status: String, error: String) {
    Option(error) match {
      case Some(error) => statusLabel.setComponentError(new UserError(error))
      case None => statusLabel.setComponentError(null)
    }
    statusLabel.setValue(status)
  }
  def setStatus(status: String) { updateLabel(status, null) }
  def setError(error: String) { updateLabel(error, error) }
  def addIcon(img: BufferedImage) { submitter.addIcon(img) }
  def setName(name: String) { submitter.setName(name) }
  def getName: String = submitter.getName
  def isInvert: Boolean = submitter.isInvert
  def getBg: String = submitter.getBg
  def enableSubmission(enabled: Boolean) { submitter.enableSubmission(enabled) }
  def getCrops: (Int, Int, Int, Int) = (crops.x, crops.x2, crops.y, crops.y2)
  def hasLink: Boolean = {
    Option(inputTextField.getValue) match {
      case Some(value) => !value.equals("")
      case None => false
    }
  }
  def setLink(link: String) {
    inputTextField.setValue(link)
    UrlOptionizer.extract(link) match {
      case Some(u) =>
        replaceImage(u)
        Page.getCurrent.setUriFragment("main/" + link)
      case None => setError("URL is not Valid. Please enter URL to an image: ")
    }
  }
  private def replaceUrl {
    val currentFrag = Page.getCurrent.getUriFragment
    val currentUrl = inputTextField.getValue
    Option(currentFrag) match {
      case Some(frag) =>
        if (!currentFrag.endsWith(currentUrl)) {
          UrlOptionizer.extract(currentUrl) match {
            case Some(u) =>
              Page.getCurrent.setUriFragment("main/" + currentUrl)
              replaceImage(u)
            case None => setError("URL is not Valid. Please enter URL to an image: ")
          }
        } else { trigger }
      case None => { trigger }
    }
  }
  def getUrl: Option[String] = {
    UrlOptionizer.extract(inputTextField.getValue) match {
      case Some(u) => Option(u.toString)
      case None => None
    }
  }
  def reset { submitter.reset }
}
