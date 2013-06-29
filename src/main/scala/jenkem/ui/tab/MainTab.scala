package jenkem.ui.tab

import java.awt.image.BufferedImage
import java.util.Date
import scala.collection.JavaConversions.seqAsJavaList
import com.vaadin.data.Property
import com.vaadin.data.Property.ValueChangeEvent
import com.vaadin.event.EventRouter
import com.vaadin.server.Page
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
import jenkem.engine.Method
import jenkem.engine.Engine
import jenkem.engine.Kick
import jenkem.engine.Pal
import jenkem.engine.color.Scheme
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
import jenkem.ui.Notifications
import jenkem.ui.OutputDisplay
import jenkem.ui.ProcSetter
import jenkem.util.AwtImageUtil
import jenkem.util.HtmlUtil
import jenkem.util.InitUtil
import com.vaadin.ui.Notification
import com.vaadin.ui.Image
import com.vaadin.ui.GridLayout
import jenkem.engine.color.Color

class MainTab(val eventRouter: EventRouter) extends VerticalLayout {
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
  class ImageData(val imageRgb: Color.RgbMap,
      val width: Short, val height: Short, val lineWidth: Short,
      val method: Method)
  class ConversionData(val contrast: Short, val brightness: Short, val characters: String)

  val resizeValueChangeListener = new Property.ValueChangeListener {
    override def valueChange(event: ValueChangeEvent) {
      if(!conversionDisabled) { startConversion(false, true) }}}

  val noResizeValueChangeListener = new Property.ValueChangeListener {
    override def valueChange(event: ValueChangeEvent) {
      if(!conversionDisabled) { startConversion(false, false) }}}

  private def spacer = new Label("&nbsp;", ContentMode.HTML)

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
  val kickSelect = new OptionGroup(None.orNull, Kick.values)
  kickSelect.addStyleName("horizontal")
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
  val resetButton = new Button("Reset Conversion Settings")
  settingsLayout.addComponent(makeLabeled("Action: ", capW, resetButton, false))
  val (brightnessSlider, brightnessLabel) = makeSliderAndLabel("Character Brightness: ", -100, 100, 0)
  val (contrastSlider, contrastLabel) = makeSliderAndLabel("Character Contrast: ", -100, 100, 0)

  val charsetLayout = new HorizontalLayout
  val charsetBox = new NativeSelect
  charsetBox.setNullSelectionAllowed(false)
  charsetBox.setImmediate(true)
  charsetBox.setWidth("120px")
  val charTextField = new TextField
  charTextField.setWidth("125px")
  charsetLayout.addComponent(charsetBox)
  charsetLayout.addComponent(charTextField)
  settingsLayout.addComponent(makeLabeled("Characters: ", capW, charsetLayout, true))
  val procSetter = new ProcSetter(eventRouter)
  settingsLayout.addComponent(makeLabeled("Processing: ", capW, procSetter, true))
  settingsLayout.addComponent(spacer)
  val powerBox = makeNativeSelect("Power: ")
  val ircColorSetter = new IrcColorSetter(eventRouter)
  settingsLayout.addComponent(ircColorSetter)
  settingsLayout.addComponent(spacer)
  val outputDisplay = new OutputDisplay
  settingsLayout.addComponent(outputDisplay)
  settingsLayout.addComponent(spacer)
  val ircConnector = new IrcConnector(eventRouter)
  settingsLayout.addComponent(ircConnector)

  resetButton.addClickListener(new Button.ClickListener {
    override def buttonClick(event: ClickEvent): Unit = {
      doReset //may trigger conversion
      startConversion(false, true)
    }
  })

