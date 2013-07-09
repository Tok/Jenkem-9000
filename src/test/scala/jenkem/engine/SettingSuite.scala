/*
 * #%L
 * SettingSuite.scala - Jenkem - Tok - 2012
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
package jenkem.engine

import org.junit.runner.RunWith
import jenkem.AbstractTester
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class SettingSuite extends AbstractTester {
  val updown = "updown"
  val leftright = "leftright"
  val dbqp = "dbqp"
  val diagonal = "diagonal"
  val vertical = "vertical"
  val horizontal = "horizontal"

  test("Defaults") {
    assert(Setting.default === 0)
    assert(Setting.min <= Setting.default)
    assert(Setting.max >= Setting.default)
  }

  test("Values") {
    assert(Setting.UPDOWN.toString.equalsIgnoreCase(updown))
    assert(Setting.LEFTRIGHT.toString.equalsIgnoreCase(leftright))
    assert(Setting.DBQP.toString.equalsIgnoreCase(dbqp))
    assert(Setting.DIAGONAL.toString.equalsIgnoreCase(diagonal))
    assert(Setting.VERTICAL.toString.equalsIgnoreCase(vertical))
    assert(Setting.HORIZONTAL.toString.equalsIgnoreCase(horizontal))
  }

  test("Setting Instance") {
    val asciiSettings = Setting.getInitial(false, false)
    assert(asciiSettings.has(Setting.UPDOWN))
    assert(asciiSettings.has(Setting.LEFTRIGHT))
    assert(asciiSettings.has(Setting.DBQP))
    assert(asciiSettings.has(Setting.DIAGONAL))
    assert(!asciiSettings.has(Setting.VERTICAL))
    assert(!asciiSettings.has(Setting.HORIZONTAL))

    val ansiSettings = Setting.getInitial(true, true)
    assert(ansiSettings.has(Setting.UPDOWN))
    assert(ansiSettings.has(Setting.LEFTRIGHT))
    assert(!ansiSettings.has(Setting.DBQP))
    assert(!ansiSettings.has(Setting.DIAGONAL))
    assert(!ansiSettings.has(Setting.VERTICAL))
    assert(!ansiSettings.has(Setting.HORIZONTAL))

    def test(s: Setting): Unit = {
      assert(asciiSettings.get(s) === 0)
      assert(ansiSettings.get(s) === 0)
    }
    Setting.values.foreach(test(_))
  }

  test("Pointless") {
    Setting.values.foreach(testAny(_, true))
  }
}
