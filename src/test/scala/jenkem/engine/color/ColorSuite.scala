/*
 * #%L
 * ColorSuite.scala - Jenkem - Tok - 2012
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
import org.scalatest.junit.JUnitRunner

import jenkem.AbstractTester

@RunWith(classOf[JUnitRunner])
class ColorSuite extends AbstractTester {
  val red = Scheme.Red
  val black = Scheme.Black

  test("Default") {
    assert(Color.MAX === 255)
    assert(Color.CENTER === 127)
    assert(Color.MIN === 0)
  }

  test("Instance") {
    val c = Color(red.irc.toString, red.rgb, black.irc.toString, black.rgb, 1F)
    assert(c.fg === "4")
    assert(c.fgRgb === (255, 0, 0))
    assert(c.bg === "1")
    assert(c.bgRgb === (0, 0, 0))
    assert(c.strength === 1F)
  }

  test("Pointless") {
    val c = Color(red.irc.toString, red.rgb, black.irc.toString, black.rgb, 1F)
    testAny(c, false)
    val p = c.asInstanceOf[Product]
    assert(p.productElement(0) === "4")
    assert(p.productArity === 5)
    assert(p.productPrefix === "Color")
  }
}
