package jenkem.server

import org.jibble.pircbot.PircBot
import org.jibble.pircbot.NickAlreadyInUseException
import java.io.IOException
import org.jibble.pircbot.IrcException
import java.util.HashMap

class JenkemBot extends PircBot {
  object Command extends Enumeration {
    type Command = Value
    val GTFO, QUIT, HELP, CONFIG, SET = Value
  }

  object ConfigItem extends Enumeration {
    type ConfigItem = Value
    val DELAY = Value
  }

  object Int { //extractor
    def unapply(s : String) : Option[Int] = try {
      Some(s.toInt)
    } catch {
      case _ : java.lang.NumberFormatException => None
    }
  }

  val log = new StringBuilder

  def Bot() {
    super.setEncoding("UTF-8")
    super.setLogin("jenkem")
    super.setVersion("Jenkem 9000")
    super.setAutoNickChange(false)
    super.setMessageDelay(1000)
  }

  /**
   * Connects the jenkem bot to IRC and joins the selected channel.
   */
  def connectAndJoin(network: String, port: Int, channel: String, nick: String): String = {
    try {
      log.setLength(0) //wipe log
      super.setName(nick)
      connect(network, port)
      joinChannel(channel)
      log.append("Connected.")
      log.toString
    } catch {
      case nie: NickAlreadyInUseException => "Fail: Nick is already in use."
      case ioe: IOException => "Fail: IOException: " + ioe
      case ie: IrcException => "Fail: IrcException: " + ie
    }
  }

  /**
   * Shows the help-text.
   * @param target channel name or name of the receiver.
   */
  def showHelp(target: String) {
    sendMessage(target, "Play image from url: [url]")
    sendMessage(target, "Show configuration: CONFIG")
    sendMessage(target, "Commands that change the state of this bot can only be used in a channel and must start with " + getLogin)
    sendMessage(target, "Change config: JENKEM [Item] [Value]")
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
    ConfigItem.withName(item.toUpperCase) match {
      case ConfigItem.DELAY => setMessageDelay(sender, value)
      case _ => sendMessage(sender, "Config item unknown: " + item)
    }
  }

  def setMessageDelay(target: String, value: String) {
    val min = 100
    val max = 3000
    val between = " between " + min + " and " + max + "."
    value match {
      case Int(v) if min to max contains v =>
        setMessageDelay(v)
        sendMessage(target, ConfigItem.DELAY + " set to " + v)
      case Int(v) => sendMessage(target, ConfigItem.DELAY + " must be" + between)
      case _ => sendMessage(target, ConfigItem.DELAY + " must be a numeric value" + between)
    }
  }

  override def onMessage(channel: String, sender: String, login: String, hostname: String, message: String) {
    val m = message.split(" ")
    if (channel.equals(getChannels.head)) {
      if (m.head.equalsIgnoreCase("Jenkem") || m.head.equalsIgnoreCase(getLogin) || m.head.equalsIgnoreCase(getNick)) {
        Command.withName(m.tail.head.toUpperCase) match {
          case Command.GTFO => disconnect
          case Command.QUIT => disconnect
          case Command.HELP => showHelp(channel)
          case Command.CONFIG => showConfig(channel)
          case Command.SET => changeConfig(channel, m(2), m(3))
          case _ => sendMessage(channel, "Command unknown: " + message)
        }
      }
    }
  }

  override def onPrivateMessage(sender: String, login: String, hostname: String, message: String) {
    val m = message.split(" ")
    Command.withName(m.head.toUpperCase) match {
      case Command.HELP => showHelp(sender)
      case Command.CONFIG => showConfig(sender)
      case _ => sendMessage(sender, "Command unknown: " + message)
    }
  }

  def getLog(): String = log.toString

  /**
   * Takes a String[] and floods it to IRC by using a new Thread.
   * @param channel name of the channel
   * @param out a String[] with the image as ASCII for IRC.
   */
  @throws(classOf[InterruptedException])
  def playImage(out: List[String]): String = {
    if (!getChannels.isEmpty) {
      new Thread(new IrcSender(out)).start
      log.append("Playing image.")
    } else {
      log.append("Bot is not in any channel.")
    }
    log.toString
  }

  /**
   * Runs a thread to play the image into the channel.
   * this is done in Jenkems own thread so an asynchronous command can set the stop switch while the image is forwarded to IRC.
   * Pircbots OutputMessage queue is thus bypassed by directly using sendRawLine method.
   * the tread stops when the stopSwitch is changed to true or when the image is done being played.
   */
  class IrcSender(fullImage: List[String]) extends Runnable {
    override def run {
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
          sendImageLine(image.tail)
        }
      }
    }
  }

}
