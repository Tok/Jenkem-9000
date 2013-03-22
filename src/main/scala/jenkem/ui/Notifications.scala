package jenkem.ui

import com.vaadin.server.Page
import com.vaadin.ui.Notification

object Notifications {
  val imageSaved = new Notification("Image submitted to gallery.", Notification.Type.HUMANIZED_MESSAGE)
  imageSaved.setDelayMsec(1500)
  def showImageSaved(page: Page) { imageSaved.show(page) }

  val imageNotSaved = new Notification("Image not submitted!", Notification.Type.ERROR_MESSAGE)
  imageNotSaved.setDelayMsec(1500)
  def showImageNotSaved(page: Page) { imageNotSaved.show(page) }

  val dbNotConnected = new Notification("Database is not connected!", Notification.Type.ERROR_MESSAGE)
  dbNotConnected.setDelayMsec(1500)
  def showDbNotConnected(page: Page) { dbNotConnected.show(page) }

  val playToIrc = new Notification("Sending conversion to IRC.", Notification.Type.HUMANIZED_MESSAGE)
  playToIrc.setDelayMsec(1000)
  def showPlayToIrc(page: Page) { playToIrc.show(page) }

  val connectToIrc = new Notification("Connecting to IRC.", "Please wait and refresh the bot status.", Notification.Type.HUMANIZED_MESSAGE)
  connectToIrc.setDelayMsec(2000)
  def showConnectToIrc(page: Page) { connectToIrc.show(page) }
}