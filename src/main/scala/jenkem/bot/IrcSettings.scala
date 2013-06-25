package jenkem.bot

object IrcSettings {
  object Loc extends Enumeration {
    type Loc = Value
    val OPENSHIFT, ELSEWHERE = Value
  }
  sealed class IrcNetwork(val net: String, val port: Int, val location: Loc.Value)
  val freenode = new IrcNetwork("irc.freenode.net", 8001, Loc.OPENSHIFT)
  val efnet = new IrcNetwork("efnet.xs4all.nl", 6669, Loc.ELSEWHERE) //"irc.efnet.org"
  val undernet = new IrcNetwork("irc.undernet.org", 6667, Loc.ELSEWHERE)
  val rizon = new IrcNetwork("irc.rizon.net", 6667, Loc.ELSEWHERE)
  val networks = Array(freenode, efnet, undernet, rizon)
  def getDefaultNetwork: IrcNetwork = freenode

  val defaultChannel = "#Jenkem"
  val defaultNick = "J_"
  val login = "jenkem"
  val version = "Jenkem-9000"
  val defaultDelayMs = 1000
  val maxDelayMs = 3000
  val warningDelayMs = 500
}
