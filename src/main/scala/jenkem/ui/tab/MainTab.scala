package jenkem.ui.tab

import java.awt.image.BufferedImage
import java.util.Date
import scala.collection.JavaConversions.seqAsJavaList
import com.vaadin.data.Property
import com.vaadin.data.Property.ValueChangeEvent
import com.vaadin.event.EventRouter
import com.vaadin.shared.ui.label.ContentMode
import com.vaadin.ui.AbsoluteLayout
import com.vaadin.ui.Alignment
import com.vaadin.ui.Button
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.CheckBox
import com.vaadin.ui.Component
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Label
import com.vaadin.ui.ListSelect
import com.vaadin.ui.NativeSelect
import com.vaadin.ui.OptionGroup
import com.vaadin.ui.Slider
import com.vaadin.ui.TextField
import com.vaadin.ui.VerticalLayout
import javax.jdo.annotations.PersistenceCapable
import jenkem.engine.ConversionMethod
import jenkem.engine.Engine
import jenkem.engine.Kick
import jenkem.engine.Pal
import jenkem.engine.color.Power
import jenkem.event.DoConversionEvent
import jenkem.event.SendToIrcEvent
import jenkem.persistence.PersistenceService
import jenkem.persistence.data.ImageCss
import jenkem.persistence.data.ImageHtml
import jenkem.persistence.data.ImageInfo
import jenkem.persistence.data.ImageIrc
import jenkem.persistence.data.JenkemImage
import jenkem.ui.ImagePreparer
import jenkem.ui.Inline
import jenkem.ui.IrcColorSetter
import jenkem.ui.IrcConnector
import jenkem.ui.OutputDisplay
import jenkem.ui.ProcSetter
import jenkem.util.AwtImageUtil
import jenkem.util.HtmlUtil
import jenkem.util.InitUtil
import com.vaadin.ui.Notification
import com.vaadin.server.Page

class MainTab(val eventRouter: EventRouter) extends VerticalLayout {
  val nbsp = "&nbsp;"

  val capW = 150
  val defaultWidth = 68
  val minWidth = 16
  val maxWidth = 74

  var conversionDisabled = true
  var ircOutput: List[String] = Nil
  var imagePrep: ImagePreparationData = _
  var imageData: ImageData = _
  var convData: ConversionData = _

  setCaption("Main")
  setSizeFull
  setMargin(true)
  setSpacing(true)

  class ImagePreparationData(val icon: BufferedImage, val originalName: String)
  class ImageData(val imageRgb: Map[(Int, Int), (Short, Short, Short)],
      val width: Short, val height: Short, val lineWidth: Short, val kick: Kick.Value,
      val method: ConversionMethod.Value)
  class ConversionData(val contrast: Short, val brightness: Short, val characters: String)

  val resizeValueChangeListener = new Property.ValueChangeListener {
    override def valueChange(event: ValueChangeEvent) {
      if(!conversionDisabled) { startConversion(false, true) }}}

  val noResizeValueChangeListener = new Property.ValueChangeListener {
    override def valueChange(event: ValueChangeEvent) {
      if(!conversionDisabled) { startConversion(false, false) }}}
  
  val saveNotification = new Notification("Image submitted to gallery.", Notification.Type.HUMANIZED_MESSAGE)
  saveNotification.setDelayMsec(1500)

  val mainLayout = new HorizontalLayout
  mainLayout.setMargin(true)
  mainLayout.setSpacing(true)
  val settingsLayout = new VerticalLayout
  settingsLayout.setSpacing(true)

  val inline = new Inline
  val inlineLayout = new VerticalLayout

  val kickLayout = new HorizontalLayout
  val kickLabel = new Label("Kick: ")
  kickLabel.setWidth("88px")
  val kickSelect = new OptionGroup(null, Kick.values)
  kickSelect.addStyleName("horizontal");
  kickSelect.setNullSelectionAllowed(false)
  kickSelect.setImmediate(true)
  kickSelect.addValueChangeListener(resizeValueChangeListener)
  kickLayout.addComponent(kickLabel)
  kickLayout.addComponent(kickSelect)
  inlineLayout.addComponent(kickLayout)

  val imagePreparer = new ImagePreparer(eventRouter)
  imagePreparer.enableSubmission(false)
  addComponent(imagePreparer)
  addComponent(mainLayout)

