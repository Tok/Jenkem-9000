/*
 * #%L
 * IrcSettings.scala - Jenkem - Tok - 2012
 * %%
 * Copyright (C) 2012 - 2013 Lukas Steiger <lsteiger4@gmail.com>
 * %%
 * This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar.
 * See http://www.wtfpl.net/ for more details.
 * #L%
 */
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
