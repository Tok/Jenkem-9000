package jenkem.ui.tab

import java.awt.image.BufferedImage
import java.util.Date
import scala.collection.JavaConversions.seqAsJavaList
import com.vaadin.data.Property
import com.vaadin.data.Property.ValueChangeEvent
import com.vaadin.data.Property.ValueChangeListener
import com.vaadin.event.EventRouter
import com.vaadin.shared.ui.label.ContentMode
import com.vaadin.ui.AbsoluteLayout
import com.vaadin.ui.Button
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.CheckBox
import com.vaadin.ui.Component
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Label
import com.vaadin.ui.NativeSelect
import com.vaadin.ui.OptionGroup
import com.vaadin.ui.Slider
import com.vaadin.ui.TextField
import com.vaadin.ui.VerticalLayout
import jenkem.event.DoConversionEvent
import jenkem.event.SendToIrcEvent
import jenkem.persistence.PersistenceService
import jenkem.shared.CharacterSet
import jenkem.shared.ConversionMethod
import jenkem.shared.Engine
import jenkem.shared.ImageUtil
import jenkem.shared.Power
import jenkem.shared.ProcessionSettings
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
import jenkem.util.AwtImageUtil
import jenkem.engine.Kick
import jenkem.engine.LineWidth
import jenkem.util.HtmlUtil

class MainTab(val eventRouter: EventRouter) extends VerticalLayout {
  val engine = new Engine
  val nbsp = "&nbsp;"

  var conversionDisabled = true
  var ircOutput: List[String] = Nil
  var imagePrep: ImagePreparationData = _
  var imageData: ImageData = _
  var convData: ConversionData = _

  class ImagePreparationData(val icon: BufferedImage, val originalName: String)
  class ImageData(val imageRgb: java.util.Map[String, Array[java.lang.Integer]],
      val width: Int, val height: Int, val lineWidth: Int, val kick: Kick.Value,
      val method: ConversionMethod)
  class ConversionData(val contrast: Int, val brightness: Int,
      val characters: String)

  setCaption("Main")
  setSizeFull
  setMargin(true)
  setSpacing(true)

  val mainLayout = new HorizontalLayout
  mainLayout.setMargin(true)
  mainLayout.setSpacing(true)
  val settingsLayout = new VerticalLayout
  settingsLayout.setSpacing(true)

  val imagePreparer = new ImagePreparer(eventRouter)
  imagePreparer.enableSubmission(false)
  addComponent(imagePreparer)
  addComponent(mainLayout)

  val inline = new Inline
  mainLayout.addComponent(inline)
  mainLayout.addComponent(settingsLayout)

  val resizeValueChangeListener = new Property.ValueChangeListener {
    override def valueChange(event: ValueChangeEvent) { startConversion(false, true) }
  }

  val noResizeValueChangeListener = new Property.ValueChangeListener {
    override def valueChange(event: ValueChangeEvent) { startConversion(false, false) }
  }

  val methodBox = makeNativeSelect("Conversion Method: ")
  val widthBox = makeNativeSelect("Max Line Width: ")
  val resetButton = new Button("Reset")
  settingsLayout.addComponent(makeLabeled("Reset All Settings: ", resetButton))
  val charsetBox = makeNativeSelect("Character Set: ")
  val charTextField = new TextField
  settingsLayout.addComponent(makeLabeled("Characters: ", charTextField))
  val (processingSlider, processingLabel) = makeSliderAndLabel("Processing: ", 0, 64, 32)
  val processingLayout = new HorizontalLayout
  val doVlineBox = makeCheckBox("|")
  processingLayout.addComponent(doVlineBox)
  val doHlineBox = makeCheckBox("-")
  processingLayout.addComponent(doHlineBox)
  val doDbqplineBox = makeCheckBox("dbqp")
  processingLayout.addComponent(doDbqplineBox)
  val doDiaglineBox = makeCheckBox("/ \\")
  processingLayout.addComponent(doDiaglineBox)
  settingsLayout.addComponent(makeLabeled("Options: ", processingLayout))
  val kickSelect = new OptionGroup("", Kick.getAll)
  settingsLayout.addComponent(makeLabeled("Kick: ", kickSelect))
  val powerBox = makeNativeSelect("Power: ")
  val bgOptions = List("white", "black")
  val bgSelect = new OptionGroup("", bgOptions)
  bgSelect.setDescription("Only relvant for images with transparency.")
  settingsLayout.addComponent(makeLabeled("Background: ", bgSelect))
  val (contrastSlider, contrastLabel) = makeSliderAndLabel("Contrast: ", -100, 100, 0)
  val (brightnessSlider, brightnessLabel) = makeSliderAndLabel("Brightness: ", -100, 100, 0)
  settingsLayout.addComponent(new Label(nbsp, ContentMode.HTML))
  val ircColorSetter = new IrcColorSetter(eventRouter)
  settingsLayout.addComponent(ircColorSetter)
  settingsLayout.addComponent(new Label(nbsp, ContentMode.HTML))
  val outputDisplay = new OutputDisplay
  settingsLayout.addComponent(outputDisplay)
  settingsLayout.addComponent(new Label(nbsp, ContentMode.HTML))
  val ircConnector = new IrcConnector(eventRouter)
  settingsLayout.addComponent(ircConnector)

