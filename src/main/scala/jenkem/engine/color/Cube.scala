package jenkem.engine.color

import scala.Array.canBuildFrom

import jenkem.engine.Pal

object Cube {
  def getTwoNearestColors(col: (Short, Short, Short), colorMap: Map[Scheme.IrcColor, Short], power: Power): Color = {
    def makeWeightedList(wl: List[WeightedColor], ic: List[Scheme.IrcColor]): List[WeightedColor] = {
      if (ic.isEmpty) { wl }
      else { makeWeightedList(createWc(colorMap, col, ic.head) :: wl, ic.tail) }
    }
    def calcPoweredStrength(col: (Short, Short, Short), wc: WeightedColor, p: Double): Double = {
      Math.pow(calcStrength(col, wc.ircColor.rgb, colorMap.get(wc.ircColor).get.doubleValue), p)
    }

    val list: List[WeightedColor] = makeWeightedList(Nil, Scheme.ircColors)
    // if the list isn't shuffled the following occurs:
    // the color that happens to be the 1st in the collection is used in
    // favor of the others,
    // when more possible values would apply with the same strength.
    // this is not good, because it often favors one random color of (RGB)
    // and of (CYM) over the others.
    // instead, all possibilities of R, G or B should use Black and C, Y or
    // M should use White instead of the color that
    // is selected by doing nothing.
    // doing this could potentially have a good effect on the output of
    // colorless images with alot of pixels
    // on the black-white scale
    // (=represented by the black to white diagonal in the cube, which has
    // the same distance to all the 3 RGB and the 3 CMY edges).

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

    val p: Double = power.exponent
    val strongestStrength = calcPoweredStrength(col, strongest, p)
    val secondStrength = calcPoweredStrength(col, second, p)
    val strength: Double = strongestStrength / secondStrength

    new Color(
        second.ircColor.irc.toString, second.ircColor.rgb,
        strongest.ircColor.irc.toString, strongest.ircColor.rgb,
        strength
    )
  }

  def getColorChar(colorMap: Map[Scheme.IrcColor, Short],
            charset: String, p: Power, rgb: (Short, Short, Short)): String = {
    val c: Color = getTwoNearestColors(rgb, colorMap, p)
    c.fg.toString + "," + c.bg.toString + Pal.getChar(charset, c.strength)
  }

  private def createWc(colorMap: Map[Scheme.IrcColor, Short],
    col: (Short, Short, Short), ic: Scheme.IrcColor): WeightedColor = {
    val weight = calcStrength(col, (ic.rgb._1,ic.rgb._2,ic.rgb._3) , colorMap.get(ic).get.doubleValue)
    new WeightedColor(ic, weight)
  }

  private def calcStrength(col: (Short, Short, Short), comp: (Short, Short, Short), factor: Double): Double = {
    calcDistance(col, comp) / factor
  }

  private def calcDistance(from: (Short, Short, Short), to: (Short, Short, Short)): Double = {
    Math.sqrt((Math.pow(to._1 - from._1, 2D)
      + Math.pow(to._2 - from._2, 2D)
      + Math.pow(to._3 - from._3, 2D)))
  }

  //XXX move elsewhere
  def getBgCode(ircChar: String): String = {
     val split = ircChar.split(",")
     split(1).substring(0, split(1).length - 1);
  }
}