  val lwLayout = new HorizontalLayout
  val lwCaptionLabel = new Label("Line Width: ")
  lwCaptionLabel.setWidth("88px")
  lwLayout.addComponent(lwCaptionLabel)
  lwLayout.setComponentAlignment(lwCaptionLabel, Alignment.MIDDLE_LEFT)
  val widthSlider = new Slider
  widthSlider.setMin(minWidth)
  widthSlider.setValue(defaultWidth)
  widthSlider.setMax(maxWidth)
  widthSlider.addValueChangeListener(resizeValueChangeListener)
  widthSlider.setWidth("350px")
  lwLayout.addComponent(widthSlider)
  lwLayout.setComponentAlignment(widthSlider, Alignment.MIDDLE_LEFT)
  inlineLayout.addComponent(lwLayout)
  inlineLayout.addComponent(inline)
  mainLayout.addComponent(inlineLayout)

  mainLayout.addComponent(settingsLayout)

  val methodBox = makeListMethodSelect("Conversion Method: ")
  val resetButton = new Button("Reset")
  settingsLayout.addComponent(makeLabeled("Reset All Settings: ", capW, resetButton))
  val (contrastSlider, contrastLabel) = makeSliderAndLabel("Contrast: ", -100, 100, 0)
  val (brightnessSlider, brightnessLabel) = makeSliderAndLabel("Brightness: ", -100, 100, 0)

  val charsetLayout = new HorizontalLayout
  val charsetBox = new NativeSelect
  charsetBox.setNullSelectionAllowed(false)
  charsetBox.setImmediate(true)
  charsetBox.setWidth("120px")
  val charTextField = new TextField
  charTextField.setWidth("125px")
  charsetLayout.addComponent(charsetBox)
  charsetLayout.addComponent(charTextField)
  settingsLayout.addComponent(makeLabeled("Characters: ", capW, charsetLayout))
  val procSetter = new ProcSetter(eventRouter)
  settingsLayout.addComponent(makeLabeled("Processing: ", capW, procSetter))
  settingsLayout.addComponent(new Label(nbsp, ContentMode.HTML))
  val powerBox = makeNativeSelect("Power: ")
  val ircColorSetter = new IrcColorSetter(eventRouter)
  settingsLayout.addComponent(ircColorSetter)
  settingsLayout.addComponent(new Label(nbsp, ContentMode.HTML))
  val outputDisplay = new OutputDisplay
  settingsLayout.addComponent(outputDisplay)
  settingsLayout.addComponent(new Label(nbsp, ContentMode.HTML))
  val ircConnector = new IrcConnector(eventRouter)
  settingsLayout.addComponent(ircConnector)

  resetButton.addClickListener(new Button.ClickListener {
    override def buttonClick(event: ClickEvent) {
      doReset //triggers conversion
      startConversion(false, false)
    }
  })

  Pal.charsets.foreach(cb => charsetBox.addItem(cb))
  charsetBox.addValueChangeListener(new Property.ValueChangeListener {
    override def valueChange(event: ValueChangeEvent) {
      val charsetName = event.getProperty.getValue.toString
      Pal.valueOf(charsetName) match {
        case Some(pal) => 
          makeInitsForCharset(pal.chars)
          if (!pal.chars.equals(charTextField.getValue)) {
             //triggers conversion
            charTextField.setValue(pal.chars)
          }
        case None => imagePreparer.setError("Charset not found.")
      }
    }
  })

  charTextField.setImmediate(true)
  charTextField.addValueChangeListener(noResizeValueChangeListener)

  Power.values.foreach(p => powerBox.addItem(p))
  powerBox.addValueChangeListener(noResizeValueChangeListener)

  eventRouter.addListener(classOf[DoConversionEvent], new {
    def convert(e: DoConversionEvent) {
      startConversion(e.prepareImage, e.resize)
    }}, "convert")
  eventRouter.addListener(classOf[SendToIrcEvent], new {
    def send { ircConnector.sendToIrc(ircOutput) }}, "send")

  doReset

  private def makeNativeSelect(caption: String): NativeSelect = {
    val box = new NativeSelect
    box.setNullSelectionAllowed(false)
    box.setImmediate(true)
    val layout = makeLabeled(caption, capW, box)
    settingsLayout.addComponent(layout)
    box
  }