  val charsetBoxListener = new Property.ValueChangeListener {
    override def valueChange(event: ValueChangeEvent): Unit = {
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
  }
  charsetBox.addValueChangeListener(charsetBoxListener)

  charTextField.setImmediate(true)
  charTextField.addValueChangeListener(noResizeValueChangeListener)

  Power.values.foreach(p => powerBox.addItem(p))
  powerBox.addValueChangeListener(noResizeValueChangeListener)

  eventRouter.addListener(classOf[DoConversionEvent], new {
    def convert(e: DoConversionEvent): Unit =
      startConversion(e.prepareImage, e.resize)
    }, "convert")
  eventRouter.addListener(classOf[SendToIrcEvent], new {
    def send: Unit = ircConnector.sendToIrc(ircOutput)}, "send")

  methodBox.select(Method.Vortacular)
  doReset

  private def makeNativeSelect(caption: String): NativeSelect = {
    val box = new NativeSelect
    box.setNullSelectionAllowed(false)
    box.setImmediate(true)
    val layout = makeLabeled(caption, capW, box, true)
    settingsLayout.addComponent(layout)
    box
  }

  private def makeListMethodSelect(caption: String): ListSelect = {
    val settings = new ListSelect
    settings.addValueChangeListener(new Property.ValueChangeListener {
      override def valueChange(event: ValueChangeEvent): Unit = {
        conversionDisabled = true
        makeInitsForMethod
        conversionDisabled = false
        startConversion(false, true)
      }
    })
    settings.setRows(Method.values.length)
    Method.values.map(settings.addItem(_))
    settings.setNullSelectionAllowed(false)
    settings.setImmediate(true)
    val layout = makeLabeled(caption, capW, settings, true)
    settingsLayout.addComponent(layout)
    settings
  }

  private def makeLabeled(caption: String, width: Int, component: Component, setWidth: Boolean) = {
    val layout = new HorizontalLayout
    val captionLabel = new Label(caption)
    captionLabel.setWidth(width + "px")
    if (setWidth) { component.setWidth("250px") }
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
      override def valueChange(event: ValueChangeEvent): Unit = {
        label.setValue("%1.0f".format(event.getProperty.getValue))
        startConversion(false, false)
      }
    })
    layout.setWidth("100%")
    layout.addComponent(slider, "left:0px")
    layout.addComponent(label, "left:220px")
    settingsLayout.addComponent(makeLabeled(caption, capW, layout, true))
    (slider, label)
  }

  private def doPrepareImage(url: String): Unit = {
    val crops = imagePreparer.getCrops
    val invert = imagePreparer.isInvert
    val bg = imagePreparer.getBg
    val icon = AwtImageUtil.makeIcon(url, bg, invert, crops)
    imagePreparer.addIcon(icon)
    imagePreparer.setName(url.split("/").last)
    imagePreparer.enableSubmission(true)
    imagePrep = new ImagePreparationData(icon, imagePreparer.getName)
  }

  private def doPrepareImageData(url: String): Unit = {
    val kick = Kick.valueOf(kickSelect.getValue.toString).get
    val crops = imagePreparer.getCrops
    val invert = imagePreparer.isInvert
    val bg = imagePreparer.getBg
    val originalImage = AwtImageUtil.bufferImage(url, bg, invert, crops)
    val originalWidth = originalImage.getWidth
    val originalHeight = originalImage.getHeight
    val lineWidth = widthSlider.getValue.shortValue
    val method = Method.valueOf(methodBox.getValue.toString).get
    val (width, height) = InitUtil.calculateNewSize(method, lineWidth, originalWidth, originalHeight)
    val b = imagePreparer.getBrightness
    val c = imagePreparer.getContrast
    val scaled = AwtImageUtil.getScaled(originalImage, width, height, kick, b, c)
    inline.setIntermediate(scaled, method)
    val imageRgb = AwtImageUtil.getImageRgb(scaled)
    imageData = new ImageData(imageRgb, scaled.getWidth.toShort, scaled.getHeight.toShort, lineWidth, method)
  }

  private def startConversion(prepareImage: Boolean, resize: Boolean): Unit = {
    if (!conversionDisabled) {
      conversionDisabled = true
      imagePreparer.getUrl match {
        case Some(url) =>
          try {
            val totalStart = (new Date).getTime
            if (prepareImage) { doPrepareImage(url) }
            if (resize) { doPrepareImageData(url) }
            if (prepareImage) {
              makeInits
              conversionDisabled = false
              startConversion(false, false)
            } else {
              val (conversionTime, htmlTime) = makeConversion
              val totalEnd = (new Date).getTime
              val totalTime = totalEnd - totalStart
              val status = "Image converted in " + conversionTime + "ms. " +
                    "HTML generated in " + htmlTime + "ms. " +
                    "Total time " + totalTime + "ms."
              imagePreparer.setStatus(status)
              System.gc
            }
          } catch {
            case iioe: javax.imageio.IIOException => imagePreparer.setError("Cannot read image from URL.")
            case e: Exception => imagePreparer.setError(e.getMessage)
          }
        case None => { }
      }
    }
  }

  private def makeConversion(): (Long, Long) = {
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
    val conversionStart = (new Date).getTime
    ircOutput = generateIrcOutput(params, imageData.height)
    val conversionEnd = (new Date).getTime
    val conversionDiff = conversionEnd - conversionStart
    outputDisplay.addIrcOutput(ircOutput.map(_ + "\n"))
    val htmlDiff = updateInline(imageData.method, ircOutput, imagePreparer.getName)
    ircConnector.refresh
    conversionDisabled = false
    (conversionDiff, htmlDiff)
  }

  private def generateIrcOutput(params: Engine.Params, lastIndex: Int) = {
    def generate0(index: Int): List[String] = {
      if (index + 2 > lastIndex) { Nil }
      else { Engine.generateLine(params, index) :: generate0(index + 2) }
    }
    generate0(0)
  }

  private def updateInline(method: Method, ircOutput: List[String], name: String): Long = {
    val htmlStart = (new Date).getTime
    val htmlAndCss = HtmlUtil.generateHtml(ircOutput, name, method)
    val inlineCss = HtmlUtil.prepareCssForInline(htmlAndCss._2)
    val inlineHtml = HtmlUtil.prepareHtmlForInline(htmlAndCss._1, inlineCss)
    val htmlEnd = (new Date).getTime
    inline.setValue(inlineHtml)
    inline.reset
    htmlEnd - htmlStart
  }

  private def makeInits(): Unit = {
    conversionDisabled = true
    val chars = charTextField.getValue.replaceAll("[,0-9]", "")
    makeImageInits
    conversionDisabled = false
  }

  private def makeImageInits(): Unit = {
    conversionDisabled = true
    contrastSlider.setValue(0)
    brightnessSlider.setValue(0)
    val (methodOpt, scheme, charsetOpt) = InitUtil.getDefaults(imageData.imageRgb)
    methodOpt match {
      case Some(meth) =>
        if (imagePreparer.getBrightness == 0 && imagePreparer.getContrast == 0) {
          methodBox.setValue(meth)
        }
      case None => { }
    }
    ircColorSetter.setSelectedScheme(scheme)
    charsetOpt match {
      case Some(chrset) => charsetBox.select(chrset)
      case None => { }
    }
    conversionDisabled = false
  }

  private def doReset(): Unit = {
    conversionDisabled = true
    widthSlider.setValue(defaultWidth)
    contrastSlider.setValue(0)
    brightnessSlider.setValue(0)
    makeInitsForMethod
    conversionDisabled = false
  }

  private def makeInitsForMethod(): Unit = {
    conversionDisabled = true
    val method = Method.valueOf(methodBox.getValue.toString).get
    ircColorSetter.makeEnabled(method.hasColor)
    ircColorSetter.setSelectedScheme(Scheme.Default)
    powerBox.setEnabled(method.equals(Method.Vortacular))
    powerBox.select(Power.Linear)
    kickSelect.select(Kick.OFF)
    kickSelect.setEnabled(method.hasKick)
    procSetter.reset(Pal.hasAnsi(charTextField.getValue))
    procSetter.setEnabled(!method.equals(Method.Pwntari))
    charsetBox.removeValueChangeListener(charsetBoxListener)
    charsetBox.removeAllItems
    Pal.getCharsetForMethod(method).foreach(cb => charsetBox.addItem(cb))
    charsetBox.addValueChangeListener(charsetBoxListener)
    charsetBox.setValue(Pal.getForMethod(method))
    charsetBox.setEnabled(!method.equals(Method.Pwntari))
    charTextField.setEnabled(!method.equals(Method.Pwntari))
    if (method.equals(Method.Pwntari)) {
      charTextField.setValue("▄")
      contrastSlider.setValue(0)
      brightnessSlider.setValue(0)
    }
    contrastSlider.setEnabled(!method.equals(Method.Pwntari))
    brightnessSlider.setEnabled(!method.equals(Method.Pwntari))
    contrastLabel.setEnabled(!method.equals(Method.Pwntari))
    brightnessLabel.setEnabled(!method.equals(Method.Pwntari))
    conversionDisabled = false
  }

  private def makeInitsForCharset(chars: String) = procSetter.reset(Pal.hasAnsi(chars))

  def saveImage(): Unit = {
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
    if (PersistenceService.saveJenkemImage(jenkemImage)) {
      Notifications.showImageSaved(Page.getCurrent)
    } else {
      Notifications.showImageNotSaved(Page.getCurrent)
    }
  }

  def hasLink: Boolean = imagePreparer.hasLink
  def setLink(link: String): Unit = imagePreparer.setLink(link)
}
