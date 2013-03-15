package jenkem.engine.color

import jenkem.shared.color.IrcColor

class WeightedColor(val ircColor: IrcColor, val weight: Double) {
  def getCoords: (Short, Short, Short) = {
    val rgb = ircColor.getRgb
    (rgb(0).shortValue, rgb(1).shortValue, rgb(2).shortValue)
  }
}
