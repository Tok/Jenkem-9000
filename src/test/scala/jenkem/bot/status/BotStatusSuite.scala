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
