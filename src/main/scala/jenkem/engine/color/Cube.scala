/*
 * #%L
 * Cube.scala - Jenkem - Tok - 2012
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

import scala.Array.canBuildFrom
import jenkem.engine.Pal

object Cube {
  def getNearest(col: Color.Rgb, colorMap: Color.IrcMap): Short = {
    val list: List[WeightedColor] = makeWeightedList(col, colorMap, Scheme.ircColors, Nil)
    val shuffled: List[WeightedColor] = util.Random.shuffle(list)
    val ordered: List[WeightedColor] = shuffled.sortBy(_.weight)
    ordered.head.ircColor.irc
  }

  private def makeWeightedList(col: Color.Rgb, colorMap: Color.IrcMap,
      ic: List[Scheme.IrcColor], wl: List[WeightedColor]): List[WeightedColor] = {
    if (ic.isEmpty) { wl }
    else { makeWeightedList(col, colorMap, ic.tail, createWc(colorMap, col, ic.head) :: wl) }
  }

  def getTwoNearestColors(col: Color.Rgb, colorMap: Color.IrcMap, power: Power): Color = {
    def calcPoweredStrength(wc: WeightedColor, p: Float): Float = {
      Math.pow(calcStrength(col, wc.ircColor.rgb, colorMap.get(wc.ircColor).get.floatValue), p).toFloat
    }

    val list: List[WeightedColor] = makeWeightedList(col, colorMap, Scheme.ircColors, Nil)

    // shuffle to balance colors with same strength
    val shuffled: List[WeightedColor] = util.Random.shuffle(list)
    val ordered: List[WeightedColor] = shuffled.sortBy(_.weight)
    val strongest: WeightedColor = ordered.head
    val second: WeightedColor = ordered.tail.head

    // strength is used to calculate which character to return.
    // TODO XXX FIXME tune and explain this, #math suggested Voronoi diagrams.
    // ..using a variable for power and the formula applied in calcStrength
    // is just an approximative workaround. instead there should be a
    // mathematically provable correct way on how to weigh two colors
    // against each other in regard to their distance to the
    // center of the cube 127,127,127

    val p: Float = power.exponent
    val strongestStrength = calcPoweredStrength(strongest, p)
    val secondStrength = calcPoweredStrength(second, p)
    val strength: Float = strongestStrength / secondStrength

    new Color(
      second.ircColor.irc.toString, second.ircColor.rgb,
      strongest.ircColor.irc.toString, strongest.ircColor.rgb,
      strength)
  }

  private def calcStrength(col: Color.Rgb, comp: Color.Rgb, factor: Float): Float = {
    def calcDistance(from: Color.Rgb, to: Color.Rgb): Float = {
      Math.sqrt((Math.pow(to._1 - from._1, 2D)
        + Math.pow(to._2 - from._2, 2D)
        + Math.pow(to._3 - from._3, 2D))).toFloat
    }
    calcDistance(col, comp) / factor
  }

  private def createWc(colorMap: Color.IrcMap,
    col: (Short, Short, Short), ic: Scheme.IrcColor): WeightedColor = {
    val weight = calcStrength(col, (ic.rgb._1, ic.rgb._2, ic.rgb._3), colorMap.get(ic).get.floatValue)
    new WeightedColor(ic, weight)
  }

  def getColorChar(colorMap: Color.IrcMap,
    charset: String, p: Power, rgb: (Short, Short, Short)): String = {
    val c: Color = getTwoNearestColors(rgb, colorMap, p)
    c.fg + "," + c.bg + Pal.getChar(charset, c.strength)
  }
}
