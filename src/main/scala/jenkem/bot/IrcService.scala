package jenkem.bot

import scala.collection.JavaConverters.asScalaBufferConverter

/**
 * Implementation of service to handle messages for IRC.
 */
@SerialVersionUID(-1111111111111111111L)
object IrcService {
  val bot = new JenkemBot

  def connect(network: String, port: Int, channel: String, nick: String): String = {
    val result = bot.connectAndJoin(network, port, channel, nick)
    result
  }

  def disconnect(): String = {
    bot.disconnect
    "Disconnected."
  }

  def getBotStatus(): BotStatus = bot.botStatus

  /**
   * Saves a converted JenkemImage.
   */
  def sendMessage(image: java.util.List[String]): String = bot.playImage(image.asScala.toList)
}
