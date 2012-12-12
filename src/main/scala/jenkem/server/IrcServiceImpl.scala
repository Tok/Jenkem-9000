package jenkem.server

import com.google.gwt.user.server.rpc.RemoteServiceServlet
import jenkem.client.service.IrcService

/**
 * Implementation of service to handle messages for IRC.
 */
class IrcServiceImpl extends RemoteServiceServlet with IrcService {

  /**
   * Saves a converted JenkemImage.
   */
  @deprecated("should be replaced by a real IRC service without XMPP bridging (probably after migrating to heroku)", "01.12.2012")
  override def sendMessage(network: String, channel: String, message: String): String = {
        //val jid = new JID(channel + "%" + network + "@irc.jabber.linuxlovers.at")
        //val jid = new JID(channel + "%" + network + "@irc.jabber.me.uk")
        //val jid = new JID("#" + channel + "%" + network + "@irc.jabber.hot-chilli.net")
        /*
        val nickMsg = new MessageBuilder().withRecipientJids(jid).withBody("j9000").build
        val xmppService = XMPPServiceFactory.getXMPPService
        val nickStatus = xmppService.sendMessage(nickMsg)
        val msg = new MessageBuilder().withRecipientJids(jid).withBody(message).build
        var messageSent = false
        val status = xmppService.sendMessage(msg)
        messageSent = (status.getStatusMap().get(jid) == SendResponse.Status.SUCCESS)
        if (messageSent) {
            "Message sent."
        } else {
            "Fail sending message."
        }
        */
      "Fail"
  }
}
