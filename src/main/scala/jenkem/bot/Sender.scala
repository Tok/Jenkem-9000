package jenkem.bot

object Sender {
  var botStatus = new BotStatus.Value(Disconnected, NotSending, "", "", "", IrcSettings.defaultDelayMs)
  var stopSwitch = false
  var isPlaying = false
  var playThread: Thread = None.orNull

  /**
   * Runs a thread to play the image into the channel.
   * this is done in Jenkems own thread so an asynchronous command can set the stop switch while the image is forwarded to IRC.
   * Pircbots OutputMessage queue is thus bypassed by directly using sendRawLine method.
   * the tread stops when the stopSwitch is changed to true or when the image is done being played.
   */
  class ImageSender(bot: JenkemBot, fullImage: List[String]) extends Runnable {
    override def run: Unit = {
      stopSwitch = false
      isPlaying = true
      botStatus = new BotStatus.Value(Connected, Sending, bot.getServer, bot.lastChan, bot.getNick, bot.getDelay)
      val sendMe = "PRIVMSG " + bot.getChannels.head + " :"
      sendImageLine(fullImage)
      def sendImageLine(image: List[String]): Unit = {
        if (!image.isEmpty) {
          bot.sendRawLine(sendMe + image.head)
          try {
            Thread.sleep(bot.getMessageDelay)
          } catch {
            case ie: InterruptedException => bot.sendRawLine(sendMe + ie.getMessage)
          }
          if (!stopSwitch) { sendImageLine(image.tail) } else { resetStop(bot) }
        } else { resetStop(bot) } //finished
      }
    }
  }

  /**
   * Takes a String[] and floods it to IRC by using a new Thread.
   * @param channel name of the channel
   * @param out a String[] with the image as ASCII for IRC.
   */
  @throws(classOf[InterruptedException])
  def playImage(bot: JenkemBot, out: List[String]): String = {
    this.synchronized {
      if (isPlaying) { "Bot is busy." }
      else if (bot.getChannels.isEmpty) { "Bot is not in any channel." }
      else {
        playThread = new Thread(new ImageSender(bot, out))
        playThread.start
        "Playing image..."
      }
    }
  }

  def makeStop: Unit = if (isPlaying) { stopSwitch = true; isPlaying = false }
  def resetStop(bot: JenkemBot): Unit = {
    stopSwitch = false
    isPlaying = false
    botStatus = new BotStatus.Value(Connected, NotSending, bot.getServer, bot.lastChan, bot.getNick, bot.getDelay)
  }
}
