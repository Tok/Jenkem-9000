/*
 * #%L
 * Method.scala - Jenkem - Tok - 2012
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

sealed abstract class Method(val name: String, val hasColor: Boolean, val hasKick: Boolean)

object Method {
  case object Vortacular extends Method("Vortacular", true, true)
  case object Pwntari extends Method("Pwntari", true, false)
  case object Plain extends Method("Plain", false, true)
  case object Stencil extends Method("Stencil", false, true)
  val values = List(Vortacular, Pwntari, Plain, Stencil)
  def valueOf(name: String): Option[Method] = values.find(_.name.equalsIgnoreCase(name))
  val default = Vortacular
}
