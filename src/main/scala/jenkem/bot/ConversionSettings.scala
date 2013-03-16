package jenkem.bot

import java.util.HashMap
import scala.reflect.BeanProperty
import jenkem.engine.ConversionMethod
import jenkem.engine.Kick
import jenkem.engine.color.Power
import jenkem.shared.ColorScheme
import jenkem.shared.color.IrcColor
import jenkem.engine.Pal

class ConversionSettings {
  @BeanProperty var width: Int = _
  @BeanProperty var method: ConversionMethod.Value = _
  @BeanProperty var colorMap: java.util.Map[IrcColor, java.lang.Integer] = _
  @BeanProperty var schemeName: String = _
  @BeanProperty var chars: String = _
  @BeanProperty var kick: Kick.Value = _
  @BeanProperty var power: Power.Value = _

  val defaultWidth = 68

  def createColorMap(cs: ColorScheme) {
    val map: java.util.Map[IrcColor, java.lang.Integer] = new HashMap[IrcColor, java.lang.Integer]
    IrcColor.values.foreach(ic => map.put(ic, ic.getOrder(cs)))
    colorMap = map
    schemeName = cs.name
  }

  def reset {
    width = defaultWidth
    method = ConversionMethod.Vortacular
    createColorMap(ColorScheme.Default)
    chars = Pal.Ansi.chars
    kick = Kick.OFF
    power = Power.Quadratic
  }
}
