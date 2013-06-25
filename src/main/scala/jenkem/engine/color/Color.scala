package jenkem.engine.color

case class Color(
  val fg: String, val fgRgb: Scheme.RGB,
  val bg: String, val bgRgb: Scheme.RGB, val strength: Float)
