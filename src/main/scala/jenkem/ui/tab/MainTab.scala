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
import jenkem.engine.ConversionMethod
import jenkem.engine.Engine
import jenkem.engine.Kick
import jenkem.engine.color.Power
import jenkem.event.DoConversionEvent
import jenkem.event.SendToIrcEvent
import jenkem.persistence.PersistenceService
import jenkem.shared.ImageUtil
import jenkem.shared.data.ImageCss
import jenkem.shared.data.ImageHtml
import jenkem.shared.data.ImageInfo
import jenkem.shared.data.ImageIrc
import jenkem.shared.data.JenkemImage
import jenkem.ui.ImagePreparer
import jenkem.ui.Inline
import jenkem.ui.IrcColorSetter
import jenkem.ui.IrcConnector
import jenkem.ui.OutputDisplay
import jenkem.ui.ProcSetter
import jenkem.util.AwtImageUtil
import jenkem.util.HtmlUtil
import jenkem.engine.Pal

class MainTab(val eventRouter: EventRouter) extends VerticalLayout {
  val engine = new Engine
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

  class ImagePreparationData(val icon: BufferedImage, val originalName: String)
  class ImageData(val imageRgb: Map[(Int, Int), (Short, Short, Short)],
      val width: Int, val height: Int, val lineWidth: Int, val kick: Kick.Value,
      val method: ConversionMethod.Value)
  class ConversionData(val contrast: Int, val brightness: Int,
      val characters: String)

  val resizeValueChangeListener = new Property.ValueChangeListener {
    override def valueChange(event: ValueChangeEvent) { startConversion(false, true) }
  }

  val noResizeValueChangeListener = new Property.ValueChangeListener {
    override def valueChange(event: ValueChangeEvent) { startConversion(false, false) }
  }

