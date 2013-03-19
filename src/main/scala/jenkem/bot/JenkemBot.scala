package jenkem.bot

import java.io.IOException
import java.lang.InterruptedException
import org.jibble.pircbot.IrcException
import org.jibble.pircbot.NickAlreadyInUseException
import org.jibble.pircbot.PircBot
import jenkem.engine.ConversionMethod
import jenkem.engine.Kick
import jenkem.engine.color.Power
import jenkem.engine.Pal
import jenkem.engine.color.Scheme
import jenkem.util.AwtImageUtil
import jenkem.engine.Engine
import jenkem.util.InitUtil

class JenkemBot extends PircBot {
  val defaultDelay = 1000
  def init {
    super.setEncoding("UTF-8")
    super.setLogin("jenkem")
    super.setVersion("Jenkem-9000")
    super.setAutoNickChange(false)
    super.setMessageDelay(defaultDelay)
  }
  init

  object Command extends Enumeration {
    type Command = Value
    val QUIT, GTFO, STOP, STFU, HELP, CONFIG, ASCII, COLORS, SET, RESET = Value
  }

  object ConfigItem extends Enumeration {
    type ConfigItem = Value
    val DELAY, WIDTH, SCHEME, CHARSET, CHARS, ASCII, ANSI, POWER = Value
  }

  object IntExtractor {
    def unapply(s: String): Option[Int] = try { Some(s.toInt) }
    catch { case _: java.lang.NumberFormatException => None }
  }

  val emp = ""
  val sep = " "
  val comma = ", "
  val setTo = " set to "
  val settings = new ConversionSettings

  var lastChan = emp
  var botStatus = new BotStatus(Disconnected, NotSending, emp, emp, emp)
  var stopSwitch = false
  var isPlaying = false
  var playThread = new Thread

  override def onMessage(channel: String, sender: String, login: String, hostname: String, message: String) {
    val m = message.split(sep)
    if (channel.equalsIgnoreCase(lastChan)) {
      try { executeCommand(channel, m) }
      catch {
        case nsee: NoSuchElementException => convertAndPlay(channel, m.tail.head)
      }
    }
  }

  def executeCommand(channel: String, message: Array[String]) {
    if (message.head.equalsIgnoreCase("Jenkem") ||
        message.head.equalsIgnoreCase(getLogin) ||
        message.head.equalsIgnoreCase(getNick)) {
      Command.withName(message.tail.head.toUpperCase) match {
        case Command.GTFO | Command.QUIT => disconnect
        case Command.STFU | Command.STOP => makeStop
        case Command.HELP => showHelp(channel)
        case Command.CONFIG => showConfig(channel)
        case Command.SET => changeConfig(channel, message(2), message(3))
        case Command.RESET => reset(channel)
      }
    }
  }

  override def onPrivateMessage(sender: String, login: String, hostname: String, message: String) {
    val m = message.split(sep)
    try {
      Command.withName(m.head.toUpperCase) match {
        case Command.HELP => showHelp(sender)
        case Command.CONFIG => showConfig(sender)
      }
    } catch {
      case nsee: NoSuchElementException => sendMessage(sender, "Command unknown: " + message)
    }
  }

  override def onConnect {
    botStatus = new BotStatus(Connected, NotSending, getServer, lastChan, getNick)
  }

  override def onDisconnect {
    botStatus = new BotStatus(Disconnected, NotSending, getServer, lastChan, getNick)
  }

  /**
   * Shows the help-text.
   * @param target channel name or name of the receiver.
   */
  private def showHelp(target: String) {
    sendMessage(target, "Play image from url: JENKEM [url]")
    sendMessage(target, "Change config: JENKEM [ConfigItem] [Value]")
    sendMessage(target, "  ConfigItems are: DELAY, WIDTH, SCHEME, CHARSET, CHARS, POWER")
    sendMessage(target, "Show config: JENKEM CONFIG")
    sendMessage(target, "Reset config: JENKEM RESET")
  }

  /**
   * Shows the configuration.
   * @param target channel name or name of the receiver.
   */
  private def showConfig(target: String) {
    sendMessage(target, "Delay (ms): " + getMessageDelay
        + ", Width (chars): " + settings.width
        + ", Power: " + settings.power
        + ", Scheme: " + settings.schemeName
        + ", Charset: " + settings.chars)
  }

  /**
   * Handles Exceptions by forwarding them to the IRC channel.
   * @param target name of the sender or channel
   * @param e the Exception to handle
   */
  private def showException(target: String, t: Throwable) {
    sendMessage(target, "FAIL: " + t.toString)
  }

  private def changeConfig(sender: String, item: String, value: String) {
    try {
      ConfigItem.withName(item.toUpperCase) match {
        case ConfigItem.DELAY => setMessageDelay(sender, value)
        case ConfigItem.WIDTH => setWidth(sender, value)
        case ConfigItem.SCHEME => setScheme(sender, value)
        case ConfigItem.CHARSET => setCharset(sender, value)
        case ConfigItem.CHARS | ConfigItem.ASCII | ConfigItem.ANSI => setChars(sender, value)
        case ConfigItem.POWER => setPower(sender, value)
      }
    } catch {
      case nse: NoSuchElementException => sendMessage(sender, "Config item unknown: " + item)
    }
  }

