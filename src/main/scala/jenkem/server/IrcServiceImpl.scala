package jenkem.server

import scala.collection.JavaConverters._
import com.google.gwt.user.server.rpc.RemoteServiceServlet
import jenkem.client.service.IrcService
import jenkem.shared.BotStatus

/**
 * Implementation of service to handle messages for IRC.
 */
@SerialVersionUID(-1111111111111111111L)
class IrcServiceImpl extends RemoteServiceServlet with IrcService {
  val bot = new JenkemBot

  override def connect(network: String, port: Int, channel: String, nick: String): String = {
    val result = bot.connectAndJoin(network, port, channel, nick)
    result
  }

  override def disconnect(): String = {
    bot.disconnect
    "Disconnected."
  }

  override def getBotStatus(): BotStatus = bot.botStatus

  /**
   * Saves a converted JenkemImage.
   */
  override def sendMessage(image: java.util.List[String]): String = bot.playImage(image.asScala.toList)
}
