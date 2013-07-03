package jenkem.ui

import java.awt.image.BufferedImage
import java.net.URL
import com.vaadin.annotations.JavaScript
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
import com.vaadin.ui.Slider
import com.vaadin.ui.Layout
import com.vaadin.data.Property
import com.vaadin.data.Property.ValueChangeEvent
import com.vaadin.ui.Component
import com.vaadin.ui.AbstractOrderedLayout

class ImagePreparer(val eventRouter: EventRouter) extends GridLayout {
  val captionWidth = "100px"

  var disableTrigger = false
  var crops = new Crops(0, 0, 100, 100, 100, 100)
  val cropper = new Cropper
  cropper.setWidth("100px")
  cropper.setHeight("150px")

  cropper.addListener(new CropperChangeListener {
    override def valueChange(c: Crops) {
      crops = c
      val cropText = "Top: -" + crops.y + "% Bottom: -" + (100 - crops.y2) +
              "% Left: -" + crops.x + "% Right: -" + (100 - crops.x2) + "%"
      cropLabel.setValue(cropText)
      trigger
    }
  })

  val statusLayout = new HorizontalLayout
  val statusCaptionLabel = new Label("Status: ")
  statusCaptionLabel.setWidth(captionWidth)
  val statusLabel = new Label("Ready...")
  statusLayout.setSpacing(true)
  addToLayoutLeft(statusLayout, statusCaptionLabel)
  addToLayoutLeft(statusLayout, statusLabel)

  val urlLayout = new HorizontalLayout
  val urlCaptionLabel = new Label("Image URL: ")
  urlCaptionLabel.setWidth(captionWidth)
  val inputTextField = new TextField
  inputTextField.setWidth("570px")
  inputTextField.focus
  inputTextField.setImmediate(true)
  val convButton = new Button("Convert Image")
  urlLayout.setSpacing(true)
  addToLayoutLeft(urlLayout, urlCaptionLabel)
  addToLayoutLeft(urlLayout, inputTextField)
  addToLayoutLeft(urlLayout, convButton)

  val submissionLayout = new HorizontalLayout
  val submissionCaptionLabel = new Label("Name: ")
  submissionCaptionLabel.setWidth(captionWidth)
  val submitter = new Submitter(eventRouter)
  submissionLayout.setSpacing(true)
  addToLayoutLeft(submissionLayout, submissionCaptionLabel)
  addToLayoutLeft(submissionLayout, submitter)
  val resetAllButton = new Button("Reset All")
  resetAllButton.addClickListener(new Button.ClickListener {
    override def buttonClick(event: ClickEvent) {
      disableTrigger = true
      submitter.reset
      brightnessSlider.setValue(0)
      contrastSlider.setValue(0)
      disableTrigger = false
      cropper.setImageSrc(cropper.getImageSrc)
    }
  })
  addToLayoutLeft(submissionLayout, resetAllButton)

  val brightnessLayout = new HorizontalLayout
  brightnessLayout.setSpacing(true)
  val (brightnessSlider, brightnessLabel) = makeSliderAndLabel("Brightness: ", -100, 100, 0, brightnessLayout)

  val contrastLayout = new HorizontalLayout
  contrastLayout.setSpacing(true)
  val (contrastSlider, contrastLabel) = makeSliderAndLabel("Contrast: ", -70, 70, 0, contrastLayout)

  val cropLayout = new HorizontalLayout
  val cropCaptionLabel = new Label("Cropping: ")
  cropCaptionLabel.setWidth(captionWidth)
  cropLayout.setSpacing(true)
  addToLayoutLeft(cropLayout, cropCaptionLabel)
  val resetButton = new Button("Select All")
  resetButton.addClickListener(new Button.ClickListener {
    override def buttonClick(event: ClickEvent) {
      cropper.setImageSrc(cropper.getImageSrc)
    }
  })
  addToLayoutLeft(cropLayout, resetButton)
  val cropLabel = new Label
  addToLayoutLeft(cropLayout, cropLabel)

  bind

  setSpacing(true)
  setMargin(true)
  //   0   1
  //0 ### [Status]
  //1 ### [Url   ]
  //2 ### [Bright]
  //3 ### [Cont  ]
  //4 ### [Sub   ]
  //5 ### [Crop  ]
  setRows(6)
  setColumns(2)

