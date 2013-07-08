/*
 * #%L
 * KickSuite.scala - Jenkem - Tok - 2012
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
class KickSuite extends AbstractTester {
  val x = "x"
  val y = "y"
  val xy = "xy"
  val off = "off"

  test("Default") {
    assert(Kick.default === Kick.OFF)
  }

  test("Offsets") {
    assert(Kick.OFF.xOffset === 0)
    assert(Kick.OFF.yOffset === 0)
    assert(Kick.X.xOffset === 1)
    assert(Kick.X.yOffset === 0)
    assert(Kick.Y.xOffset === 0)
    assert(Kick.Y.yOffset === 1)
    assert(Kick.XY.xOffset === 1)
    assert(Kick.XY.yOffset === 1)
  }

  test("Values") {
    assert(Kick.valueOf(off).isInstanceOf[Option[Kick]])
    assert(Kick.valueOf(x).isInstanceOf[Option[Kick]])
    assert(Kick.valueOf(y).isInstanceOf[Option[Kick]])
    assert(Kick.valueOf(xy).isInstanceOf[Option[Kick]])
    assert(Kick.valueOf(off).get === Kick.OFF)
    assert(Kick.valueOf(x).get === Kick.X)
    assert(Kick.valueOf(y).get === Kick.Y)
    assert(Kick.valueOf(xy).get === Kick.XY)
    assert(Kick.valueOf("--") === None)
    assert(Kick.OFF.toString.equalsIgnoreCase(off))
    assert(Kick.X.toString.equalsIgnoreCase(x))
    assert(Kick.Y.toString.equalsIgnoreCase(y))
    assert(Kick.XY.toString.equalsIgnoreCase(xy))
  }

  test("Pointless") {
    Kick.values.foreach(testAny(_, true))
  }
}