  private def setMessageDelay(target: String, value: String) {
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

  private def reset(target: String) {
    settings.reset
    sendMessage(target, "Conversion settings have been resetted.")
  }

  private def setWidth(target: String, value: String) {
    val min = 16
    val max = 80
    val between = ConfigItem.WIDTH + " must be between " + min + " and " + max + "."
    value match {
      case IntExtractor(v) if min to max contains v =>
        settings.width = v
        sendMessage(target, ConfigItem.WIDTH + setTo + v)
      case IntExtractor(v) => sendMessage(target, between)
      case _ => sendMessage(target, between)
    }
  }

  private def setScheme(target: String, value: String) {
    Scheme.valueOf(value) match {
      case Some(scheme) =>
        settings.colorMap = Scheme.createColorMap(scheme)
        settings.setSchemeName(scheme.name)
        sendMessage(target, ConfigItem.SCHEME + setTo + value)
      case None => sendMessage(target, "Scheme must be one of: " + Scheme.values.mkString(comma))
    }
  }

  private def setCharset(target: String, value: String) {
    Pal.valueOf(value) match {
      case Some(scheme) =>
        settings.chars = scheme.chars
        sendMessage(target, ConfigItem.CHARSET + setTo + scheme.chars)
      case None =>
        sendMessage(target, "Charset must be one of: " + Pal.values.mkString(comma))
    }
  }

  private def setChars(target: String, value: String) {
    settings.chars = " " + value.replaceAll("[0-9],", emp)
    sendMessage(target, "Chars set to \"" + settings.chars + "\".")
  }

  private def setPower(target: String, value: String) {
    Power.valueOf(value) match {
      case Some(power) =>
        settings.power = power
        sendMessage(target, ConfigItem.POWER + setTo + power.name)
      case None =>
        sendMessage(target, "Power must be one of " + Power.values.mkString(comma))
    }
  }

  private def convertAndPlay(channel: String, url: String) {
    jenkem.util.UrlOptionizer.extract(url) match {
      case Some(u) => playImage(generate(url, settings))
      case None => sendMessage(channel, "Command unknown: " + url)
    }
  }

  private def makeStop() {
    if (isPlaying) {
      stopSwitch = true
      isPlaying = false
    }
  }

  private def resetStop() {
    stopSwitch = false
    isPlaying = false
    botStatus = new BotStatus(Connected, NotSending, getServer, lastChan, getNick)
  }

  /**
   * Runs a thread to play the image into the channel.
   * this is done in Jenkems own thread so an asynchronous command can set the stop switch while the image is forwarded to IRC.
   * Pircbots OutputMessage queue is thus bypassed by directly using sendRawLine method.
   * the tread stops when the stopSwitch is changed to true or when the image is done being played.
   */
  class IrcSender(fullImage: List[String]) extends Runnable {
    override def run {
      stopSwitch = false
      isPlaying = true;
      botStatus = new BotStatus(Connected, Sending, getServer, lastChan, getNick)
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

  /**
   * Connects the jenkem bot to IRC and joins the selected channel.
   */
  def connectAndJoin(network: String, port: Int, channel: String, nick: String): String = {
    settings.reset
    def handleConnectionException(message: String, network: String, channel: String, nick: String): String = {
      botStatus = new BotStatus(Disconnected, NotSending, network, channel, nick)
      message
    }
    try {
      super.setName(nick)
      lastChan = channel
      connect(network, port)
      joinChannel(channel)
      "Joining " + channel + "..."
    } catch {
      case nie: NickAlreadyInUseException => handleConnectionException("Fail: Nick is already in use.", network, channel, nick)
      case ioe: IOException => handleConnectionException("Fail: IOException: " + ioe, network, channel, nick)
      case ie: IrcException => handleConnectionException("Fail: IrcException: " + ie, network, channel, nick)
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
      if (isPlaying) { "Bot is busy." }
      else if (getChannels.isEmpty) { "Bot is not in any channel." }
      else {
        playThread = new Thread(new IrcSender(out))
        playThread.start
        "Playing image..."
      }
    }
  }

  def generate(url: String, cs: ConversionSettings): List[String] = {
    val invert = false
    val originalImage = AwtImageUtil.bufferImage(url, "black", invert)
    val originalWidth = originalImage.getWidth
    val originalHeight = originalImage.getHeight
    val (width, height) = AwtImageUtil.calculateNewSize(cs.width, originalWidth, originalHeight)
    val imageRgb = AwtImageUtil.getImageRgb(originalImage, width, height, cs.kick)
    val lastIndex = height
    val params = cs.getParams(imageRgb)
    val ircOutput = List[String]()
    def generate0(index: Int): List[String] = {
      if (index + 2 > lastIndex) { System.gc; Nil }
      else { Engine.generateLine(params, index) :: generate0(index + 2) }
    }
    val colorString = if (params.method.equals(ConversionMethod.Vortacular)) {
          ", Scheme: " + cs.schemeName  + ", Power: " + params.power } else { "" }
    val message = List("Mode: " + params.method + colorString
        + ", Chars: " + params.charset + ", Width: " + (width / 2).intValue.toString
        + ", Brightness: " + params.brightness + ", Contrast: " + params.contrast)
    message ::: generate0(0)
  }
}
