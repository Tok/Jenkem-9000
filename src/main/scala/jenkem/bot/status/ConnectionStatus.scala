package jenkem.bot.status

sealed trait ConnectionStatus
case object Connected extends ConnectionStatus
case object Disconnected extends ConnectionStatus
case object UnknownConnectionStatus extends ConnectionStatus
