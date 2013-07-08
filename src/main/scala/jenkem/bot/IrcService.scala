/*
 * #%L
 * IrcService.scala - Jenkem - Tok - 2012
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
package jenkem.bot

import scala.collection.JavaConverters.asScalaBufferConverter
import jenkem.bot.status.BotStatus

/**
 * Implementation of service to handle messages for IRC.
 */
object IrcService {
  val bot = new JenkemBot

  def disconnect: String = { bot.disconnect;

"Disconnected." }
  def connect(network: String, port: Int, channel: String, nick: String): String = {
    bot.connectAndJoin(network, port, channel, nick)
  }

  def getBotStatus: BotStatus.Value = Sender.botStatus
  def setDelay(delay: Int): Unit = bot.setMessageDelay(delay)
  def sendMessage(image: java.util.List[String]): String = Sender.playImage(bot, image.asScala.toList)
}