  addComponent(cropper, 0, 0, 0, 5)
  setComponentAlignment(cropper, Alignment.TOP_LEFT)
  addMiddleLeft(statusLayout, 1, 0)
  addMiddleLeft(urlLayout, 1, 1)
  addMiddleLeft(brightnessLayout, 1, 2)
  addMiddleLeft(contrastLayout, 1, 3)
  addMiddleLeft(submissionLayout, 1, 4)
  addMiddleLeft(cropLayout, 1, 5)

  private def addMiddleLeft(comp: Component, col: Int, row: Int): Unit = {
    addComponent(comp, col, row)
    setComponentAlignment(comp, Alignment.MIDDLE_LEFT)
  }

  private def addToLayoutLeft(layout: AbstractOrderedLayout, comp: Component): Unit = {
    layout.addComponent(comp)
    layout.setComponentAlignment(comp, Alignment.MIDDLE_LEFT)
  }

  private def makeSliderAndLabel(caption: String, min: Int, max: Int, default: Int, layout: HorizontalLayout): (Slider, Label) = {
    val label = new Label(default.toString)
    label.setWidth("30px")
    label.setStyleName("sliderLabel")
    val slider = new Slider(None.orNull)
    slider.setWidth("570px")
    slider.setMin(min)
    slider.setMax(max)
    slider.setValue(default)
    slider.setImmediate(true)
    slider.addValueChangeListener(new Property.ValueChangeListener {
      override def valueChange(event: ValueChangeEvent): Unit = {
        label.setValue("%1.0f".format(event.getProperty.getValue))
        trigger
      }
    })
    val button = new Button("Reset")
    button.addClickListener(new Button.ClickListener {
      override def buttonClick(event: ClickEvent) {
        slider.setValue(default)
      }
    })
    val captionLabel = new Label(caption)
    captionLabel.setWidth(captionWidth)
    addToLayoutLeft(layout, captionLabel)
    addToLayoutLeft(layout, slider)
    addToLayoutLeft(layout, label)
    addToLayoutLeft(layout, button)
    (slider, label)
  }

  def getBrightness(): Int = brightnessLabel.getValue.toInt
  def getContrast(): Int = contrastLabel.getValue.toInt

  private def bind(): Unit = {
    inputTextField.addFocusListener(new FocusListener {
      override def focus(event: FocusEvent): Unit = inputTextField.selectAll
    })
    convButton.addClickListener(new Button.ClickListener {
      override def buttonClick(event: ClickEvent): Unit = replaceUrl
    })
  }
  private def replaceImage(url: URL): Unit = cropper.setImageSrc(url.toString)
  private def trigger(): Unit = {
    if (!disableTrigger) {
      eventRouter.fireEvent(new DoConversionEvent(true, true))
    }
  }
  private def updateLabel(status: String, error: String): Unit = {
    Option(error) match {
      case Some(error) => statusLabel.setComponentError(new UserError(error))
      case None => statusLabel.setComponentError(None.orNull)
    }
    statusLabel.setValue(status)
  }
  def setStatus(status: String): Unit = updateLabel(status, None.orNull)
  def setError(error: String): Unit = updateLabel(error, error)
  def addIcon(img: BufferedImage): Unit = submitter.addIcon(img)
  def setName(name: String): Unit = submitter.setName(name)
  def getName: String = submitter.getName
  def isInvert: Boolean = submitter.isInvert
  def getBg: String = submitter.getBg
  def enableSubmission(enabled: Boolean): Unit = submitter.enableSubmission(enabled)
  def getCrops: (Int, Int, Int, Int) = (crops.x, crops.x2, crops.y, crops.y2)
  def hasLink: Boolean = {
    Option(inputTextField.getValue) match {
      case Some(value) => !value.equals("")
      case None => false
    }
  }
  def setLink(link: String): Unit = {
    inputTextField.setValue(link)
    UrlOptionizer.extract(link) match {
      case Some(u) =>
        replaceImage(u)
        Page.getCurrent.setUriFragment("main/" + link)
      case None => setError("URL is not Valid. Please enter URL to an image: ")
    }
  }
  private def replaceUrl(): Unit = {
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
  def reset: Unit = submitter.reset
}
