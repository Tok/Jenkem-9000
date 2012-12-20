package jenkem.server

import java.io.IOException
import java.lang.InterruptedException
import com.google.gwt.event.shared.HandlerManager
import org.jibble.pircbot.IrcException
import org.jibble.pircbot.NickAlreadyInUseException
import org.jibble.pircbot.PircBot
import jenkem.client.event.BotStatusChangeEvent
import jenkem.shared.BotStatus

class JenkemBot extends PircBot {
  object Command extends Enumeration {
    type Command = Value
    val QUIT, GTFO, STOP, STFU, HELP, CONFIG, ASCII, COLORS, SET = Value
  }

  object ConfigItem extends Enumeration {
    type ConfigItem = Value
    val DELAY = Value
  }

  object IntExtractor {
    def unapply(s: String): Option[Int] = try {
      Some(s.toInt)
    } catch {
      case _: java.lang.NumberFormatException => None
    }
  }

  object UrlExtractor {
    def unapply(u: java.net.URL) = Some((u.getProtocol, u.getHost, u.getPort, u.getPath))
  }

  val engine = new ServerAsciiEngine
  var botStatus = new BotStatus(BotStatus.ConnectionStatus.Disconnected, BotStatus.SendStatus.NotSending, "", "", "")
  var lastChan = ""
  var stopSwitch = false
  var isPlaying = false

  def Bot {
    super.setEncoding("UTF-8")
    super.setLogin("jenkem")
    super.setVersion("Jenkem-9000")
    super.setAutoNickChange(false)
    super.setMessageDelay(1000)
  }

  /**
   * Connects the jenkem bot to IRC and joins the selected channel.
   */
  def connectAndJoin(network: String, port: Int, channel: String, nick: String): String = {
    def handleConnectionException(message: String, network: String, channel: String, nick: String): String = {
      botStatus = new BotStatus(BotStatus.ConnectionStatus.Disconnected,
         BotStatus.SendStatus.NotSending, network, channel, nick)
      message
    }
    try {
      super.setName(nick)
      connect(network, port)
      joinChannel(channel)
      lastChan = channel
      "Joining " + channel + "..."
    } catch {
      case nie: NickAlreadyInUseException => handleConnectionException("Fail: Nick is already in use.", network, channel, nick)
      case ioe: IOException => handleConnectionException("Fail: IOException: " + ioe, network, channel, nick)
      case ie: IrcException => handleConnectionException("Fail: IrcException: " + ie, network, channel, nick)
    }
  }

  override def onConnect() {
    botStatus = new BotStatus(BotStatus.ConnectionStatus.Connected,
        BotStatus.SendStatus.NotSending, getServer, lastChan, getNick)
  }

  override def onDisconnect = {
    this.botStatus = new BotStatus(BotStatus.ConnectionStatus.Disconnected,
        BotStatus.SendStatus.NotSending, getServer, lastChan, getNick)
  }

  /**
   * Shows the help-text.
   * @param target channel name or name of the receiver.
   */
  def showHelp(target: String) {
    //sendMessage(target, "Play image from url: [url]")
    sendMessage(target, "Show configuration: CONFIG")
    //sendMessage(target, "Commands that change the state of this bot can only be used in a channel and must start with " + getLogin)
    sendMessage(target, "Change config: JENKEM [ConfigItem] [Value]")
  }

  /**
   * Shows the configuration.
   * @param target channel name or name of the receiver.
   */
  def showConfig(target: String) {
    sendMessage(target, "Message Delay (ms): " + getMessageDelay)
  }

  /**
   * Handles Exceptions by forwarding them to the IRC channel.
   * @param target name of the sender or channel
   * @param e the Exception to handle
   */
  def showException(target: String, t: Throwable) {
    sendMessage(target, "FAIL: " + t.toString)
  }

  def changeConfig(sender: String, item: String, value: String) = {
    try {
      ConfigItem.withName(item.toUpperCase) match {
        case ConfigItem.DELAY => setMessageDelay(sender, value)
      }
    } catch {
      case nsee: NoSuchElementException => sendMessage(sender, "Config item unknown: " + item)
    }
  }

