package jenkem.bot

import scala.collection.JavaConverters.asScalaBufferConverter
import jenkem.bot.status.BotStatus

/**
 * Implementation of service to handle messages for IRC.
 */
object IrcService {
  val bot = new JenkemBot

  def disconnect: String = { bot.disconnect; "Disconnected." }
  def connect(network: String, port: Int, channel: String, nick: String): String = {
    bot.connectAndJoin(network, port, channel, nick)
  }

  def getBotStatus: BotStatus.Value = Sender.botStatus
  def setDelay(delay: Int): Unit = bot.setMessageDelay(delay)
  def sendMessage(image: java.util.List[String]): String = Sender.playImage(bot, image.asScala.toList)
}