  ConversionMethod.values.foreach(m => methodBox.addItem(m))
  methodBox.addValueChangeListener(new Property.ValueChangeListener {
    override def valueChange(event: ValueChangeEvent) {
      makeInitsForMethod
      startConversion(false, true)
    }
  })

  LineWidth.getAll.map(lw => widthBox.addItem(lw.value))
  widthBox.addValueChangeListener(resizeValueChangeListener)

  resetButton.addClickListener(new Button.ClickListener {
    override def buttonClick(event: ClickEvent) {
      doReset; startConversion(true, true)
    }
  })

  CharacterSet.values.foreach(cb => charsetBox.addItem(cb))
  charsetBox.addValueChangeListener(new Property.ValueChangeListener {
    override def valueChange(event: ValueChangeEvent) {
      val charset = event.getProperty.getValue.toString
      charTextField.setValue(CharacterSet.getValueByName(charset).getCharacters)
      makeInitsForCharset
      startConversion(false, false)
    }
  })

  charTextField.setImmediate(true)
  charTextField.addValueChangeListener(noResizeValueChangeListener)

  kickSelect.addStyleName("horizontal");
  kickSelect.setNullSelectionAllowed(false)
  kickSelect.setImmediate(true)
  kickSelect.addValueChangeListener(resizeValueChangeListener)

  Power.values.foreach(p => powerBox.addItem(p))
  powerBox.addValueChangeListener(noResizeValueChangeListener)

