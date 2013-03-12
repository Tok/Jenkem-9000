package jenkem.tab

import java.awt.image.BufferedImage
import java.util.Date

import scala.collection.JavaConversions.seqAsJavaList

import com.google.gwt.event.shared.HandlerManager
import com.vaadin.data.Property
import com.vaadin.data.Property.ValueChangeEvent
import com.vaadin.data.Property.ValueChangeListener
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

import jenkem.AwtImageUtil
import jenkem.client.event.DoConversionEvent
import jenkem.client.event.DoConversionEventHandler
import jenkem.client.event.SaveImageEvent
import jenkem.client.event.SaveImageEventHandler
import jenkem.client.event.SendToIrcEvent
import jenkem.client.event.SendToIrcEventHandler
import jenkem.server.PersistenceService
import jenkem.shared.CharacterSet
import jenkem.shared.ConversionMethod
import jenkem.shared.Engine
import jenkem.shared.HtmlUtil
import jenkem.shared.ImageUtil
import jenkem.shared.Kick
import jenkem.shared.LineWidth
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

class MainTab extends VerticalLayout {
  val engine = new Engine
  val htmlUtil = new HtmlUtil

  val width = 400
  var conversionDisabled = true
  var ircOutput: List[String] = null
  var imagePrep: ImagePreparationData = null
  var imageData: ImageData = null
  var convData: ConversionData = null

  class ImagePreparationData(val icon: BufferedImage, val originalName: String) { }
  class ImageData(val imageRgb: java.util.Map[String, Array[java.lang.Integer]],
      val width: Int, val height: Int, val lineWidth: Int, val kick: Kick,
      val method: ConversionMethod) { }
  class ConversionData(val contrast: Int, val brightness: Int,
      val characters: String) { }

  setCaption("Main")
  setSizeFull
  setMargin(true)
  setSpacing(true)

  val mainLayout = new HorizontalLayout
  mainLayout.setMargin(true)
  mainLayout.setSpacing(true)
  val settingsLayout = new VerticalLayout
  settingsLayout.setSpacing(true)

  val eventBus = new HandlerManager(null)
  val imagePreparer = new ImagePreparer(eventBus)
  imagePreparer.enableSubmission(false)
  addComponent(imagePreparer)
  addComponent(mainLayout)

  val inline = new Inline
  mainLayout.addComponent(inline)
  mainLayout.addComponent(settingsLayout)

  val resizeValueChangeListener = new Property.ValueChangeListener {
    override def valueChange(event: ValueChangeEvent) { doConversion(false, true) }
  }

  val noResizeValueChangeListener = new Property.ValueChangeListener {
    override def valueChange(event: ValueChangeEvent) { doConversion(false, false) }
  }

  val methodBox = makeComboBox("Conversion Method: ")
  val widthBox = makeComboBox("Max Line Width: ")
  val resetButton = new Button("Reset")
  settingsLayout.addComponent(makeLabeled("Reset All Settings: ", resetButton))
  val charsetBox = makeComboBox("Character Set: ")
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
  val kickSelect = new OptionGroup(null, Kick.values.toList)
  settingsLayout.addComponent(makeLabeled("Kick: ", kickSelect))
  val powerBox = makeComboBox("Power: ")
  val bgOptions = List("white", "black")
  val bgSelect = new OptionGroup(null, bgOptions)
  bgSelect.setDescription("Only relvant for images with transparency.")
  settingsLayout.addComponent(makeLabeled("Background: ", bgSelect))
  val (contrastSlider, contrastLabel) = makeSliderAndLabel("Contrast: ", -100, 100, 0)
  val (brightnessSlider, brightnessLabel) = makeSliderAndLabel("Brightness: ", -100, 100, 0)
  settingsLayout.addComponent(new Label("&nbsp;", ContentMode.HTML))
  val ircColorSetter = new IrcColorSetter(eventBus)
  settingsLayout.addComponent(ircColorSetter)
  settingsLayout.addComponent(new Label("&nbsp;", ContentMode.HTML))
  val outputDisplay = new OutputDisplay
  settingsLayout.addComponent(outputDisplay)
  settingsLayout.addComponent(new Label("&nbsp;", ContentMode.HTML))
  val ircConnector = new IrcConnector(eventBus)
  settingsLayout.addComponent(ircConnector)

