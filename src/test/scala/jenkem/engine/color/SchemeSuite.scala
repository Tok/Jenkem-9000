/*
 * #%L
 * SchemeSuite.scala - Jenkem - Tok - 2012
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
package jenkem.engine.color

import org.junit.runner.RunWith
import jenkem.AbstractTester
import org.scalatest.junit.JUnitRunner
import org.scalatest.PrivateMethodTester.PrivateMethod
import org.scalatest.PrivateMethodTester.anyRefToInvoker

@RunWith(classOf[JUnitRunner])
class SchemeSuite extends AbstractTester {
  test("Default") {
    assert(Scheme.default === Scheme.Default)
  }

  test("Orders, Names And Values") {
    def testName(s: Scheme): Unit = {
      assert(s.name.equalsIgnoreCase(s.toString))
      assert(Scheme.valueOf(s.name.toLowerCase).isInstanceOf[Option[Scheme]])
    }
    assert(Scheme.Full.order === 0)
    assert(Scheme.Default.order === 1)
    assert(Scheme.Mono.order === 2)
    assert(Scheme.Lsd.order === 3)
    assert(Scheme.Bwg.order === 4)
    assert(Scheme.Bw.order === 5)
    Scheme.values.foreach(testName(_))
  }

  test("IRC Colors") {
    def testName(ic: Scheme.IrcColor): Unit = {
      assert(ic.name.equalsIgnoreCase(ic.toString))
      assert(Scheme.valuOfIrcColor(ic.irc).isInstanceOf[Option[Scheme.IrcColor]])
    }
    assert(Scheme.White.irc === 0)
    assert(Scheme.Black.irc === 1)
    assert(Scheme.DarkBlue.irc === 2)
    assert(Scheme.DarkGreen.irc === 3)
    assert(Scheme.Red.irc === 4)
    assert(Scheme.Brown.irc === 5)
    assert(Scheme.Purple.irc === 6)
    assert(Scheme.Orange.irc === 7)
    assert(Scheme.Yellow.irc === 8)
    assert(Scheme.Green.irc === 9)
    assert(Scheme.Teal.irc === 10)
    assert(Scheme.Cyan.irc === 11)
    assert(Scheme.Blue.irc === 12)
    assert(Scheme.Magenta.irc === 13)
    assert(Scheme.Gray.irc === 14)
    assert(Scheme.LightGray.irc === 15)
    Scheme.ircColors.foreach(testName(_))
  }

  test("Color Map") {
    def testColorMap(s: Scheme): Unit = {
      assert(Scheme.createColorMap(s).size === 16)
    }
    Scheme.values.foreach(testColorMap(_))
  }

  test("Pointless") {
    Scheme.values.foreach(testAny(_, true))
    Scheme.ircColors.foreach(testAny(_, true))
    testAny(new WeightedColor(Scheme.Black, 1F), false)
  }
}
