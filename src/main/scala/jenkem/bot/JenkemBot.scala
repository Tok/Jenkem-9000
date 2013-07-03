package jenkem.bot

import java.io.IOException
import java.lang.InterruptedException
import org.jibble.pircbot.IrcException
import org.jibble.pircbot.NickAlreadyInUseException
import org.jibble.pircbot.PircBot
import jenkem.engine.Method
import jenkem.engine.Engine
import jenkem.engine.Pal
import jenkem.engine.color.Power
import jenkem.engine.color.Scheme
import jenkem.util.AwtImageUtil
import jenkem.util.GoogleUtil
import jenkem.util.UrlOptionizer
import jenkem.ui.ImagePreparer
import jenkem.util.InitUtil
import jenkem.engine.Proportion
import jenkem.bot.status.Connected
import jenkem.bot.status.Disconnected
import jenkem.bot.status.NotSending
import jenkem.bot.status.BotStatus

class JenkemBot extends PircBot {
  def init: Unit = {
    super.setEncoding("UTF-8")
    super.setLogin(IrcSettings.login)
    super.setVersion(IrcSettings.version)
    super.setMessageDelay(IrcSettings.defaultDelayMs)
    super.setAutoNickChange(false)
  }
  init

  object Command extends Enumeration {
    type Command = Value
    val QUIT, GTFO, STOP, STFU, HELP, CONFIG, SET, RESET = Value
  }

  object ConfigItem extends Enumeration {
    type ConfigItem = Value
    val DELAY, WIDTH, MODE, SCHEME, CHARSET, CHARS, ASCII, ANSI, POWER, PROPORTION = Value
  }

  object IntExtractor {
    def unapply(s: String): Option[Int] = try { Some(s.toInt) }
    catch { case _: java.lang.NumberFormatException => None }
  }

  val settings = new ConversionSettings
  var lastChan = ""

  override def onMessage(channel: String, sender: String, login: String, hostname: String, message: String): Unit = {
    evaluateCommand(channel, splitAtSpace(message))
  }

  private def evaluateCommand(channel: String, message: Array[String]): Unit = {
    if (message.head.equalsIgnoreCase("Jenkem") ||
      message.head.equalsIgnoreCase(getLogin) ||
      message.head.equalsIgnoreCase(getNick)) {
      if (message.tail.isEmpty) { convertAndPlay(channel, "") }
      else {
        try {
          val item = if (message.length > 2) { Some(message(2)) } else { None }
          val value = if (message.length > 3) { Some(message(3)) } else { None }
          executeCommand(channel, message.tail.head.toUpperCase, item, value)
        } catch {
          case nsee: NoSuchElementException => convertAndPlay(channel, message.tail.mkString("+"))
        }
      }
    }
  }

  private def executeCommand(channel: String, command: String, item: Option[String], value: Option[String]): Unit = {
    Command.withName(command) match {
      case Command.GTFO | Command.QUIT => disconnect
      case Command.STFU | Command.STOP => Sender.makeStop
      case Command.HELP => showHelp(channel)
      case Command.CONFIG => showConfig(channel)
      case Command.SET => changeConfig(channel, item, value)
      case Command.RESET => reset(channel)
    }
  }

  private def changeConfig(channel: String, item: Option[String], value: Option[String]): Unit = {
    item match {
      case Some(i) =>
        value match {
          case Some(v) => applyNewConfigValue(channel, i, v)
          case None => sendMessage(channel, "New value needed for " + i)
        }
      case None => sendMessage(channel, "Set command requires an item to change and a new value.")
    }
  }

  private def applyNewConfigValue(sender: String, item: String, value: String): Unit = {
    try {
      ConfigItem.withName(item.toUpperCase) match {
        case ConfigItem.DELAY => setMessageDelay(sender, value)
        case ConfigItem.WIDTH => setWidth(sender, value)
        case ConfigItem.MODE => setMethod(sender, value)
        case ConfigItem.SCHEME => setScheme(sender, value)
        case ConfigItem.CHARSET => setCharset(sender, value)
        case ConfigItem.CHARS | ConfigItem.ASCII | ConfigItem.ANSI => setChars(sender, value)
        case ConfigItem.POWER => setPower(sender, value)
        case ConfigItem.PROPORTION => setProportion(sender, value)
      }
    } catch {
      case nse: NoSuchElementException => sendMessage(sender, "Config item unknown: " + item)
    }
  }

