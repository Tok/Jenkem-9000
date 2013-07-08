/*
 * #%L
 * Kick.scala - Jenkem - Tok - 2012
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
package jenkem.engine

/**
 * Represents the possible kicks for the conversion.
 * Depending on the Kick, Methods that convert two rows or columns at a time
 * will start at position 0 or 1 in rows (Y) or columns (X).
 *   X
 * Y +---------->
 *   | ## ## ##
 *   | ## ## ##
 *   | ## ## ##
 *   | ## ## ##
 *   v
 */
sealed abstract class Kick(val xOffset: Int, val yOffset: Int)

object Kick {
  case object OFF extends Kick(0, 0)
  case object X extends Kick(1, 0)
  case object Y extends Kick(0, 1)
  case object XY extends Kick(1, 1)
  val values: List[Kick] = List(OFF, X, Y, XY)
  def valueOf(name: String): Option[Kick] = values.find(_.toString.equalsIgnoreCase(name))
  def default: Kick = OFF
}
