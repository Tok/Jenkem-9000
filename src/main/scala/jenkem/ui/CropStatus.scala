package jenkem.ui

import java.awt.image.BufferedImage
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.GridLayout
import com.vaadin.ui.Button
import com.vaadin.ui.Label
import com.vaadin.ui.Image
import com.vaadin.ui.TextField
import jenkem.AwtImageUtil
import com.vaadin.event.FieldEvents.FocusListener
import com.vaadin.ui.Alignment
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.data.Property
import com.vaadin.data.Property.ValueChangeEvent
import com.vaadin.event.FieldEvents.FocusEvent
import com.google.gwt.event.shared.HandlerManager
import jenkem.client.event.SaveImageEvent
import jenkem.client.event.CropChangeEvent
import jenkem.client.event.CropChangeEventHandler
import jenkem.client.event.ResetCropsEvent
import jenkem.client.event.DoConversionEvent

class CropStatus(val eventBus: HandlerManager) extends HorizontalLayout {
  setSpacing(true)

  eventBus.addHandler(CropChangeEvent.TYPE, new CropChangeEventHandler {
    override def onChangeCrop(e: CropChangeEvent) {
      setCropStatus(e.getXStart, e.getXEnd, e.getYStart, e.getYEnd)
      eventBus.fireEvent(new DoConversionEvent(true, true))
    }
  })

  val resetButton = new Button("Reset Crops")
  resetButton.addClickListener(new Button.ClickListener {
    override def buttonClick(event: ClickEvent) {
      eventBus.fireEvent(new ResetCropsEvent)
      reset
      eventBus.fireEvent(new DoConversionEvent(true, true))
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

  def getCrops: (Int, Int, Int, Int) = (xStart, xEnd, yStart, yEnd)
  def reset = setCropStatus(0, 100, 0, 100)
  def setCropStatus(xs: Int, xe: Int, ys: Int, ye: Int) {
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
}
