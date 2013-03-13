package jenkem.bot

sealed trait ConnectionStatus
case object Connected extends ConnectionStatus
case object Disconnected extends ConnectionStatus
case object UnknownConnectionStatus extends ConnectionStatus

sealed trait SendStatus
case object Sending extends SendStatus
case object NotSending extends SendStatus
case object UnknownSendStatus extends SendStatus

class BotStatus(val connectionStatus: ConnectionStatus, val sendStatus: SendStatus,
  val network: String, val channel: String, val name: String) {
  def isConnected: Boolean = connectionStatus == Connected
  def isSending: Boolean = sendStatus == Sending
}
