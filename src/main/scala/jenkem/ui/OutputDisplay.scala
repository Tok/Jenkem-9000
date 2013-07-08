/*
 * #%L
 * OutputDisplay.scala - Jenkem - Tok - 2012
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

import com.vaadin.event.FieldEvents.FocusEvent
import com.vaadin.event.FieldEvents.FocusListener
import com.vaadin.ui.Label
import com.vaadin.ui.TextArea
import com.vaadin.ui.VerticalLayout

class OutputDisplay extends VerticalLayout {
  setSpacing(true)
  setWidth("400px")

  val outputCaptionLabel = new Label("Binary Output For IRC: ")
  val ircText = new TextArea
  ircText.setWidth("400px")
  ircText.setWordwrap(false)
  //ircText.setReadOnly(true)
  ircText.addFocusListener(new FocusListener {
    override def focus(event: FocusEvent): Unit = ircText.selectAll
  })
  addComponent(outputCaptionLabel)
  addComponent(ircText)

  def addIrcOutput(ircOutput: List[String]): Unit = {
    val lines = new StringBuilder(ircOutput.size)
    ircOutput.foreach(line => lines.append(line))
    ircText.setValue(lines.toString)
  }
}
