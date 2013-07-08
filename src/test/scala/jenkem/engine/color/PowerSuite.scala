/*
 * #%L
 * PowerSuite.scala - Jenkem - Tok - 2012
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

@RunWith(classOf[JUnitRunner])
class PowerSuite extends AbstractTester {
  val linear = "linear"
  val quadratic = "quadratic"
  val cubic = "cubic"
  val quartic = "quartic"

  test("Default") {
    assert(Power.default === Power.Linear)
  }

  test("Offsets") {
    assert(Power.Linear.exponent === 1F)
    assert(Power.Quadratic.exponent === 2F)
    assert(Power.Cubic.exponent === 3F)
    assert(Power.Quartic.exponent === 4F)
  }

  test("Values") {
    assert(Power.valueOf(linear).isInstanceOf[Option[Power]])
    assert(Power.valueOf(quadratic).isInstanceOf[Option[Power]])
    assert(Power.valueOf(cubic).isInstanceOf[Option[Power]])
    assert(Power.valueOf(quartic).isInstanceOf[Option[Power]])
    assert(Power.valueOf(linear).get === Power.Linear)
    assert(Power.valueOf(quadratic).get === Power.Quadratic)
    assert(Power.valueOf(cubic).get === Power.Cubic)
    assert(Power.valueOf(quartic).get === Power.Quartic)
    assert(Power.valueOf("--") === None)
    assert(Power.Linear.toString.equalsIgnoreCase(linear))
    assert(Power.Quadratic.toString.equalsIgnoreCase(quadratic))
    assert(Power.Cubic.toString.equalsIgnoreCase(cubic))
    assert(Power.Quartic.toString.equalsIgnoreCase(quartic))
  }

  test("Pointless") {
    Power.values.foreach(testAny(_, true))
  }
}
