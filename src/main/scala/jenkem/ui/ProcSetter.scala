/*
 * #%L
 * ProcSetter.scala - Jenkem - Tok - 2012
 * %%
 * Copyright (C) 2012 - 2013 Lukas Steiger <lsteiger4@gmail.com>
 * %%
 * This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar.
 * See http://www.wtfpl.net/ for more details.
 * #L%
 */
package jenkem.ui

import scala.collection.immutable.HashMap

import com.vaadin.data.Property
import com.vaadin.data.Property.ValueChangeEvent
import com.vaadin.event.EventRouter
import com.vaadin.ui.Alignment
import com.vaadin.ui.CheckBox
import com.vaadin.ui.Component
import com.vaadin.ui.GridLayout
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Label
import com.vaadin.ui.Slider

import jenkem.engine.Setting
import jenkem.event.DoConversionEvent

class ProcSetter(val eventRouter: EventRouter) extends GridLayout {
  var triggeringDisabled = true
  setWidth("250px")

  val vcl = new Property.ValueChangeListener {
    override def valueChange(event: ValueChangeEvent): Unit = {
      if (!triggeringDisabled) {
        eventRouter.fireEvent(new DoConversionEvent(false, false))
      }
    }
  }

  type PS = Setting
  val settings = Setting.values
  setRows(3)
  setColumns(3)

  val boxes = makeComponentMap[CheckBox](settings, makeBox)
  val labels = makeComponentMap[Label](settings.take(2), makeLabel)
  val sliders = makeComponentMap[Slider](settings.take(2), makeSlider)
  (0 to 1).foreach(makeSliderRow(_))

  val layout = new HorizontalLayout
  (2 to 6).foreach(i => layout.addComponent(boxes.get(settings(i)).get))
  addComponent(layout, 0, 2, 2, 2)

  val fullBlockDescription = "Replace spaces by fullblocks to get better ANSI output for non-monospace fonts."
  boxes.get(Setting.FULLBLOCK).get.setDescription(fullBlockDescription)

  triggeringDisabled = false

  private def makeComponentMap[T](sets: List[PS], f: PS => Component): HashMap[PS, T] = {
    def makeComponentMap0(hm: HashMap[PS, T], s: List[PS]): HashMap[PS, T] = {
      if (s.isEmpty) { hm } else {
        val comp = f(s.head).asInstanceOf[T]
        makeComponentMap0(hm + ((s.head, comp)), s.tail)
      }
    }
    makeComponentMap0(new HashMap[PS, T], sets)
  }

  private def makeBox(setting: PS): CheckBox = {
    val box = new CheckBox(setting.caption)
    box.setWidth("50px")
    box.setImmediate(true)
    box.addValueChangeListener(vcl)
    box
  }

  private def makeLabel(setting: PS): Label = {
    val label = new Label(Setting.default.toString)
    label.setWidth("30px")
    label.setStyleName("sliderLabel")
    label
  }

  private def makeSlider(setting: PS): Slider = {
    val slider = new Slider(None.orNull)
    slider.setWidth("170px")
    slider.setMin(Setting.min)
    slider.setMax(Setting.max)
    slider.setValue(Setting.default)
    slider.setImmediate(true)
    slider.addValueChangeListener(new Property.ValueChangeListener {
      override def valueChange(event: ValueChangeEvent): Unit = {
        labels.get(setting).get.setValue("%1.0f".format(event.getProperty.getValue))
        if (!triggeringDisabled) {
          eventRouter.fireEvent(new DoConversionEvent(false, false))
        }
      }
    })
    slider
  }

  private def makeSliderRow(i: Int): Unit = {
    val setting = settings(i)
    addAligned(boxes.get(setting).get, 0, i)
    addAligned(sliders.get(setting).get, 1, i)
    addAligned(labels.get(setting).get, 2, i)
  }

  private def addAligned(comp: Component, row: Int, i: Int): Unit = {
    addComponent(comp, row, i)
    setComponentAlignment(comp, Alignment.MIDDLE_LEFT)
  }

  def reset(hasAnsi: Boolean): Unit = {
    val fullBlock = hasAnsi
    triggeringDisabled = true
    val vals = Setting.getInitial(hasAnsi, fullBlock)
    settings.foreach(set(_))
    def set(setting: PS): Unit = {
      boxes.get(setting).get.setValue(vals.has(setting))
      sliders.get(setting) match {
        case Some(s) => s.setValue(vals.get(setting))
        case None => { }
      }
    }
    boxes.get(Setting.FULLBLOCK).get.setEnabled(hasAnsi)
    triggeringDisabled = false
  }

  def getSettings: Setting.Instance = {
    def makePair(setting: PS): Setting.Pair = {
      (boxes.get(setting).get.getValue,
      sliders.get(setting).get.getValue.intValue)
    }
    def makeBool(setting: PS): Boolean = {
      boxes.get(setting).get.getValue
    }
    new Setting.Instance(
      makePair(Setting.UPDOWN), makePair(Setting.LEFTRIGHT),
      makeBool(Setting.DBQP), makeBool(Setting.DIAGONAL),
      makeBool(Setting.VERTICAL), makeBool(Setting.HORIZONTAL),
      makeBool(Setting.FULLBLOCK))
  }
}