  ConversionMethod.values.foreach(m => methodBox.addItem(m))
  methodBox.addValueChangeListener(new Property.ValueChangeListener {
    override def valueChange(event: ValueChangeEvent) {
      makeInitsForMethod
      doConversion(false, true)
    }
  })

  LineWidth.values.foreach(lw => widthBox.addItem(lw.getValue))
  widthBox.addValueChangeListener(resizeValueChangeListener)

  resetButton.addClickListener(new Button.ClickListener {
    override def buttonClick(event: ClickEvent) {
      doReset; doConversion(true, true)
    }
  })

  CharacterSet.values.foreach(cb => charsetBox.addItem(cb))
  charsetBox.addValueChangeListener(new Property.ValueChangeListener {
    override def valueChange(event: ValueChangeEvent) {
      val charset = event.getProperty.getValue.toString
      charTextField.setValue(CharacterSet.getValueByName(charset).getCharacters)
      makeInitsForCharset
      doConversion(false, false)
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
    override def valueChange(event: ValueChangeEvent) { doConversion(true, true) }
  })

  eventBus.addHandler(DoConversionEvent.TYPE, new DoConversionEventHandler {
    override def onDoConversion(event: DoConversionEvent) {
      doConversion(event.prepareImage, event.resize)
    }
  })
  eventBus.addHandler(SendToIrcEvent.TYPE, new SendToIrcEventHandler {
    override def onSend(event: SendToIrcEvent) { ircConnector.sendToIrc(ircOutput) }
  })
  eventBus.addHandler(SaveImageEvent.TYPE, new SaveImageEventHandler {
    override def onSave(event: SaveImageEvent) { saveImage }
  })

  doReset

  def setLink(link: String) = imagePreparer.setLink(link)
  def makeComboBox(caption: String): NativeSelect = {
    val box = new NativeSelect
    box.setNullSelectionAllowed(false)
    box.setImmediate(true)
    val layout = makeLabeled(caption, box)
    settingsLayout.addComponent(layout)
    box
  }

  def makeLabeled(caption: String, component: Component) = {
    val layout = new HorizontalLayout
    val captionLabel = new Label(caption)
    captionLabel.setWidth(150 + "px")
    component.setWidth((width - 150) + "px")
    layout.addComponent(captionLabel)
    layout.addComponent(component)
    layout
  }

  def makeCheckBox(caption: String): CheckBox = {
    val box = new CheckBox(caption)
    box.setImmediate(true)
    box.addValueChangeListener(noResizeValueChangeListener)
    settingsLayout.addComponent(box)
    box
  }

  def makeSliderAndLabel(caption: String, min: Int, max: Int, default: Int): (Slider, Label) = {
    val layout = new AbsoluteLayout
    val label = new Label(default.toString)
    label.setWidth(30 + "px")
    label.setStyleName("sliderLabel")
    val slider = new Slider(null)
    slider.setWidth((width - 180) + "px")
    slider.setMin(min)
    slider.setMax(max)
    slider.setImmediate(true)
    slider.addValueChangeListener(new Property.ValueChangeListener {
      override def valueChange(event: ValueChangeEvent) {
        label.setValue("%1.0f".format(event.getProperty.getValue))
        doConversion(false, false)
      }
    })
    layout.setWidth("100%")
    layout.addComponent(slider, "left:0px")
    layout.addComponent(label, "left:220px")
    settingsLayout.addComponent(makeLabeled(caption, layout))
    (slider, label)
  }

  def doReset() {
    conversionDisabled = true
    methodBox.select(ConversionMethod.Vortacular)
    widthBox.select(LineWidth.Default.getValue)
    charsetBox.select(CharacterSet.Ansi)
    charTextField.setValue(CharacterSet.Ansi.getCharacters)
    processingSlider.setValue(32)
    doVlineBox.setValue(false)
    doHlineBox.setValue(false)
    doDbqplineBox.setValue(false)
    doDiaglineBox.setValue(false)
    kickSelect.select(Kick.Off)
    powerBox.select(Power.Quadratic)
    bgSelect.select(bgOptions(0))
    contrastSlider.setValue(0)
    brightnessSlider.setValue(0)
    ircColorSetter.reset
    conversionDisabled = false
  }

  def doPrepareImage(url: String) {
    val crops = imagePreparer.getCrops
    val icon = AwtImageUtil.makeIcon(url, bgSelect.getValue.toString, crops)
    imagePreparer.addIcon(icon)
    imagePreparer.setName(url.split("/").last)
    imagePreparer.enableSubmission(true)
    imagePrep = new ImagePreparationData(icon, imagePreparer.getName)
  }

  def doPrepareImageData(url: String) {
    val kick = Kick.valueOf(kickSelect.getValue.toString)
    val crops = imagePreparer.getCrops
    val originalImage = AwtImageUtil.bufferImage(url, bgSelect.getValue.toString, crops)
    val originalWidth = originalImage.getWidth
    val originalHeight = originalImage.getHeight
    val lineWidth = widthBox.getValue.toString.toInt
    val method = ConversionMethod.getValueByName(methodBox.getValue.toString)
    val (width, height) = AwtImageUtil.calculateNewSize(method, lineWidth, originalWidth, originalHeight)
    val imageRgb = AwtImageUtil.getImageRgb(originalImage, width, height, kick)
    val dataWidth = width - (2 * kick.getXOffset)
    val dataHeight = height - (2 * kick.getYOffset)
    imageData = new ImageData(imageRgb, dataWidth, dataHeight, lineWidth, kick, method)
  }

  def doConversion(prepareImage: Boolean, resize: Boolean) {
    if (conversionDisabled) { return }
    val url = imagePreparer.getUrl match {
      case Some(s) => s
      case None => return
    }
    if (prepareImage || imagePrep == null) { doPrepareImage(url) }
    if (resize || imageData == null) { doPrepareImageData(url) }
    if (prepareImage) { makeInits; doConversion(false, false) }
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
      outputDisplay.addIrcOutput(ircOutput)
      updateInline(imageData.method, ircOutput, imagePreparer.getName)
      imagePreparer.setStatus("Ready...")
      ircConnector.refresh
    } catch {
      case iioe: javax.imageio.IIOException => imagePreparer.setError("Cannot read image from URL.")
      case iae: IllegalArgumentException => imagePreparer.setError(iae.getMessage)
      case e: Exception => imagePreparer.setError(e.getMessage)
    }
  }

