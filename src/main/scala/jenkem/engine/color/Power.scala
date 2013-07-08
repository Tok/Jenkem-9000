/*
 * #%L
 * Power.scala - Jenkem - Tok - 2012
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
package jenkem.engine.color

sealed abstract class Power(val name: String, val exponent: Float)

object Power {
  case object Linear extends Power("Linear", 1F)
  case object Quadratic extends Power("Quadratic", 2F)
  case object Cubic extends Power("Cubic", 3F)
  case object Quartic extends Power("Quartic", 4F)
  val values: List[Power] = List(Linear, Quadratic, Cubic, Quartic)
  def valueOf(name: String): Option[Power] = values.find(_.name.equalsIgnoreCase(name))
  def default: Power = Linear
}
