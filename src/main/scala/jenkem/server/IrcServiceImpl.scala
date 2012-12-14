package jenkem.server

import com.google.gwt.user.server.rpc.RemoteServiceServlet
import jenkem.client.service.IrcService

/**
 * Implementation of service to handle messages for IRC.
 */
@SerialVersionUID(-1111111111111111111L)
class IrcServiceImpl extends RemoteServiceServlet with IrcService {

  /**
   * Saves a converted JenkemImage.
   */
  override def sendMessage(network: String, channel: String, message: String): String = {
    "Not implemented"
  }
}