  override def onPrivateMessage(sender: String, login: String, hostname: String, message: String): Unit = {
    val m = splitAtSpace(message)
    try {
      Command.withName(m.head.toUpperCase) match {
        case Command.HELP => showHelp(sender)
        case Command.CONFIG => showConfig(sender)
      }
    } catch {
      case nsee: NoSuchElementException => sendMessage(sender, "Command unknown: " + message)
    }
  }

  override def onConnect: Unit = {
    Sender.botStatus = new BotStatus.Value(Connected, NotSending, getServer, lastChan, getNick, getDelay)
  }

  override def onDisconnect: Unit = {
    Sender.botStatus = new BotStatus.Value(Disconnected, NotSending, getServer, lastChan, getNick, getDelay)
  }

  /**
   * Shows the help-text.
   * @param target channel name or name of the receiver.
   */
  private def showHelp(target: String): Unit = {
    sendMessage(target, "Play image from url: JENKEM [url]")
    sendMessage(target, "Let jenkem search for an image to play: JENKEM [search term]")
    sendMessage(target, "Change config: JENKEM [ConfigItem] [Value]")
    sendMessage(target, "  ConfigItems are: MODE, DELAY, WIDTH, SCHEME, CHARSET, CHARS, POWER, PROPORTION")
    sendMessage(target, "Show config: JENKEM CONFIG")
    sendMessage(target, "Reset config: JENKEM RESET")
  }

  /**
   * Shows the configuration.
   * @param target channel name or name of the receiver.
   */
  private def showConfig(target: String): Unit = {
    sendMessage(target, "Mode: " + settings.getMethod
      + ", Delay (ms): " + getMessageDelay
      + ", Width (chars): " + settings.width
      + ", Power: " + settings.power
      + ", Scheme: " + settings.schemeName
      + ", Charset: " + settings.chars
      + ", Proportion: " + settings.proportion)
  }

  def getDelay: Int = super.getMessageDelay.toInt

  private def setMessageDelay(target: String, value: String): Unit = {
    val min = 100
    val max = 3000
    val between = " between " + min + " and " + max + "."
    value match {
      case IntExtractor(v) if min to max contains v =>
        setMessageDelay(v)
        sendMessage(target, ConfigItem.DELAY + " set to " + v)
        Sender.botStatus = new BotStatus.Value(Connected, NotSending, getServer, lastChan, getNick, getDelay)
      case IntExtractor(v) => sendMessage(target, ConfigItem.DELAY + " must be" + between)
      case _ => sendMessage(target, ConfigItem.DELAY + " must be a numeric value" + between)
    }
  }

  private def reset(target: String): Unit = {
    settings.reset
    sendMessage(target, "Conversion settings have been resetted.")
  }

  private def setWidth(target: String, value: String): Unit = {
    val min = 16
    val max = 80
    val between = ConfigItem.WIDTH + " must be between " + min + " and " + max + "."
    value match {
      case IntExtractor(v) if min to max contains v =>
        settings.width = v
        reportConfigChange(target, ConfigItem.WIDTH, v.toString)
      case IntExtractor(v) => sendMessage(target, between)
      case _ => sendMessage(target, between)
    }
  }

  private def setMethod(target: String, value: String): Unit = {
    Method.valueOf(value) match {
      case Some(method) =>
        settings.method = method
        reportConfigChange(target, ConfigItem.MODE, value)
      case None => sendMessage(target, "Mode must be one of: " + makeString(Method.values))
    }
  }

  private def setScheme(target: String, value: String): Unit = {
    Scheme.valueOf(value) match {
      case Some(scheme) =>
        settings.colorMap = Scheme.createColorMap(scheme)
        settings.setSchemeName(scheme.name)
        reportConfigChange(target, ConfigItem.SCHEME, value)
      case None => sendMessage(target, "Scheme must be one of: " + makeString(Scheme.values))
    }
  }

