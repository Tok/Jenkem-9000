/*
 * #%L
 * TabController.scala - Jenkem - Tok - 2012
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

import scala.collection.immutable.ListMap
import com.vaadin.event.EventRouter
import com.vaadin.server.Page
import com.vaadin.ui.TabSheet
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent
import jenkem.event.SaveImageEvent
import jenkem.ui.tab.GalleryTab
import jenkem.ui.tab.InfoTab
import jenkem.ui.tab.MainTab
import com.vaadin.ui.Notification

class TabController(val eventRouter: EventRouter) {
  var isReady = false
  val defaultUrl = "http://upload.wikimedia.org/wikipedia/commons/0/03/RGB_Colorcube_Corner_White.png"
  val tabSheet = new TabSheet
  val mainTab = new MainTab(eventRouter)
  val galleryTab = new GalleryTab(eventRouter, false)
  val infoTab = new InfoTab
  val tabs = ListMap(
    mainTab.getCaption.toLowerCase -> mainTab,
    galleryTab.getCaption.toLowerCase -> galleryTab,
    infoTab.getCaption.toLowerCase -> infoTab)

  def getTabSheet(page: Page): TabSheet = {
    tabSheet.setWidth("1024px")
    tabSheet.addSelectedTabChangeListener(new TabSheet.SelectedTabChangeListener() {
      override def selectedTabChange(event: SelectedTabChangeEvent): Unit = {
        if (isReady) {
          val tabsheet = event.getTabSheet
          val tab = tabsheet.getTab(tabsheet.getSelectedTab)
          Option(tab) match {
            case Some(tab) =>
              if (tab.getCaption.toLowerCase.equals("main") && !mainTab.hasLink) {
                page.setUriFragment(tab.getCaption.toLowerCase + "/" + defaultUrl)
                mainTab.setLink(defaultUrl)
              } else {
                page.setUriFragment(tab.getCaption.toLowerCase)
              }
            case None => { }
          }
        }
      }
    })
    tabs.foreach((t) => tabSheet.addTab(t._2, t._2.getCaption))
    tabSheet
  }

  def selectTab(frag: String): Unit = {
    def selectMainWithDefault(): Unit = {
      tabSheet.setSelectedTab(mainTab)
      mainTab.setLink(defaultUrl)
    }
    Option(frag) match {
      case Some(frag) =>
        val split = frag.split("/", 2)
        tabs.get(split(0)) match {
          case None =>
            if (split(0).endsWith("admin")) {
              val adminTab = new GalleryTab(eventRouter, true)
              tabSheet.addTab(adminTab, "Admin")
              tabSheet.setSelectedTab(adminTab)
            } else {
              selectMainWithDefault
            }
          case Some(tab) =>
            tabSheet.setSelectedTab(tab)
            if (tab.equals(mainTab)) {
              if (split.length > 1) { mainTab.setLink(split(1)) }
              else { mainTab.setLink(defaultUrl) }
            }
        }
      case None => selectMainWithDefault
    }
  }

  eventRouter.addListener(classOf[SaveImageEvent],
    new {
      def save: Unit = {
        mainTab.saveImage
        galleryTab.update
      }
    }, "save")
}
