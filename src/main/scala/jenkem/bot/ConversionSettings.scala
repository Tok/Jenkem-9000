package jenkem.bot

import java.util.HashMap
import scala.reflect.BeanProperty
import jenkem.engine.ConversionMethod
import jenkem.engine.Kick
import jenkem.engine.color.Power
import jenkem.engine.Pal
import jenkem.engine.color.Scheme
import jenkem.engine.Engine
import jenkem.engine.ProcSettings

class ConversionSettings {
  @BeanProperty var width: Int = _
  @BeanProperty var method: ConversionMethod.Value = _
  @BeanProperty var colorMap: Map[Scheme.IrcColor, Short] = _
  @BeanProperty var schemeName: String = _
  @BeanProperty var chars: String = _
  @BeanProperty var kick: Kick.Value = _
  @BeanProperty var power: Power.Value = _

  val defaultWidth = 68

  def reset {
    width = defaultWidth
    method = ConversionMethod.Vortacular
    colorMap = Scheme.createColorMap(Scheme.Default)
    schemeName = Scheme.Default.name
    chars = Pal.Ansi.chars
    kick = Kick.OFF
    power = Power.Quadratic
  }

  def getParams(imageRgb: Map[(Int, Int), (Short, Short, Short)]): Engine.Params = {
    //TODO reimplement method and colorMap overriding
    new Engine.Params(
        method,
        imageRgb,
        colorMap,
        chars.replaceAll("[,0-9]", ""),
        ProcSettings.getInitial(Pal.hasAnsi(chars)),
        0,
        0,
        power
    )
  }
}
