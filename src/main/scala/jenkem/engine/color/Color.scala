/*
 * #%L
 * Color.scala - Jenkem - Tok - 2012
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

case class Color(
  val fg: String, val fgRgb: Color.Rgb,
  val bg: String, val bgRgb: Color.Rgb, val strength: Float)

object Color {
  type Rgb = (Short, Short, Short)
  type RgbMap = Map[(Int, Int), (Short, Short, Short)]
  type IrcMap = Map[Scheme.IrcColor, Short]
  val MAX: Short = 255
  val CENTER: Short = 127
  val MIN: Short = 0
}