  def generateIrcOutput(method: ConversionMethod, lastIndex: Int): List[String] = {
    def generate0(index: Int): List[String] = {
      if (index + method.getStep > lastIndex) Nil
      else engine.generateLine(method, index) + "\n" :: generate0(index + method.getStep)
    }
    generate0(0)
  }

  def updateInline(method: ConversionMethod, ircOutput: List[String], name: String) {
    val htmlAndCss = htmlUtil.generateHtml(ircOutput, name, method)
    val inlineCss = htmlUtil.prepareCssForInline(htmlAndCss(1))
    val inlineHtml = htmlUtil.prepareHtmlForInline(htmlAndCss(0), inlineCss)
    inline.setValue(inlineHtml)
  }

  def makeInits() {
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

  def makeInitsForMethod() {
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

  def makeInitsForCharset() {
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
    val htmlAndCss = htmlUtil.generateHtml(ircOutput, name, imageData.method)
    val now = new Date
    val format = new java.text.SimpleDateFormat("yyyy.MM.dd HH:mm:ss")
    val base64Icon = AwtImageUtil.encodeToBase64(imagePrep.icon)
    val ints = Array(convData.contrast, convData.brightness, ircOutput.size, imageData.lineWidth)
    val jenkemImageInfo = new ImageInfo(
        name, base64Icon, imageData.method.getName, convData.characters, ints, format.format(now)
    )
    val jenkemImageHtml = new ImageHtml(name, htmlAndCss(0))
    val jenkemImageCss = new ImageCss(name, htmlAndCss(1))
    val irc = new StringBuilder
    ircOutput.map(line => irc.append(line))
    val jenkemImageIrc = new ImageIrc(name, irc.toString);
    val jenkemImage = new JenkemImage(jenkemImageInfo, jenkemImageHtml, jenkemImageCss, jenkemImageIrc)
    PersistenceService.saveJenkemImage(jenkemImage)
    imagePreparer.setStatus("Image submitted to gallery.")
  }

}