  def setMessageDelay(target: String, value: String) {
    val min = 100
    val max = 3000
    val between = " between " + min + " and " + max + "."
    value match {
      case IntExtractor(v) if min to max contains v =>
        setMessageDelay(v)
        sendMessage(target, ConfigItem.DELAY + " set to " + v)
      case IntExtractor(v) => sendMessage(target, ConfigItem.DELAY + " must be" + between)
      case _ => sendMessage(target, ConfigItem.DELAY + " must be a numeric value" + between)
    }
  }

  override def onMessage(channel: String, sender: String, login: String, hostname: String, message: String) {
    val m = message.split(" ")
    if (channel.equals(lastChan)) {
      if (m.head.equalsIgnoreCase("Jenkem") || m.head.equalsIgnoreCase(getLogin) || m.head.equalsIgnoreCase(getNick)) {
        try {
          Command.withName(m.tail.head.toUpperCase) match {
            case Command.GTFO => disconnect
            case Command.QUIT => disconnect
            case Command.STFU => makeStop
            case Command.STOP => makeStop
            case Command.HELP => showHelp(channel)
            case Command.CONFIG => showConfig(channel)
            case Command.SET => changeConfig(channel, m(2), m(3))
          }
        } catch {
          case nsee: NoSuchElementException => convertAndPlay(channel, m.tail.head)
        }
      }
    }
  }

  def convertAndPlay(channel: String, url: String) {
    //TODO use channel
    new java.net.URL(url) match {
      case UrlExtractor(protocol, host, port, path) => playImage(engine.generate(url))
      case _ => sendMessage(channel, "Command unknown: " + url)
    }
  }

  override def onPrivateMessage(sender: String, login: String, hostname: String, message: String) {
    val m = message.split(" ")
    try {
      Command.withName(m.head.toUpperCase) match {
        case Command.HELP => showHelp(sender)
        case Command.CONFIG => showConfig(sender)
      }
    } catch {
        case nsee: NoSuchElementException => sendMessage(sender, "Command unknown: " + message)
    }
  }

  /**
   * Takes a String[] and floods it to IRC by using a new Thread.
   * @param channel name of the channel
   * @param out a String[] with the image as ASCII for IRC.
   */
  @throws(classOf[InterruptedException])
  def playImage(out: List[String]): String = {
    this.synchronized {
      if (isPlaying) {
        "Bot is busy."
      } else if (getChannels.isEmpty) {
        "Bot is not in any channel."
      } else {
        new Thread(new IrcSender(out)).start
        "Playing image..."
      }
    }
  }

  /**
   * Runs a thread to play the image into the channel.
   * this is done in Jenkems own thread so an asynchronous command can set the stop switch while the image is forwarded to IRC.
   * Pircbots OutputMessage queue is thus bypassed by directly using sendRawLine method.
   * the tread stops when the stopSwitch is changed to true or when the image is done being played.
   */
  class IrcSender(fullImage: List[String]) extends Runnable {
    override def run {
      isPlaying = true;
      botStatus = new BotStatus(BotStatus.ConnectionStatus.Connected, BotStatus.SendStatus.Sending, getServer, lastChan, getNick)
      val sendMe = "PRIVMSG " + getChannels.head + " :"
      sendImageLine(fullImage)
      def sendImageLine(image: List[String]) {
        if (!image.isEmpty) {
          sendRawLine(sendMe + image.head)
          try {
            Thread.sleep(getMessageDelay)
          } catch {
            case ie: InterruptedException => sendRawLine(sendMe + ie.getMessage)
          }
          if (!stopSwitch) { sendImageLine(image.tail) } else { resetStop }
        } else { resetStop } //finished
      }
    }
  }

  def makeStop() {
    if (isPlaying) {
      stopSwitch = true
      isPlaying = false
    }
  }

  def resetStop() {
    stopSwitch = false;
    isPlaying = false;
    botStatus = new BotStatus(BotStatus.ConnectionStatus.Connected, BotStatus.SendStatus.NotSending, getServer, lastChan, getNick)
  }

}
