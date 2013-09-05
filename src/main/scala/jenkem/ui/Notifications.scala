/*
 * #%L
 * Notifications.scala - Jenkem - Tok - 2012
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

import com.vaadin.server.Page
import com.vaadin.ui.Notification

object Notifications {
  val imageSaved = new Notification("Image submitted to gallery.", Notification.Type.HUMANIZED_MESSAGE)
  imageSaved.setDelayMsec(1500)
  def showImageSaved(page: Page): Unit = imageSaved.show(page)

  val imageNotSaved = new Notification("Image not submitted!", Notification.Type.ERROR_MESSAGE)
  imageNotSaved.setDelayMsec(1500)
  def showImageNotSaved(page: Page): Unit = imageNotSaved.show(page)

  val imageDeleted = new Notification("Image has been delted.", Notification.Type.HUMANIZED_MESSAGE)
  imageDeleted.setDelayMsec(1500)
  def showImageDeleted(page: Page): Unit = imageDeleted.show(page)

  val dbNotConnected = new Notification("Database is not connected!", Notification.Type.ERROR_MESSAGE)
  dbNotConnected.setDelayMsec(1500)
  def showDbNotConnected(page: Page): Unit = dbNotConnected.show(page)

  val playToIrc = new Notification("Sending conversion to IRC.", Notification.Type.HUMANIZED_MESSAGE)
  playToIrc.setDelayMsec(1000)
  def showPlayToIrc(page: Page): Unit = playToIrc.show(page)

  val connectToIrc = new Notification("Connecting to IRC.", "Please wait and refresh the bot status.", Notification.Type.HUMANIZED_MESSAGE)
  connectToIrc.setDelayMsec(2000)
  def showConnectToIrc(page: Page): Unit = connectToIrc.show(page)
}
