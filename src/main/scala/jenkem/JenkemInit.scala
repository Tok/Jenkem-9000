/*
 * #%L
 * JenkemInit.scala - Jenkem - Tok - 2012
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
package jenkem

import com.vaadin.annotations.Theme
import com.vaadin.event.EventRouter
import com.vaadin.server.Page
import com.vaadin.server.ThemeResource
import com.vaadin.server.VaadinRequest
import com.vaadin.shared.ui.label.ContentMode
import com.vaadin.ui.Alignment
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Image
import com.vaadin.ui.Label
import com.vaadin.ui.UI
import com.vaadin.ui.VerticalLayout

import jenkem.ui.TabController

@Theme("jenkemtheme")
class JenkemInit extends UI {
  override def init(request: VaadinRequest): Unit = {
    val titleLayout = new HorizontalLayout
    val layout = new VerticalLayout
    layout.setSpacing(true)

    val eventRouter = new EventRouter

    val asciiLabel = new Label(
      "      __           _                             ________  _______  _______  _______ \n"
        + "     |  |         | |                           /   __   \\/   _   \\/   _   \\/   _   \\ \n"
        + "     |  |___ _ ___| | _ ___ _ __ __    _____   (   (__\\   \\  / \\   \\  / \\   \\  / \\   \\ \n"
        + " _   |  | _ \\ '_  \\ |/ / _ \\ '  '  \\  (_____)   \\______    )(   )   )(   )   )(   )   ) \n"
        + "/ \\__|  | __/ | | |   (  __/ || || |            ______/   /  \\_/   /  \\_/   /  \\_/   / \n"
        + "\\______/\\___)_| |_|_|\\_\\___)_||_||_|           (_________/\\_______/\\_______/\\_______/ \n\n")
    asciiLabel.setContentMode(ContentMode.PREFORMATTED)
    asciiLabel.setStyleName("asciiLabel")
    titleLayout.addComponent(asciiLabel)
    titleLayout.setComponentAlignment(asciiLabel, Alignment.MIDDLE_CENTER)

    val image = new Image("", new ThemeResource("j.png"))
    titleLayout.addComponent(image)
    titleLayout.setComponentAlignment(image, Alignment.TOP_CENTER)

    layout.addComponent(titleLayout)
    layout.setComponentAlignment(titleLayout, Alignment.TOP_CENTER)

    val tc = new TabController(eventRouter)

    val tabSheet = tc.getTabSheet(Page.getCurrent)
    layout.addComponent(tabSheet)
    layout.setComponentAlignment(tabSheet, Alignment.MIDDLE_CENTER)
    setContent(layout)

    val frag = Page.getCurrent.getUriFragment
    tc.selectTab(frag)
    tc.isReady = true
  }
}
