/*
 * #%L
 * BotStatusSuite.scala - Jenkem - Tok - 2012
 * %%
 * Copyright (C) 2012 - 2013 Lukas Steiger
 *                 <lsteiger4@gmail.com>
 * %%
 * This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar.
 * See http://www.wtfpl.net/ for more details.
 * #L%
 */
package jenkem.bot.status

import org.junit.runner.RunWith
import jenkem.AbstractTester
import jenkem.bot.IrcSettings
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class BotStatusSuite extends AbstractTester {
  val empty = ""

  test("Default") {
    val default = BotStatus.default
    assert(default.connectionStatus === Disconnected)
    assert(default.sendStatus === NotSending)
    assert(default.network === empty)
    assert(default.channel === empty)
    assert(default.name === empty)
    assert(default.delay === IrcSettings.defaultDelayMs)
  }

  test("Pointless") {
    testAny(Connected, true)
    testAny(Disconnected, true)
    testAny(UnknownConnectionStatus, true)
    testAny(Sending, true)
    testAny(NotSending, true)
    testAny(UnknownSendStatus, true)
  }
}
