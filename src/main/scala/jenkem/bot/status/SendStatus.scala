package jenkem.bot.status

sealed trait SendStatus
case object Sending extends SendStatus
case object NotSending extends SendStatus
case object UnknownSendStatus extends SendStatus
