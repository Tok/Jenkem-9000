/*
 * #%L
 * MethodSuite.scala - Jenkem - Tok - 2012
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
class MethodSuite extends AbstractTester {
  test("Values") {
    assert(Method.valueOf("vortacular").get === Method.Vortacular)
    assert(Method.valueOf("pwntari").get === Method.Pwntari)
    assert(Method.valueOf("plain").get === Method.Plain)
    assert(Method.valueOf("stencil").get === Method.Stencil)
    assert(Method.valueOf("--") === None)
  }

  test("Has Color") {
    assert(Method.Vortacular.hasColor)
    assert(Method.Pwntari.hasColor)
    assert(!Method.Plain.hasColor)
    assert(!Method.Stencil.hasColor)
  }

  test("Has Kick") {
    assert(Method.Vortacular.hasKick)
    assert(!Method.Pwntari.hasKick)
    assert(Method.Plain.hasKick)
    assert(Method.Stencil.hasKick)
  }

  test("Pointless") {
    Method.values.foreach(testAny(_, true))
  }
}
