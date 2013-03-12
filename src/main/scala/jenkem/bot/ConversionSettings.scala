package jenkem.bot

import java.util.HashMap
import scala.Array.canBuildFrom
import scala.reflect.BeanProperty
import jenkem.shared.CharacterSet
import jenkem.shared.ColorScheme
import jenkem.shared.ConversionMethod
import jenkem.shared.Power
import jenkem.shared.color.IrcColor
import jenkem.engine.Kick

class ConversionSettings {
  @BeanProperty var width: Int = _
  @BeanProperty var method: ConversionMethod = _
  @BeanProperty var colorMap: java.util.Map[IrcColor, java.lang.Integer] = _
  @BeanProperty var schemeName: String = _
  @BeanProperty var chars: String = _
  @BeanProperty var kick: Kick.Value = _
  @BeanProperty var power: Power = _

  def createColorMap(cs: ColorScheme) {
    val map: java.util.Map[IrcColor, java.lang.Integer] = new HashMap[IrcColor, java.lang.Integer]
    IrcColor.values.map(ic => map.put(ic, ic.getOrder(cs)))
    colorMap = map
    schemeName = cs.name
  }

  def reset {
    width = 68
    method = ConversionMethod.Vortacular
    createColorMap(ColorScheme.Default)
    chars = CharacterSet.Ansi.getCharacters
    kick = Kick.OFF
    power = Power.Quadratic
  }
}
