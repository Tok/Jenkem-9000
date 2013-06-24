package jenkem.ui

import scala.annotation.migration
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

import jenkem.engine.color.Scheme
import jenkem.event.DoConversionEvent

class IrcColorSetter(val eventRouter: EventRouter) extends VerticalLayout {
  var isTriggeringDisabled = false
  setSpacing(true)
  setWidth("400px")
  val labels = makeLabelMap
  val sliders = makeSliderMap
  val presetBox = new NativeSelect
  val captionLabel = new Label("Preset: ")
  captionLabel.setWidth("150px")
  Scheme.values.foreach(cs => presetBox.addItem(cs))
  presetBox.setWidth("250px")
  presetBox.setNullSelectionAllowed(false)
  presetBox.setImmediate(true)
  presetBox.addValueChangeListener(new Property.ValueChangeListener {
    override def valueChange(event: ValueChangeEvent): Unit = {
      Scheme.valueOf(event.getProperty.getValue.toString) match {
        case Some(s) => updatePreset(s)
        case None => { }
      }
      if (!isTriggeringDisabled) {
        eventRouter.fireEvent(new DoConversionEvent(false, false))
      }
    }
  })
  val layout = new HorizontalLayout
  layout.addComponent(captionLabel)
  layout.addComponent(presetBox)
  addComponent(layout)

  val sliderLayout = new HorizontalLayout
  Scheme.ircColors.foreach(ic => sliderLayout.addComponent(makeSliderLayout(ic)))
  addComponent(sliderLayout)

  setSelectedScheme(Scheme.Default)
  updatePreset(Scheme.Default)

  private def makeLabelMap(): HashMap[Scheme.IrcColor, Label] = {
    def makeLabelMap0(hm: HashMap[Scheme.IrcColor, Label], ic: List[Scheme.IrcColor]): HashMap[Scheme.IrcColor, Label] = {
      if (ic.isEmpty) { hm }
      else {
        val label = new Label
        label.setStyleName("colorLabel " + ic.head.name)
        makeLabelMap0(hm + ((ic.head, label)), ic.tail)
      }
    }
    makeLabelMap0(new HashMap[Scheme.IrcColor, Label], Scheme.ircColors)
  }
  private def makeSliderMap(): HashMap[Scheme.IrcColor, Slider] = {
    def makeSliderMap0(hm: HashMap[Scheme.IrcColor, Slider], ic: List[Scheme.IrcColor]): HashMap[Scheme.IrcColor, Slider] = {
      if (ic.isEmpty) { hm }
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
    makeSliderMap0(new HashMap[Scheme.IrcColor, Slider], Scheme.ircColors)
  }
  private def makeSliderLayout(ic: Scheme.IrcColor) = {
    val label = getLabel(ic)
    val slider = getSlider(ic)
    slider.addValueChangeListener(new Property.ValueChangeListener {
      override def valueChange(event: ValueChangeEvent) {
        label.setValue("%1.0f".format(event.getProperty.getValue))
        if (!isTriggeringDisabled) {
          eventRouter.fireEvent(new DoConversionEvent(false, false))
        }
      }
    })
    val layout = new VerticalLayout
    layout.setSpacing(true)
    layout.addComponent(slider)
    layout.setComponentAlignment(slider, Alignment.MIDDLE_CENTER)
    layout.addComponent(label)
    layout
  }
  private def getLabel(ic: Scheme.IrcColor): Label = labels.get(ic) match {
    case Some(label) => label
    case None => new Label
  }
  private def getSlider(ic: Scheme.IrcColor): Slider = sliders.get(ic) match {
    case Some(slider) => slider
    case None => new Slider
  }
  private def updatePreset(cs: Scheme): Unit = {
    isTriggeringDisabled = true
    Scheme.ircColors.foreach(ic => getSlider(ic).setValue(ic.scheme(cs.order)))
    isTriggeringDisabled = false
  }
  private def getValue(ic: Scheme.IrcColor) = Integer.valueOf(getLabel(ic).getValue).shortValue

  def getColorMap: Map[Scheme.IrcColor, Short] = {
    def getColorMap0(hm: HashMap[Scheme.IrcColor, Short], ic: List[Scheme.IrcColor]):
        HashMap[Scheme.IrcColor, Short] = {
      if (ic.isEmpty) { hm }
      else { getColorMap0(hm + ((ic.head, getValue(ic.head))), ic.tail) }
    }
    getColorMap0(new HashMap[Scheme.IrcColor, Short], Scheme.ircColors)
  }
  def setSelectedScheme(scheme: Scheme): Unit = presetBox.select(scheme)
  def makeEnabled(enabled: Boolean): Unit = {
    presetBox.setEnabled(enabled)
    sliders.values.foreach(s => s.setEnabled(enabled))
  }
  def reset: Unit = {
    isTriggeringDisabled = true
    if (presetBox.getValue.equals(Scheme.Default)) {
        Scheme.valueOf(presetBox.getValue.toString) match {
          case Some(s) => updatePreset(s)
          case None => { }
        }
    } else { presetBox.select(Scheme.Default) }
    isTriggeringDisabled = false
    eventRouter.fireEvent(new DoConversionEvent(false, false))
  }
}