  private def makeListMethodSelect(caption: String): ListSelect = {
    val settings = new ListSelect
    settings.addValueChangeListener(new Property.ValueChangeListener {
      override def valueChange(event: ValueChangeEvent) {
        conversionDisabled = true
        makeInitsForMethod
        conversionDisabled = false
        startConversion(false, true)
      }
    })
    settings.setRows(ConversionMethod.values.length)
    ConversionMethod.values.map(settings.addItem(_))
    settings.setNullSelectionAllowed(false)
    settings.setImmediate(true)
    val layout = makeLabeled(caption, capW, settings)
    settingsLayout.addComponent(layout)
    settings
  }

  private def makeLabeled(caption: String, width: Int, component: Component) = {
    val layout = new HorizontalLayout
    val captionLabel = new Label(caption)
    captionLabel.setWidth(width + "px")
    component.setWidth("250px")
    layout.addComponent(captionLabel)
    layout.addComponent(component)
    layout
  }

  private def makeCheckBox(caption: String) = {
    val box = new CheckBox(caption)
    box.setImmediate(true)
    box.addValueChangeListener(noResizeValueChangeListener)
    settingsLayout.addComponent(box)
    box
  }

  private def makeSliderAndLabel(caption: String, min: Int, max: Int, default: Int): (Slider, Label) = {
    val layout = new AbsoluteLayout
    val label = new Label(default.toString)
    label.setWidth("30px")
    label.setStyleName("sliderLabel")
    val slider = new Slider("")
    slider.setWidth("220px")
    slider.setMin(min)
    slider.setMax(max)
    slider.setImmediate(true)
    slider.addValueChangeListener(new Property.ValueChangeListener {
      override def valueChange(event: ValueChangeEvent) {
        label.setValue("%1.0f".format(event.getProperty.getValue))
        startConversion(false, false)
      }
    })
    layout.setWidth("100%")
    layout.addComponent(slider, "left:0px")
    layout.addComponent(label, "left:220px")
    settingsLayout.addComponent(makeLabeled(caption, capW, layout))
    (slider, label)
  }

  private def doReset() {
    conversionDisabled = true
    methodBox.select(ConversionMethod.Vortacular)
    conversionDisabled = true
    widthSlider.setValue(defaultWidth)
    charTextField.setValue(Pal.Ansi.chars)
    procSetter.reset(true)
    kickSelect.select(Kick.default)
    powerBox.select(Power.Linear)
    contrastSlider.setValue(0)
    brightnessSlider.setValue(0)
    ircColorSetter.reset
    charsetBox.select(Pal.Ansi) //unsets conversionDisabled!
    conversionDisabled = false
  }

  private def doPrepareImage(url: String) {
    val crops = imagePreparer.getCrops
    val invert = imagePreparer.isInvert
    val bg = imagePreparer.getBg
    val icon = AwtImageUtil.makeIcon(url, bg, invert, crops)
    imagePreparer.addIcon(icon)
    imagePreparer.setName(url.split("/").last)
    imagePreparer.enableSubmission(true)
    imagePrep = new ImagePreparationData(icon, imagePreparer.getName)
  }

  private def doPrepareImageData(url: String) {
    val kick = Kick.valueOf(kickSelect.getValue.toString).get
    val crops = imagePreparer.getCrops
    val invert = imagePreparer.isInvert
    val bg = imagePreparer.getBg
    val originalImage = AwtImageUtil.bufferImage(url, bg, invert, crops)
    val originalWidth = originalImage.getWidth
    val originalHeight = originalImage.getHeight
    val lineWidth = widthSlider.getValue.shortValue
    val method = ConversionMethod.valueOf(methodBox.getValue.toString).get
    val (width, height) = AwtImageUtil.calculateNewSize(lineWidth, originalWidth, originalHeight)
    val imageRgb = AwtImageUtil.getImageRgb(originalImage, width, height, kick)
    val dataWidth = (width - (2 * kick.xOffset)).shortValue
    val dataHeight = (height - (2 * kick.yOffset)).shortValue
    imageData = new ImageData(imageRgb, dataWidth, dataHeight, lineWidth, kick, method)
  }

  private def startConversion(prepareImage: Boolean, resize: Boolean) {
    if (!conversionDisabled) {
      conversionDisabled = true
      imagePreparer.getUrl match {
        case Some(url) =>
          if (prepareImage) { doPrepareImage(url) }
          if (resize) { doPrepareImageData(url) }
          if (prepareImage) {
            makeInits
            conversionDisabled = false
            startConversion(false, false)
          } else { makeConversion }
        case None => { }
      }
    }
  }