  private def setCharset(target: String, value: String): Unit = {
    Pal.valueOf(value) match {
      case Some(scheme) =>
        settings.chars = scheme.chars
        reportConfigChange(target, ConfigItem.CHARSET, scheme.chars)
      case None =>
        sendMessage(target, "Charset must be one of: " + makeString(Pal.values))
    }
  }

  private def setChars(target: String, value: String): Unit = {
    settings.chars = " " + value.replaceAll("[0-9],", "")
    sendMessage(target, "Chars set to \"" + settings.chars + "\".")
  }

  private def setPower(target: String, value: String): Unit = {
    Power.valueOf(value) match {
      case Some(power) =>
        settings.power = power
        reportConfigChange(target, ConfigItem.POWER, power.name)
      case None =>
        sendMessage(target, "Power must be one of " + makeString(Power.values))
    }
  }

  private def setProportion(target: String, value: String): Unit = {
    Proportion.valueOf(value) match {
      case Some(proportion) =>
        settings.proportion = proportion
        reportConfigChange(target, ConfigItem.PROPORTION, proportion.toString)
      case None =>
        sendMessage(target, "Proportion must be one of " + makeString(Proportion.values))
    }
  }

  private def reportConfigChange(target: String, item: ConfigItem.Value, value: String): Unit = {
    sendMessage(target, item + " set to " + value)
  }

  private def convertAndPlay(channel: String, urlOrTerm: String): Unit = {
    UrlOptionizer.extract(urlOrTerm) match {
      case Some(u) => //is url
        Sender.playImage(this, generate(urlOrTerm, settings))
      case None => //is term
        GoogleUtil.getUrlForTerm(urlOrTerm) match {
          case Some(imageUrl) =>
            sendMessage(channel, imageUrl)
            Sender.playImage(this, generate(imageUrl, settings))
          case None => sendMessage(channel, "Fail: Cannot find image for \"" + urlOrTerm + "\"")
        }
    }
    if (settings.schemeName.equals(Scheme.Bwg.name)) { settings.reset }
  }

  private def splitAtSpace(message: String): Array[String] = message.split(" ")
  private def makeString(list: List[Any]): String = list.mkString(", ")

  /**
   * Connects the jenkem bot to IRC and joins the selected channel.
   */
  def connectAndJoin(network: String, port: Int, channel: String, nick: String): String = {
    settings.reset
    def handleConnectionException(message: String, network: String, channel: String, nick: String): String = {
      Sender.botStatus = new BotStatus.Value(Disconnected, NotSending, network, channel, nick, getDelay)
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

  private def generate(url: String, cs: ConversionSettings): List[String] = {
    val invert = false
    val originalImage = AwtImageUtil.bufferImage(url, "white", invert)
    val originalWidth = originalImage.getWidth
    val originalHeight = originalImage.getHeight
    val (width, height) = Proportion.getWidthAndHeight(cs.proportion, cs.method, cs.width, originalWidth, originalHeight)
    val scaled = AwtImageUtil.getScaled(originalImage, width, height, cs.kick, 0, 0)
    val imageRgb = AwtImageUtil.getImageRgb(scaled)
    val lastIndex = height
    val params = cs.getParams(imageRgb)
    def generate0(index: Int): List[String] = {
      if (index + 2 > lastIndex) { Nil }
      else { Engine.generateLine(params, index) :: generate0(index + 2) }
    }
    val hasColor = params.method.hasColor
    val notPwntari = !params.method.equals(Method.Pwntari)
    val colorString = if (hasColor) { ", Scheme: " + cs.schemeName } else { "" }
    val powerString = if (notPwntari) { ", Power: " + params.power } else { "" }
    val charsString = if (notPwntari) { ", Chars: " + params.characters } else { "" }
    val widthDivisor = if (!params.method.equals(Method.Pwntari)) { 2 } else { 1 }
    val message = List("Mode: " + params.method + colorString + powerString
      + charsString + ", Width: " + (width / widthDivisor).intValue.toString)
    message ::: generate0(0)
  }
}
