package jenkem.server

import scala.collection.JavaConverters._
import com.google.gwt.user.server.rpc.RemoteServiceServlet
import jenkem.client.service.IrcService

/**
 * Implementation of service to handle messages for IRC.
 */
@SerialVersionUID(-1111111111111111111L)
class IrcServiceImpl extends RemoteServiceServlet with IrcService {
  val bot = new JenkemBot

  override def connect(network: String, port: Int, channel: String, nick: String): String = {
    bot.connectAndJoin(network, port, channel, nick)
  }

  override def disconnect(): String = {
    bot.disconnect
    "Disconnected."
  }

  override def isBotConnected(): Boolean = bot.isConnected

  override def getLog(): String = bot.getLog

  /**
   * Saves a converted JenkemImage.
   */
  override def sendMessage(image: java.util.List[String]): String = bot.playImage(image.asScala.toList)
}