  private def makeConversion() {
    try {
      val chars = charTextField.getValue.replaceAll("[,0-9]", "")
      val contrast = contrastLabel.getValue.toShort
      val brightness = brightnessLabel.getValue.toShort
      convData = new ConversionData(contrast, brightness, chars)
      val ps = procSetter.getSettings
      val params = new Engine.Params(
        imageData.method,
        imageData.imageRgb,
        ircColorSetter.getColorMap,
        chars,
        ps,
        contrast,
        brightness,
        Power.valueOf(powerBox.getValue.toString).get
      )
      ircOutput = generateIrcOutput(params, imageData.height)
      outputDisplay.addIrcOutput(ircOutput.map(_ + "\n"))
      updateInline(imageData.method, ircOutput, imagePreparer.getName)
      imagePreparer.setStatus("Ready...")
      ircConnector.refresh
      conversionDisabled = false
    } catch {
      case iioe: javax.imageio.IIOException => imagePreparer.setError("Cannot read image from URL.")
      case e: Exception => imagePreparer.setError(e.getMessage)
    }
  }

  private def generateIrcOutput(params: Engine.Params, lastIndex: Int) = {
    def generate0(index: Int): List[String] = {
      if (index + 2 > lastIndex) { Nil }
      else { Engine.generateLine(params, index) :: generate0(index + 2) }
    }
    generate0(0)
  }

  private def updateInline(method: ConversionMethod.Value, ircOutput: List[String], name: String) {
    val htmlAndCss = HtmlUtil.generateHtml(ircOutput, name, method)
    val inlineCss = HtmlUtil.prepareCssForInline(htmlAndCss._2)
    val inlineHtml = HtmlUtil.prepareHtmlForInline(htmlAndCss._1, inlineCss)
    inline.setValue(inlineHtml)
  }

  private def makeInits() {
    conversionDisabled = true
    val chars = charTextField.getValue.replaceAll("[,0-9]", "")
    makeImageInits
    conversionDisabled = false
  }

  private def makeImageInits() {
    conversionDisabled = true
    val con = InitUtil.getDefaultContrast(imageData.imageRgb)
    contrastSlider.setValue(con)
    val bri = InitUtil.getDefaultBrightness(imageData.imageRgb)
    brightnessSlider.setValue(bri)
    val (method, scheme, charset) = InitUtil.getDefaults(imageData.imageRgb)
    methodBox.setValue(method)
    ircColorSetter.setSelectedScheme(scheme)
    charsetBox.select(charset)
    conversionDisabled = false
  }

  private def makeInitsForMethod() {
    conversionDisabled = true
    val method = ConversionMethod.valueOf(methodBox.getValue.toString).get
    ircColorSetter.makeEnabled(method.equals(ConversionMethod.Vortacular))
    powerBox.setEnabled(method.equals(ConversionMethod.Vortacular))
    procSetter.reset(Pal.hasAnsi(charTextField.getValue))
    charsetBox.setValue(Pal.getForMethod(method)) //unsets conversionDisabled!
    conversionDisabled = false
  }

  private def makeInitsForCharset(chars: String) = procSetter.reset(Pal.hasAnsi(chars))

  def saveImage() {
    val name = imagePreparer.getName
    val htmlAndCss = HtmlUtil.generateHtml(ircOutput, name, imageData.method)
    val format = new java.text.SimpleDateFormat("yyyy.MM.dd HH:mm:ss")
    val base64Icon = AwtImageUtil.encodeToBase64(imagePrep.icon)
    val jenkemImageInfo = new ImageInfo(name, base64Icon, imageData.method.name, convData.characters,
        convData.contrast.shortValue, convData.brightness.shortValue, ircOutput.size.shortValue,
        imageData.lineWidth.shortValue, format.format(new Date)
    )
    val jenkemImageHtml = new ImageHtml(name, htmlAndCss._1)
    val jenkemImageCss = new ImageCss(name, htmlAndCss._2)
    val jenkemImageIrc = new ImageIrc(name, ircOutput.map(_ + "\n").mkString)
    val jenkemImage = new JenkemImage(jenkemImageInfo, jenkemImageHtml, jenkemImageCss, jenkemImageIrc)
    PersistenceService.saveJenkemImage(jenkemImage)
    saveNotification.show(Page.getCurrent)
  }

  def hasLink: Boolean = imagePreparer.hasLink
  def setLink(link: String) { imagePreparer.setLink(link) }

}
