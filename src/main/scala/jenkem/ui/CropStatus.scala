package jenkem.ui

import com.vaadin.event.EventRouter
import com.vaadin.ui.Alignment
import com.vaadin.ui.Button
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Label

import jenkem.event.CropsChangeEvent
import jenkem.event.DoConversionEvent

class CropStatus(val eventRouter: EventRouter) extends HorizontalLayout {
  setSpacing(true)

  eventRouter.addListener(classOf[CropsChangeEvent], new {
    def changeCrops(e: CropsChangeEvent) {
      setCropStatus(e.xs, e.xe, e.ys, e.ye)
      eventRouter.fireEvent(new DoConversionEvent(true, true))
    }}, "changeCrops")

  val resetButton = new Button("Reset Crops")
  resetButton.addClickListener(new Button.ClickListener {
    override def buttonClick(event: ClickEvent) {
      eventRouter.fireEvent(new jenkem.event.ResetCropsEvent)
      reset
      eventRouter.fireEvent(new DoConversionEvent(true, true))
    }
  })
  addComponent(resetButton)
  setComponentAlignment(resetButton, Alignment.MIDDLE_LEFT)

  val statusLabel = new Label
  addComponent(statusLabel)
  setComponentAlignment(statusLabel, Alignment.MIDDLE_LEFT)

  var xStart = 0
  var xEnd = 100
  var yStart = 0
  var yEnd = 100
  reset

  private def setCropStatus(xs: Int, xe: Int, ys: Int, ye: Int) {
    xStart = xs
    xEnd = xe
    yStart = ys
    yEnd = ye
    val builder = new StringBuilder
    builder.append("X: from ")
    builder.append(xs)
    builder.append("% to ")
    builder.append(xe)
    builder.append("%   Y: from ")
    builder.append(ys)
    builder.append("% to ")
    builder.append(ye)
    builder.append("%")
    statusLabel.setValue(builder.toString)
  }
  def getCrops: (Int, Int, Int, Int) = (xStart, xEnd, yStart, yEnd)
  def reset { setCropStatus(0, 100, 0, 100) }
}
