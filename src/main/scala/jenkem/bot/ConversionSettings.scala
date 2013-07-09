/*
 * #%L
 * ConversionSettings.scala - Jenkem - Tok - 2012
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
  @BeanProperty var colorMap: Color.IrcMap = _
  @BeanProperty var schemeName: String = _
  @BeanProperty var chars: String = _
  @BeanProperty var kick: Kick = _
  @BeanProperty var power: Power = _
  @BeanProperty var proportion: Proportion = _
  @BeanProperty var fullBlock: Boolean = _

  reset

  def reset: Unit = {
    width = InitUtil.DEFAULT_WIDTH
    method = Method.default
    colorMap = Scheme.createColorMap(Scheme.default)
    schemeName = Scheme.default.name
    chars = Pal.default.chars
    kick = Kick.OFF
    power = Power.default
    proportion = Proportion.default
    fullBlock = Pal.hasAnsi(Pal.default.chars)
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
        Setting.getInitial(Pal.hasAnsi(chars), fullBlock),
        0,
        0,
        power
    )
  }
}
