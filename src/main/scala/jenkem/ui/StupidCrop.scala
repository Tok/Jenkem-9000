package jenkem.ui

import java.net.URL

import com.vaadin.data.Property
import com.vaadin.data.Property.ValueChangeEvent
import com.vaadin.event.EventRouter
import com.vaadin.server.ExternalResource
import com.vaadin.shared.ui.slider.SliderOrientation
import com.vaadin.ui.GridLayout
import com.vaadin.ui.Image
import com.vaadin.ui.Label
import com.vaadin.ui.Slider

import jenkem.event.CropsChangeEvent
import jenkem.event.ResetCropsEvent

class StupidCrop(val eventRouter: EventRouter) extends GridLayout {
  val width = "100px"
  val difference = 20 //minimal crop in % in any direction
  val min = 0
  val max = 100

  var xStartSlider = makeSlider(SliderOrientation.HORIZONTAL)
  var xEndSlider = makeSlider(SliderOrientation.HORIZONTAL)
  var yStartSlider = makeSlider(SliderOrientation.VERTICAL)
  var yEndSlider = makeSlider(SliderOrientation.VERTICAL)

  val xStartListener = new Property.ValueChangeListener {
    override def valueChange(event: ValueChangeEvent) {
      val xEnd: Int = xEndSlider.getValue.intValue
      if (parseIntFromEvent(event) + difference > xEnd) {
        xStartSlider.setValue(xEnd - difference)
      } else { fireChange }
    }
  }
  val xEndListener = new Property.ValueChangeListener {
    override def valueChange(event: ValueChangeEvent) {
      val xStart: Int = xStartSlider.getValue.intValue
      if (parseIntFromEvent(event) - difference < xStart) {
        xEndSlider.setValue(xStart + difference)
      } else { fireChange }
    }
  }
  val yStartListener = new Property.ValueChangeListener {
    override def valueChange(event: ValueChangeEvent) {
      val yEnd: Int = yEndSlider.getValue.intValue
      if (parseIntFromEvent(event) + difference > yEnd) {
        yStartSlider.setValue(yEnd - difference)
      } else { fireChange }
    }
  }
  val yEndListener = new Property.ValueChangeListener {
    override def valueChange(event: ValueChangeEvent) {
      val yStart: Int = yStartSlider.getValue.intValue
      if (parseIntFromEvent(event) - difference < yStart) {
        yEndSlider.setValue(yStart + difference)
      } else { fireChange }
    }
  }

  val imageGrid = new GridLayout
  imageGrid.setColumns(1)
  imageGrid.setRows(1)

  setSpacing(true)
  setColumns(3)
  setRows(3)

  eventRouter.addListener(classOf[ResetCropsEvent], new {
    def resetCrop: Unit = reset
  }, "resetCrop")

  private def makeSlider(orientation: SliderOrientation) = {
    val slider = new Slider
    slider.setOrientation(orientation)
    slider.setSizeFull
    slider.setMin(min)
    slider.setMax(max)
    slider
  }

  private def makeTinyLabel(text: String) = {
    val label = new Label(text)
    label.setStyleName("tinyLabel")
    label
  }

  private def reset {
    xStartSlider = makeSlider(SliderOrientation.HORIZONTAL)
    xEndSlider = makeSlider(SliderOrientation.HORIZONTAL)
    yStartSlider = makeSlider(SliderOrientation.VERTICAL)
    yEndSlider = makeSlider(SliderOrientation.VERTICAL)

    removeAllComponents
    //   0 1 2
    //
    //0  \ - 1
    //1  | # |
    //2  0 - \
    addComponent(xStartSlider, 1, 0)
    addComponent(yEndSlider, 0, 1)
    addComponent(yStartSlider, 2, 1)
    addComponent(xEndSlider, 1, 2)
    addComponent(imageGrid, 1, 1)

    xStartSlider.setValue(min)
    xEndSlider.setValue(max)
    yStartSlider.setValue(min)
    yEndSlider.setValue(max)

    xStartSlider.addValueChangeListener(xStartListener)
    xEndSlider.addValueChangeListener(xEndListener)
    yStartSlider.addValueChangeListener(yStartListener)
    yEndSlider.addValueChangeListener(yEndListener)
  }

  private def xs = xStartSlider.getValue.intValue
  private def xe = xEndSlider.getValue.intValue
  private def ys = yStartSlider.getValue.intValue
  private def ye = yEndSlider.getValue.intValue
  private def fireChange = eventRouter.fireEvent(new CropsChangeEvent(xs, xe, ys, ye))
  private def parseIntFromEvent(e: ValueChangeEvent) = {
    try {
      e.getProperty.getValue.toString.toDouble.intValue
    } catch {
      case _:Throwable => 0
    }
  }

  def replaceImage(url: URL): Unit = {
    val newImage = new Image
    newImage.setSource(new ExternalResource(url))
    newImage.setWidth(width)
    imageGrid.removeAllComponents
    imageGrid.addComponent(newImage, 0, 0)
    reset
  }

}