  bgSelect.addStyleName("horizontal");
  bgSelect.setNullSelectionAllowed(false)
  bgSelect.setImmediate(true)
  bgSelect.addValueChangeListener(new ValueChangeListener() {
    override def valueChange(event: ValueChangeEvent) { startConversion(true, true) }
  })

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
    val layout = makeLabeled(caption, box)
    settingsLayout.addComponent(layout)
    box
  }

  private def makeLabeled(caption: String, component: Component) = {
    val layout = new HorizontalLayout
    val captionLabel = new Label(caption)
    captionLabel.setWidth("150px")
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
    settingsLayout.addComponent(makeLabeled(caption, layout))
    (slider, label)
  }

  private def doReset() {
    conversionDisabled = true
    methodBox.select(ConversionMethod.Vortacular)
    widthBox.select(LineWidth.default.value)
    charsetBox.select(CharacterSet.Ansi)
    charTextField.setValue(CharacterSet.Ansi.getCharacters)
    processingSlider.setValue(32)
    doVlineBox.setValue(false)
    doHlineBox.setValue(false)
    doDbqplineBox.setValue(false)
    doDiaglineBox.setValue(false)
    kickSelect.select(Kick.default)
    powerBox.select(Power.Quadratic)
    bgSelect.select(bgOptions(0))
    contrastSlider.setValue(0)
    brightnessSlider.setValue(0)
    ircColorSetter.reset
    conversionDisabled = false
  }

  private def doPrepareImage(url: String) {
    val crops = imagePreparer.getCrops
    val icon = AwtImageUtil.makeIcon(url, bgSelect.getValue.toString, crops)
    imagePreparer.addIcon(icon)
    imagePreparer.setName(url.split("/").last)
    imagePreparer.enableSubmission(true)
    imagePrep = new ImagePreparationData(icon, imagePreparer.getName)
  }

  private def doPrepareImageData(url: String) {
    val kick = Kick.valueOf(kickSelect.getValue.toString)
    val crops = imagePreparer.getCrops
    val originalImage = AwtImageUtil.bufferImage(url, bgSelect.getValue.toString, crops)
    val originalWidth = originalImage.getWidth
    val originalHeight = originalImage.getHeight
    val lineWidth = widthBox.getValue.toString.toInt
    val method = ConversionMethod.getValueByName(methodBox.getValue.toString)
    val (width, height) = AwtImageUtil.calculateNewSize(method, lineWidth, originalWidth, originalHeight)
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
          makeConversion
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

      val ps = new ProcessionSettings(
        64 - processingLabel.getValue.toInt,
        doVlineBox.getValue, doHlineBox.getValue,
        doDbqplineBox.getValue, doDiaglineBox.getValue, false) //TODO remove edgy setting

      engine.setParams(imageData.imageRgb, imageData.width, chars,
          convData.contrast, convData.brightness , ps)
      engine.prepareEngine(ircColorSetter.getColorMap,
          Power.valueOf(powerBox.getValue.toString))

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

  private def generateIrcOutput(method: ConversionMethod, lastIndex: Int) = {
    def generate0(index: Int): List[String] = {
      if (index + method.getStep > lastIndex) { Nil }
      else { engine.generateLine(method, index) :: generate0(index + method.getStep) }
    }
    generate0(0)
  }

  private def updateInline(method: ConversionMethod, ircOutput: List[String], name: String) {
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
    val defaultScheme = ImageUtil.getDefaultColorScheme(imageRgb, width, height)
    ircColorSetter.setSelectedScheme(defaultScheme)
    val con = jenkem.shared.ImageUtil.getDefaultContrast(imageRgb, width, height) - 100
    contrastSlider.setValue(con)
    val bri = jenkem.shared.ImageUtil.getDefaultBrightness(imageRgb, width, height) - 100
    brightnessSlider.setValue(bri)
    conversionDisabled = false
  }

  private def makeInitsForMethod() {
    conversionDisabled = true
    val method = ConversionMethod.getValueByName(methodBox.getValue.toString)
    kickSelect.setEnabled(method.hasKick)
    ircColorSetter.makeEnabled(!method.equals(ConversionMethod.Plain))
    powerBox.setEnabled(!method.equals(ConversionMethod.Plain))
    if (method.equals(ConversionMethod.Plain)) {
      charsetBox.setValue(CharacterSet.Hard)
    } else {
      charsetBox.setValue(CharacterSet.Ansi)
    }
    doVlineBox.setValue(!method.equals(ConversionMethod.Vortacular))
    doHlineBox.setValue(!method.equals(ConversionMethod.Vortacular))
    doDbqplineBox.setValue(!method.equals(ConversionMethod.Vortacular))
    doDiaglineBox.setValue(!method.equals(ConversionMethod.Vortacular))
    conversionDisabled = false
  }

  private def makeInitsForCharset() {
    conversionDisabled = true
    val chars = charTextField.getValue
    doVlineBox.setValue(!CharacterSet.hasAnsi(chars))
    doHlineBox.setValue(!CharacterSet.hasAnsi(chars))
    doDbqplineBox.setValue(!CharacterSet.hasAnsi(chars))
    doDiaglineBox.setValue(!CharacterSet.hasAnsi(chars))
    conversionDisabled = false
  }

  def saveImage() {
    val name = imagePreparer.getName
    val htmlAndCss = HtmlUtil.generateHtml(ircOutput, name, imageData.method)
    val format = new java.text.SimpleDateFormat("yyyy.MM.dd HH:mm:ss")
    val base64Icon = AwtImageUtil.encodeToBase64(imagePrep.icon)
    val ints = Array(convData.contrast, convData.brightness, ircOutput.size, imageData.lineWidth)
    val jenkemImageInfo = new ImageInfo(
        name, base64Icon, imageData.method.getName, convData.characters, ints, format.format(new Date)
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
