package jenkem.server

import jenkem.shared.CharacterSet
import jenkem.shared.ConversionMethod
import jenkem.shared.Kick
import jenkem.shared.ColorScheme
import jenkem.shared.Power

class ConversionSettings {
  var width = 68
  var method = ConversionMethod.Vortacular
  var scheme = ColorScheme.Default
  var charset = CharacterSet.Ansi.getCharacters
  var kick = Kick.Off
  var power = Power.Quadratic
}
