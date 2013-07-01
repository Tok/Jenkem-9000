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
import jenkem.engine.color.Color
import jenkem.engine.Proportion

class ConversionSettings {
  @BeanProperty var width: Int = _
  @BeanProperty var method: Method = _
  @BeanProperty var colorMap: Map[Scheme.IrcColor, Short] = _
  @BeanProperty var schemeName: String = _
  @BeanProperty var chars: String = _
  @BeanProperty var kick: Kick = _
  @BeanProperty var power: Power = _
  @BeanProperty var proportion: Proportion = _

  reset

  def reset: Unit = {
    width = InitUtil.DEFAULT_WIDTH
    method = Method.Vortacular
    colorMap = Scheme.createColorMap(Scheme.Default)
    schemeName = Scheme.Default.name
    chars = Pal.Ansi.chars
    kick = Kick.OFF
    power = Power.Linear
    proportion = Proportion.default
  }

  def getParams(imageData: Color.RgbMap): Engine.Params = {
    val (methodOpt, scheme, charsetOpt) = InitUtil.getDefaults(imageData)
    methodOpt match {
      case Some(meth) =>
        if (method.hasColor != meth.hasColor && !method.equals(Method.Pwntari)) {
          //if method is Pwntari it should not be changed here
          //because it uses different scaling.
          method = meth
        }
      case None => { }
    }
    if (scheme.equals(Scheme.Bwg)) {
      colorMap = Scheme.createColorMap(scheme)
      schemeName = scheme.name
    }
    charsetOpt match {
      case Some(chrset) =>
        if (!method.hasColor) {
          if (Pal.hasAnsi(chars)) { chars = chrset.chars }
        } else if (method.equals(Method.Pwntari)) {
          chars = "▄"
        }
      case None => { }
    }
    new Engine.Params(
        method,
        imageData,
        colorMap,
        chars.replaceAll("[,0-9]", ""),
        Setting.getInitial(Pal.hasAnsi(chars)),
        0,
        0,
        power
    )
  }
}