  setCaption("Main")
  setSizeFull
  setMargin(true)
  setSpacing(true)

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
  val kickSelect = new OptionGroup(null, Kick.getAll)
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
      doReset; startConversion(true, true)
    }
  })

  Pal.charsets.foreach(cb => charsetBox.addItem(cb))
  charsetBox.addValueChangeListener(new Property.ValueChangeListener {
    override def valueChange(event: ValueChangeEvent) {
      val charsetName = event.getProperty.getValue.toString
      charTextField.setValue(Pal.valueOf(charsetName).chars)
      makeInitsForCharset
      startConversion(false, false)
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
  eventRouter.addListener(classOf[SendToIrcEvent],
      new { def send { ircConnector.sendToIrc(ircOutput) }}, "send")

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
        makeInitsForMethod
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
    widthSlider.setValue(defaultWidth)
    charsetBox.select(Pal.Ansi)
    charTextField.setValue(Pal.Ansi.chars)
    procSetter.reset(true)
    kickSelect.select(Kick.default)
    powerBox.select(Power.Linear)
    contrastSlider.setValue(0)
    brightnessSlider.setValue(0)
    ircColorSetter.reset
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
    val kick = Kick.valueOf(kickSelect.getValue.toString)
    val crops = imagePreparer.getCrops
    val invert = imagePreparer.isInvert
    val bg = imagePreparer.getBg
    val originalImage = AwtImageUtil.bufferImage(url, bg, invert, crops)
    val originalWidth = originalImage.getWidth
    val originalHeight = originalImage.getHeight
    val lineWidth = widthSlider.getValue.intValue
    val method = ConversionMethod.valueOf(methodBox.getValue.toString)
    //val (width, height) = AwtImageUtil.calculateNewSize(method, lineWidth, originalWidth, originalHeight)
    val (width, height) = AwtImageUtil.calculateNewSize(lineWidth, originalWidth, originalHeight)
    val imageRgb = AwtImageUtil.getImageRgb(originalImage, width, height, kick)
    val dataWidth = width - (2 * kick.xOffset)
    val dataHeight = height - (2 * kick.yOffset)
    imageData = new ImageData(imageRgb, dataWidth, dataHeight, lineWidth, kick, method)
  }

  private def startConversion(prepareImage: Boolean, resize: Boolean) {
    if (!conversionDisabled) {
      imagePreparer.getUrl match {
        case Some(url) =>
          if (prepareImage) { doPrepareImage(url) }
          if (resize) { doPrepareImageData(url) }
          if (prepareImage) { makeInits; startConversion(false, false) }
          else { makeConversion }
        case None => { }
      }
    }
  }

  private def makeConversion() {
    try {
      val chars = charTextField.getValue.replaceAll("[,0-9]", "")
      val contrast = contrastLabel.getValue.toInt
      val brightness = brightnessLabel.getValue.toInt
      convData = new ConversionData(contrast, brightness, chars)
      val ps = procSetter.getSettings
      engine.setParams(imageData.imageRgb, imageData.width, chars, convData.contrast, convData.brightness , ps)
      engine.prepareEngine(ircColorSetter.getColorMap, Power.valueOf(powerBox.getValue.toString))
      ircOutput = generateIrcOutput(imageData.method, imageData.height)
      outputDisplay.addIrcOutput(ircOutput.map(_ + "\n"))
      updateInline(imageData.method, ircOutput, imagePreparer.getName)
      imagePreparer.setStatus("Ready...")
      ircConnector.refresh
    } catch {
      case iioe: javax.imageio.IIOException => imagePreparer.setError("Cannot read image from URL.")
      case iae: IllegalArgumentException => imagePreparer.setError(iae.getMessage)
      case e: Exception => imagePreparer.setError(e.getMessage)
    }
  }

  private def generateIrcOutput(method: ConversionMethod.Value, lastIndex: Int) = {
    def generate0(index: Int): List[String] = {
      if (index + 2 > lastIndex) { Nil }
      else { engine.generateLine(method, index) :: generate0(index + 2) }
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
    val imageRgb = imageData.imageRgb
    val width = imageData.width
    val height = imageData.height
    val chars = charTextField.getValue.replaceAll("[,0-9]", "")

    //FIXME replace this and use imageRgb directly!!!
    val legacyImageRgb = engine.makeLegacy(imageRgb)
    //////////////////

    val defaultScheme = ImageUtil.getDefaultColorScheme(legacyImageRgb, width, height)
    ircColorSetter.setSelectedScheme(defaultScheme)
    val con = jenkem.shared.ImageUtil.getDefaultContrast(legacyImageRgb, width, height) - 100
    contrastSlider.setValue(con)
    val bri = jenkem.shared.ImageUtil.getDefaultBrightness(legacyImageRgb, width, height) - 100
    brightnessSlider.setValue(bri)
    conversionDisabled = false
  }

  private def makeInitsForMethod() {
    conversionDisabled = true
    val method = ConversionMethod.valueOf(methodBox.getValue.toString)
    ircColorSetter.makeEnabled(method.equals(ConversionMethod.Vortacular))
    powerBox.setEnabled(method.equals(ConversionMethod.Vortacular))
    if (method.equals(ConversionMethod.Plain)) {
      charsetBox.setValue(Pal.Hard)
    } else if (method.equals(ConversionMethod.Stencil)) {
      charsetBox.setValue(Pal.HCrude)
    } else {
      charsetBox.setValue(Pal.Ansi)
    }
    procSetter.reset(Pal.hasAnsi(charTextField.getValue))
    conversionDisabled = false
  }

  private def makeInitsForCharset() {
    conversionDisabled = true
    val chars = charTextField.getValue
    procSetter.reset(Pal.hasAnsi(charTextField.getValue))
    conversionDisabled = false
  }

  def saveImage() {
    val name = imagePreparer.getName
    val htmlAndCss = HtmlUtil.generateHtml(ircOutput, name, imageData.method)
    val format = new java.text.SimpleDateFormat("yyyy.MM.dd HH:mm:ss")
    val base64Icon = AwtImageUtil.encodeToBase64(imagePrep.icon)
    val ints = Array(convData.contrast, convData.brightness, ircOutput.size, imageData.lineWidth)
    val jenkemImageInfo = new ImageInfo(
        name, base64Icon, imageData.method.name, convData.characters, ints, format.format(new Date)
    )
    val jenkemImageHtml = new ImageHtml(name, htmlAndCss._1)
    val jenkemImageCss = new ImageCss(name, htmlAndCss._2)
    val jenkemImageIrc = new ImageIrc(name, ircOutput.map(_ + "\n").mkString)
    val jenkemImage = new JenkemImage(jenkemImageInfo, jenkemImageHtml, jenkemImageCss, jenkemImageIrc)
    PersistenceService.saveJenkemImage(jenkemImage)
    imagePreparer.setStatus("Image submitted to gallery.")
  }
  def hasLink: Boolean = imagePreparer.hasLink
  def setLink(link: String) { imagePreparer.setLink(link) }

}
