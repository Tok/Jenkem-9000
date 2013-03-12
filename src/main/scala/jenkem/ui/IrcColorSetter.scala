package jenkem.ui

import scala.collection.JavaConversions.mapAsJavaMap
import scala.collection.immutable.HashMap

import com.vaadin.data.Property
import com.vaadin.data.Property.ValueChangeEvent
import com.vaadin.event.EventRouter
import com.vaadin.shared.ui.slider.SliderOrientation
import com.vaadin.ui.Alignment
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Label
import com.vaadin.ui.NativeSelect
import com.vaadin.ui.Slider
import com.vaadin.ui.VerticalLayout

import jenkem.event.DoConversionEvent
import jenkem.shared.ColorScheme
import jenkem.shared.color.IrcColor

class IrcColorSetter(val eventRouter: EventRouter) extends VerticalLayout {
  setSpacing(true)
  setWidth("400px")
  val labels = makeLabelMap
  val sliders = makeSliderMap
  val presetBox = new NativeSelect
  val captionLabel = new Label("Preset: ")
  captionLabel.setWidth("150px")
  ColorScheme.values.foreach(cs => presetBox.addItem(cs))
  presetBox.setWidth("250px")
  presetBox.setNullSelectionAllowed(false)
  presetBox.setImmediate(true)
  presetBox.addValueChangeListener(new Property.ValueChangeListener {
    override def valueChange(event: ValueChangeEvent) {
      val s = ColorScheme.valueOf(event.getProperty.getValue.toString)
      updatePreset(s)
      eventRouter.fireEvent(new DoConversionEvent(false, false))
    }
  })
  val layout = new HorizontalLayout
  layout.addComponent(captionLabel)
  layout.addComponent(presetBox)
  addComponent(layout)

  val sliderLayout = new HorizontalLayout
  IrcColor.values.foreach(ic => sliderLayout.addComponent(makeSliderLayout(ic)))
  addComponent(sliderLayout)

  setSelectedScheme(ColorScheme.Default)
  updatePreset(ColorScheme.Default)

  def makeLabelMap(): HashMap[IrcColor, Label] = {
    def makeLabelMap0(hm: HashMap[IrcColor, Label], ic: List[IrcColor]): HashMap[IrcColor, Label] = {
      if (ic.isEmpty) hm
      else {
        val label = new Label
        label.setStyleName("colorLabel " + ic.head.name)
        makeLabelMap0(hm + ((ic.head, label)), ic.tail)
      }
    }
    makeLabelMap0(new HashMap[IrcColor, Label], IrcColor.values.toList)
  }
  def makeSliderMap(): HashMap[IrcColor, Slider] = {
    def makeSliderMap0(hm: HashMap[IrcColor, Slider], ic: List[IrcColor]): HashMap[IrcColor, Slider] = {
      if (ic.isEmpty) hm
      else {
        val slider = new Slider
        slider.setHeight("100px")
        slider.setResolution(0)
        slider.setOrientation(SliderOrientation.VERTICAL)
        slider.setMin(0)
        slider.setMax(100)
        slider.setImmediate(true)
        slider.setStyleName("v-slider " + ic.head.name)
        makeSliderMap0(hm + ((ic.head, slider)), ic.tail)
      }
    }
    makeSliderMap0(new HashMap[IrcColor, Slider], IrcColor.values.toList)
  }
  def makeSliderLayout(ic: IrcColor) = {
    val label = getLabel(ic)
    val slider = getSlider(ic)
    slider.addValueChangeListener(new Property.ValueChangeListener {
      override def valueChange(event: ValueChangeEvent) {
        label.setValue("%1.0f".format(event.getProperty.getValue))
        eventRouter.fireEvent(new DoConversionEvent(false, false))
      }
    })
    val layout = new VerticalLayout
    layout.setSpacing(true)
    layout.addComponent(slider)
    layout.setComponentAlignment(slider, Alignment.MIDDLE_CENTER)
    layout.addComponent(label)
    layout
  }
  def getLabel(ic: IrcColor): Label = labels.get(ic) match {
    case Some(label) => label
    case None => println("Label for " + ic + " not found!"); new Label
  }
  def getSlider(ic: IrcColor): Slider = sliders.get(ic) match {
    case Some(slider) => slider
    case None => println("Slider for " + ic + " not found!"); new Slider
  }
  def updatePreset(cs: ColorScheme) = {
    IrcColor.values.foreach(ic => getSlider(ic).setValue(ic.getOrder(cs).intValue))
  }
  def getValue(ic: IrcColor) = Integer.valueOf(getLabel(ic).getValue)
  def setSelectedScheme(scheme: ColorScheme) = presetBox.select(scheme)
  def reset = {
    if (presetBox.getValue.equals(ColorScheme.Default)) {
        updatePreset(ColorScheme.getValueByName(presetBox.getValue.toString))
    } else presetBox.select(ColorScheme.Default)
  }
  def getColorMap(): java.util.Map[IrcColor, java.lang.Integer] = {
    def getColorMap0(hm: HashMap[IrcColor, java.lang.Integer], ic: List[IrcColor]):
        HashMap[IrcColor, java.lang.Integer] = {
      if (ic.isEmpty) hm
      else getColorMap0(hm + ((ic.head, new java.lang.Integer(getValue(ic.head)))), ic.tail)
    }
    getColorMap0(new HashMap[IrcColor, java.lang.Integer], IrcColor.values.toList)
  }
  def makeEnabled(enabled: Boolean) {
    presetBox.setEnabled(enabled)
    sliders.values.map(s => s.setEnabled(enabled))
  }
}
