package jenkem.bot

import java.util.HashMap
import scala.reflect.BeanProperty
import jenkem.engine.Method
import jenkem.engine.Kick
import jenkem.engine.color.Power
import jenkem.engine.Pal
import jenkem.engine.color.Scheme
import jenkem.engine.Engine
import jenkem.engine.Setting
import jenkem.util.InitUtil

class ConversionSettings {
  @BeanProperty var width: Int = _
  @BeanProperty var method: Method = _
  @BeanProperty var colorMap: Map[Scheme.IrcColor, Short] = _
  @BeanProperty var schemeName: String = _
  @BeanProperty var chars: String = _
  @BeanProperty var kick: Kick = _
  @BeanProperty var power: Power = _

  val defaultWidth = 64

  def reset: Unit = {
    width = defaultWidth
    method = Method.Vortacular
    colorMap = Scheme.createColorMap(Scheme.Default)
    schemeName = Scheme.Default.name
    chars = Pal.Ansi.chars
    kick = Kick.OFF
    power = Power.Linear
  }

  def getParams(imageData: Map[(Int, Int), (Short, Short, Short)]): Engine.Params = {
    val (meth, scheme, charset) = InitUtil.getDefaults(imageData)
    if (method.hasColor != meth.hasColor) {
      method = meth
    }
    if (scheme.equals(Scheme.Bwg)) {
      colorMap = Scheme.createColorMap(scheme)
      schemeName = scheme.name
    }
    if (!method.hasColor) {
      if (Pal.hasAnsi(chars)) { chars = charset.chars }
    }
    new Engine.Params(
        method,
        imageData,
        colorMap,
        chars.replaceAll("[,0-9]", ""),
        Setting.getInitial(Pal.hasAnsi(chars)),
        InitUtil.getDefaultContrast(imageData),
        InitUtil.getDefaultBrightness(imageData),
        power
    )
  }
}
