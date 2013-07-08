/*
 * #%L
 * TabControllerSuite.scala - Jenkem - Tok - 2012
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
import com.vaadin.ui.Image
import jenkem.engine.Method
import com.vaadin.ui.TabSheet
import jenkem.ui.tab.MainTab
import jenkem.ui.tab.GalleryTab
import jenkem.ui.tab.InfoTab
import jenkem.DbTest
import jenkem.event.SaveImageEvent

@RunWith(classOf[JUnitRunner])
class TabControllerSuite extends AbstractTester {
  val eventRouter = new EventRouter

  test("Default", DbTest) {
    val tc = new TabController(eventRouter)
    assert(!tc.isReady)
    assert(tc.defaultUrl !== "")
    assert(tc.tabSheet.isInstanceOf[TabSheet])
    assert(tc.mainTab.isInstanceOf[MainTab])
    assert(tc.galleryTab.isInstanceOf[GalleryTab])
    assert(tc.infoTab.isInstanceOf[InfoTab])
    assert(tc.tabs.keys.toList.size === 3)
  }

  test("Select Tab", DbTest) {
    val tc = new TabController(eventRouter)
    intercept[NullPointerException] { tc.selectTab("") }
    intercept[NullPointerException] { tc.selectTab("/") }
    intercept[NullPointerException] { tc.selectTab("/#/") }
  }

  test("Fire Events", DbTest) {
    val tc = new TabController(eventRouter)
    intercept[Exception] {
      eventRouter.fireEvent(new SaveImageEvent)
    }
  }
}
