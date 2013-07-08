/*
 * #%L
 * ImagePreparerSuite.scala - Jenkem - Tok - 2012
 * %%
 * Copyright (C) 2012 - 2013 Lukas Steiger
 *                 <lsteiger4@gmail.com>
 * %%
 * This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar.
 * See http://www.wtfpl.net/ for more details.
 * #L%
 */
package jenkem.ui

import scala.collection.mutable.ArrayBuffer
import org.junit.runner.RunWith
import org.scalatest.PrivateMethodTester.PrivateMethod
import org.scalatest.PrivateMethodTester.anyRefToInvoker
import com.vaadin.shared.ui.JavaScriptComponentState
import jenkem.AbstractTester
import org.scalatest.junit.JUnitRunner
import com.vaadin.event.EventRouter
import jenkem.js.CropperChangeListener
import jenkem.js.Crops
import com.vaadin.ui.Button
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.data.Property
import com.vaadin.ui.Field
import com.vaadin.ui.Component
import com.vaadin.event.FieldEvents.FocusEvent
import com.vaadin.event.FieldEvents.FocusListener
import java.awt.image.BufferedImage
import java.net.URL
import com.vaadin.data.Property.ValueChangeEvent

@RunWith(classOf[JUnitRunner])
class ImagePreparerSuite extends AbstractTester {
  val eventRouter = new EventRouter
  val ip = new ImagePreparer(eventRouter)
  val crops = new Crops(0, 0, 100, 100, 100, 100)
  val no = "()"
  val bogus = "--"
  val bogusLink = "http://127.0.0.1/#link"

  test("Default") {
    assert(!ip.disableTrigger)
    assert(ip.getBrightness === 0)
    assert(ip.getContrast === 0)
    assert(!ip.hasLink)
  }

  test("Trigger Cropper Change Listener") {
    val listener = ip.cropper.listeners.iterator.next.asInstanceOf[CropperChangeListener]
    assert(listener.valueChange(crops).toString === no)
    ip.cropper.addListener(new CropperChangeListener {
      override def valueChange(c: Crops) {}
    })
  }

  test("Trigger Button Click Listeners") {
    val resetAllListeners = ip.resetAllButton.getListeners(classOf[ClickEvent])
    val resetAllListener = resetAllListeners.iterator.next.asInstanceOf[Button.ClickListener]
    assert(resetAllListener.buttonClick(new ClickEvent(ip)).toString === no)
    ip.resetAllButton.addClickListener(new Button.ClickListener {
      override def buttonClick(event: ClickEvent) {}
    })
    val resetListeners = ip.resetButton.getListeners(classOf[ClickEvent])
    val resetListener = resetListeners.iterator.next.asInstanceOf[Button.ClickListener]
    assert(resetListener.buttonClick(new ClickEvent(ip)).toString === no)
    ip.resetButton.addClickListener(new Button.ClickListener {
      override def buttonClick(event: ClickEvent) {}
    })
    val convListeners = ip.convButton.getListeners(classOf[ClickEvent])
    val convListener = convListeners.iterator.next.asInstanceOf[Button.ClickListener]
    intercept[NullPointerException] {
      convListener.buttonClick(new ClickEvent(ip))
    }
    ip.convButton.addClickListener(new Button.ClickListener {
      override def buttonClick(event: ClickEvent) {}
    })
  }

  test("Text Field") {
    val field = ip.inputTextField
    val inputListeners = field.getListeners(classOf[FocusEvent])
    val inputListener = inputListeners.iterator.next.asInstanceOf[FocusListener]
    assert(inputListener.focus(new FocusEvent(field)).toString === no)
    field.addFocusListener(new FocusListener {
      override def focus(event: FocusEvent): Unit = { }
    })
    ip.reset
    field.setValue(bogusLink)
    assert(ip.getUrl.get === bogusLink)
    ip.reset
    field.setValue(bogus)
    assert(ip.getUrl === None)
  }

  test("Trigger Slider Value Change Listener") {
    val brightnessListeners = ip.brightnessSlider.getListeners(classOf[Field.ValueChangeEvent])
    val brightnessListener = brightnessListeners.iterator.next.asInstanceOf[Property.ValueChangeListener]
    assert(brightnessListener.valueChange(new Field.ValueChangeEvent(ip.brightnessSlider)).toString === no)
    ip.brightnessSlider.addValueChangeListener(new Property.ValueChangeListener {
      override def valueChange(event: ValueChangeEvent): Unit = { }
    })
    val contrastListeners = ip.contrastSlider.getListeners(classOf[Field.ValueChangeEvent])
    val contrastListener = contrastListeners.iterator.next.asInstanceOf[Property.ValueChangeListener]
    assert(contrastListener.valueChange(new Field.ValueChangeEvent(ip.contrastSlider)).toString === no)
    ip.contrastSlider.addValueChangeListener(new Property.ValueChangeListener {
      override def valueChange(event: ValueChangeEvent): Unit = { }
    })
  }

  test("Replace Image") {
    ip.reset
    val replaceImage = PrivateMethod[Unit]('replaceImage)
    ip.invokePrivate(replaceImage(new URL(bogusLink)))
    assert(ip.getUrl === None)
  }

  test("Trigger") {
    ip.reset
    val trigger = PrivateMethod[Unit]('trigger)
    ip.disableTrigger = false
    ip.invokePrivate(trigger())
    ip.disableTrigger = true
    ip.invokePrivate(trigger())
  }

  test("Update Label") {
    ip.reset
    val updateLabel = PrivateMethod[Unit]('updateLabel)
    ip.invokePrivate(updateLabel(bogus, bogus))
    ip.invokePrivate(updateLabel(bogus, ""))
  }

  test("Replace URL") {
    val replaceUrl = PrivateMethod[Unit]('replaceUrl)
    intercept[NullPointerException] { ip.invokePrivate(replaceUrl()) }
  }

  test("Getters And Setters") {
    ip.setStatus(bogus)
    ip.setError(bogus)
    ip.addIcon(new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB))
    ip.setName(bogus)
    assert(ip.getName === bogus)
    assert(!ip.isInvert)
    assert(ip.getBg === "white")
    ip.enableSubmission(false)
    ip.enableSubmission(true)
    assert(ip.getCrops === (0, 100, 0, 100))
    ip.setLink("")
    assert(!ip.hasLink)
    assert(ip.getUrl === None)
    ip.setLink(bogus)
    assert(ip.hasLink)
    assert(ip.getUrl === None)
    intercept[NullPointerException] { ip.setLink(bogusLink) }
  }
}
