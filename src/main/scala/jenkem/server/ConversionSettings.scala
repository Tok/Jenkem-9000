package jenkem.server

import jenkem.shared.CharacterSet
import jenkem.shared.ConversionMethod
import jenkem.shared.Kick
import jenkem.shared.ColorScheme
import jenkem.shared.Power

class ConversionSettings {
  var width = 68
  var method = ConversionMethod.SuperHybrid
  var scheme = ColorScheme.Default
  var charset = CharacterSet.Hard.getCharacters
  var kick = Kick.Off
  var power = Power.Quadratic
}
