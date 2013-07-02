package jenkem.bot.status

import jenkem.bot.IrcSettings

object BotStatus {
  val default: Value = new Value(Disconnected, NotSending, "", "", "", IrcSettings.defaultDelayMs)
  class Value(val connectionStatus: ConnectionStatus, val sendStatus: SendStatus,
    val network: String, val channel: String, val name: String, val delay: Int) {
    val isConnected: Boolean = connectionStatus == Connected
    val isSending: Boolean = sendStatus == Sending
  }
}
