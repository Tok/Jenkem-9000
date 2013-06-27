package jenkem.engine.color

import scala.Array.canBuildFrom
import jenkem.engine.Pal

object Cube {
  def getNearest(col: Color.Rgb, colorMap: Map[Scheme.IrcColor, Short]): Short = {
    val list: List[WeightedColor] = makeWeightedList(col, colorMap, Scheme.ircColors, Nil)
    val shuffled: List[WeightedColor] = util.Random.shuffle(list)
    val ordered: List[WeightedColor] = shuffled.sortBy(_.weight)
    ordered.head.ircColor.irc
  }

  private def makeWeightedList(col: Color.Rgb, colorMap: Map[Scheme.IrcColor, Short],
      ic: List[Scheme.IrcColor], wl: List[WeightedColor]): List[WeightedColor] = {
    if (ic.isEmpty) { wl }
    else { makeWeightedList(col, colorMap, ic.tail, createWc(colorMap, col, ic.head) :: wl) }
  }

  def getTwoNearestColors(col: Color.Rgb, colorMap: Map[Scheme.IrcColor, Short], power: Power): Color = {
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

  private def createWc(colorMap: Map[Scheme.IrcColor, Short],
    col: (Short, Short, Short), ic: Scheme.IrcColor): WeightedColor = {
    val weight = calcStrength(col, (ic.rgb._1, ic.rgb._2, ic.rgb._3), colorMap.get(ic).get.floatValue)
    new WeightedColor(ic, weight)
  }

  def getColorChar(colorMap: Map[Scheme.IrcColor, Short],
    charset: String, p: Power, rgb: (Short, Short, Short)): String = {
    val c: Color = getTwoNearestColors(rgb, colorMap, p)
    c.fg.toString + "," + c.bg.toString + Pal.getChar(charset, c.strength)
  }
}
