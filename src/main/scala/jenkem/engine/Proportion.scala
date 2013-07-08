/*
 * #%L
 * Proportion.scala - Jenkem - Tok - 2012
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

import jenkem.util.InitUtil

sealed abstract class Proportion
object Proportion {
  case object Optimize extends Proportion
  case object Exact extends Proportion
  val values = List[Proportion](Optimize, Exact)
  def valueOf(name: String): Option[Proportion] = values.find(_.toString.equalsIgnoreCase(name))
  def default: Proportion = Optimize
  def getWidthAndHeight(prop: Proportion, method: Method, maxWidth: Int,
      originalWidth: Int, originalHeight: Int): (Int, Int) = {
    if (prop.equals(Proportion.Optimize)) {
      InitUtil.calculateProportionalSize(method, maxWidth, originalWidth, originalHeight)
    } else {
      InitUtil.calculateNewSize(method, maxWidth, originalWidth, originalHeight)
    }
  }
}
