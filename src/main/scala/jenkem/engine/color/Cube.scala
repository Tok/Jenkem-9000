package jenkem.engine.color

import jenkem.shared.color.Color
import jenkem.shared.color.IrcColor
import jenkem.shared.color.WeightedColor
import jenkem.shared.Power
import jenkem.shared.Scheme

object Cube {
  def getTwoNearestColors(col: (Short, Short, Short), colorMap: java.util.Map[IrcColor, Integer], power: Power): Color = {
    def makeWeightedList(wl: List[WeightedColor], ic: List[IrcColor]): List[WeightedColor] = {
      if (ic.isEmpty) { wl }
      else { makeWeightedList(createWc(colorMap, col, ic.head) :: wl, ic.tail) }
    }
    def calcPoweredStrength(col: (Short, Short, Short), wc: WeightedColor, p: Double): Double = {
      Math.pow(calcStrength(col, wc.getCoords, colorMap.get(wc.getColor).doubleValue), p)
    }

    val list: List[WeightedColor] = makeWeightedList(Nil, IrcColor.values.toList)
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
    val ordered: List[WeightedColor] = shuffled.sortBy(_.getWeight)
    val strongest: WeightedColor = ordered.head
    val second: WeightedColor = ordered.tail.head

    // strength is used to calculate which character to return.
    // TODO XXX FIXME tune and explain this, #math suggested Voronoi diagrams.
    // ..using a variable for power and the formula applied in calcStrength
    // is just an approximative workaround. instead there should be a
    // mathematically provable correct way on how to weigh two colors
    // against each other in regard to their distance to the
    // center of the cube 127,127,127

    val p: Double = power.getValue
    val strongestStrength = calcPoweredStrength(col, strongest, p)
    val secondStrength = calcPoweredStrength(col, second, p)
    val strength: Double = strongestStrength / secondStrength

    val c: Color = new Color
    c.setRgb(Array(col._1, col._2, col._3))
    c.setBg(strongest.getColor.getValueString)
    c.setBgRgb(strongest.getCoords)
    c.setFg(second.getColor.getValueString)
    c.setFgRgb(second.getCoords)
    c.setBgStrength(strength)

    c
  }

  def getColorChar(colorMap: java.util.Map[IrcColor, Integer],
            scheme: Scheme, charset: String, p: Power, rgb: (Short, Short, Short)): String = {
    val c: Color = getTwoNearestColors(rgb, colorMap, p)
    c.getFg + "," + c.getBg() + scheme.getChar(c.getBgStrength, charset, Scheme.StrengthType.RELATIVE)
  }

  private def createWc(colorMap: java.util.Map[IrcColor, Integer],
    col: (Short, Short, Short), ic: IrcColor): WeightedColor = {
    val weight = calcStrength(col, ic.getRgb, colorMap.get(ic).doubleValue)
    WeightedColor.getInstance(ic, weight)
  }

  private def calcStrength(col: (Short, Short, Short), comp: Array[Int], factor: Double): Double = {
    calcDistance(col, comp) / factor
  }

  private def calcDistance(from: (Short, Short, Short), to: Array[Int]): Double = {
    val fromRed = from._1
    val fromGreen = from._2
    val fromBlue = from._3
    val toRed = to(0)
    val toGreen = to(1)
    val toBlue = to(2)
    Math.sqrt((Math.pow(toRed - fromRed, 2D)
      + Math.pow(toGreen - fromGreen, 2D)
      + Math.pow(toBlue - fromBlue, 2D)))
  }

  //XXX move elsewhere
  def getBgCode(ircChar: String): String = {
     val split = ircChar.split(",")
     split(1).substring(0, split(1).length - 1);
  }
}
