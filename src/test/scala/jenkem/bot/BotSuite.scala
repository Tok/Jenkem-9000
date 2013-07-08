/*
 * #%L
 * BotSuite.scala - Jenkem - Tok - 2012
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
package jenkem.bot

import org.junit.runner.RunWith
import org.scalatest.PrivateMethodTester.PrivateMethod
import org.scalatest.PrivateMethodTester.anyRefToInvoker
import jenkem.AbstractTester
import jenkem.SlowTest
import org.scalatest.junit.JUnitRunner
import jenkem.engine.Method
import jenkem.engine.color.Scheme
import jenkem.engine.Pal
import jenkem.engine.color.Power
import jenkem.engine.Proportion

@RunWith(classOf[JUnitRunner])
class BotSuite extends AbstractTester {
  val bot = new JenkemBot
  val channel = "###"
  val jenkem = "Jenkem"
  val command = "command"
  val hostname = "hostname"
  val item = "item"
  val value = "value"
  val one = "one"
  val two = "two"
  val three = "three"
  val crapValue = "--"
  val nothing = "()"
  val zero = "0"

  test("Defaults") {
    assert(bot.getVersion === "Jenkem-9000")
    assert(!bot.isConnected)
    assert(bot.getServer === None.orNull)
    assert(bot.getPort === -1)
    assert(bot.getPassword === None.orNull)
    assert(bot.lastChan === "")
  }

  test("Int Extractor") {
    assert(bot.IntExtractor.unapply(zero).get === 0)
    assert(bot.IntExtractor.unapply(Int.MinValue.toString).get === Int.MinValue)
    assert(bot.IntExtractor.unapply(Int.MaxValue.toString).get === Int.MaxValue)
    assert(bot.IntExtractor.unapply(crapValue) === None)
  }

  test("On Message") {
    assert(bot.onMessage(channel, "sender", jenkem, hostname, command).toString === nothing)
  }

  test("Evaluate Command", SlowTest) {
    val evaluateCommand = PrivateMethod[Unit]('evaluateCommand)
    val falseMessage = Array(crapValue)
    assert(bot.invokePrivate(evaluateCommand(channel, falseMessage)) === None.orNull)
    val emptyMessage = Array(jenkem)
    assert(bot.invokePrivate(evaluateCommand(channel, emptyMessage)) === None.orNull)
    val twoMessage = Array(jenkem, one)
    assert(bot.invokePrivate(evaluateCommand(channel, twoMessage)) === None.orNull)
    val threeMessage = Array(jenkem, one, two)
    assert(bot.invokePrivate(evaluateCommand(channel, threeMessage)) === None.orNull)
    val fourMessage = Array(jenkem, one, two, three)
    assert(bot.invokePrivate(evaluateCommand(channel, fourMessage)) === None.orNull)
  }

  test("Execute Command") {
    val executeCommand = PrivateMethod[Unit]('executeCommand)
    val iae = intercept[IllegalArgumentException] {
      bot.invokePrivate(executeCommand(channel, command))
    }
    assert(iae.isInstanceOf[IllegalArgumentException])
    val nsee = intercept[NoSuchElementException] {
      bot.invokePrivate(executeCommand(channel, command, None, None))
    }
    assert(nsee.isInstanceOf[NoSuchElementException])
    assert(bot.invokePrivate(executeCommand(channel, bot.Command.SET.toString, Some(item), Some(value))) === None.orNull)
    def testCommand(cName: String): Unit = {
      assert(bot.invokePrivate(executeCommand(channel, cName, None, None)) === None.orNull)
    }
    bot.Command.values.foreach(c => testCommand(c.toString))
  }

  test("Change Config") {
    val changeConfig = PrivateMethod[Unit]('changeConfig)
    val iae = intercept[IllegalArgumentException] {
      bot.invokePrivate(changeConfig(channel))
    }
    assert(iae.isInstanceOf[IllegalArgumentException])
    assert(bot.invokePrivate(changeConfig(channel, None, None)) === None.orNull)
    assert(bot.invokePrivate(changeConfig(channel, Some(item), None)) === None.orNull)
    assert(bot.invokePrivate(changeConfig(channel, Some(item), Some(value))) === None.orNull)
  }

  test("New Config Value") {
    val applyNewConfigValue = PrivateMethod[Unit]('applyNewConfigValue)
    val iae = intercept[IllegalArgumentException] {
      bot.invokePrivate(applyNewConfigValue(channel))
    }
    assert(iae.isInstanceOf[IllegalArgumentException])
    assert(bot.invokePrivate(applyNewConfigValue(channel, crapValue, crapValue)) === None.orNull)
    def testConfigItem(ciName: String): Unit = {
      assert(bot.invokePrivate(applyNewConfigValue(channel, ciName, crapValue)) === None.orNull)
    }
    bot.ConfigItem.values.foreach(ci => testConfigItem(ci.toString))
  }

  test("On Methods") {
    assert(bot.onPrivateMessage(channel, jenkem, hostname, crapValue).toString === nothing)
    assert(bot.onPrivateMessage(channel, jenkem, hostname, bot.Command.HELP.toString).toString === nothing)
    assert(bot.onPrivateMessage(channel, jenkem, hostname, bot.Command.CONFIG.toString).toString === nothing)
    assert(bot.onConnect.toString === nothing)
    assert(bot.onDisconnect.toString === nothing)
  }

  test("Delay") {
    assert(bot.getDelay === 1000)
    val setMessageDelay = PrivateMethod[Unit]('setMessageDelay)
    assert(bot.invokePrivate(setMessageDelay(channel, "-1")) === None.orNull)
    assert(bot.invokePrivate(setMessageDelay(channel, zero)) === None.orNull)
    assert(bot.invokePrivate(setMessageDelay(channel, "100")) === None.orNull)
    assert(bot.invokePrivate(setMessageDelay(channel, "1000")) === None.orNull)
    assert(bot.invokePrivate(setMessageDelay(channel, "3000")) === None.orNull)
    assert(bot.invokePrivate(setMessageDelay(channel, "10000")) === None.orNull)
    assert(bot.invokePrivate(setMessageDelay(channel, crapValue)) === None.orNull)
  }

  test("Width") {
    val setWidth = PrivateMethod[Unit]('setWidth)
    assert(bot.invokePrivate(setWidth(channel, "-1")) === None.orNull)
    assert(bot.invokePrivate(setWidth(channel, zero)) === None.orNull)
    assert(bot.invokePrivate(setWidth(channel, "16")) === None.orNull)
    assert(bot.invokePrivate(setWidth(channel, "68")) === None.orNull)
    assert(bot.invokePrivate(setWidth(channel, "80")) === None.orNull)
    assert(bot.invokePrivate(setWidth(channel, "100")) === None.orNull)
    assert(bot.invokePrivate(setWidth(channel, crapValue)) === None.orNull)
  }

  test("Method") {
    val setMethod = PrivateMethod[Unit]('setMethod)
    def testMethod(m: Method): Unit = {
      assert(bot.invokePrivate(setMethod(channel, m.toString)) === None.orNull)
    }
    Method.values.foreach(testMethod(_))
    assert(bot.invokePrivate(setMethod(channel, crapValue)) === None.orNull)
  }

  test("Scheme") {
    val setScheme = PrivateMethod[Unit]('setScheme)
    def testScheme(s: Scheme): Unit = {
      assert(bot.invokePrivate(setScheme(channel, s.toString)) === None.orNull)
    }
    Scheme.values.foreach(testScheme(_))
    assert(bot.invokePrivate(setScheme(channel, crapValue)) === None.orNull)
  }

  test("Charset") {
    val setCharset = PrivateMethod[Unit]('setCharset)
    def testCharset(cs: Pal.Charset): Unit = {
      assert(bot.invokePrivate(setCharset(channel, cs.toString)) === None.orNull)
    }
    Pal.allCharsets.foreach(testCharset(_))
    assert(bot.invokePrivate(setCharset(channel, crapValue)) === None.orNull)
  }

  test("Power") {
    val setPower = PrivateMethod[Unit]('setPower)
    def testPower(p: Power): Unit = {
      assert(bot.invokePrivate(setPower(channel, p.toString)) === None.orNull)
    }
    Power.values.foreach(testPower(_))
    assert(bot.invokePrivate(setPower(channel, crapValue)) === None.orNull)
  }

  test("Proportion") {
    val setProportion = PrivateMethod[Unit]('setProportion)
    def testPower(p: Proportion): Unit = {
      assert(bot.invokePrivate(setProportion(channel, p.toString)) === None.orNull)
    }
    Proportion.values.foreach(testPower(_))
    assert(bot.invokePrivate(setProportion(channel, crapValue)) === None.orNull)
  }
}
