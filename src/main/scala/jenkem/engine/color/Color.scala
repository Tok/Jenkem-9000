package jenkem.engine.color

case class Color(
  val fg: String, val fgRgb: Color.Rgb,
  val bg: String, val bgRgb: Color.Rgb, val strength: Float)

object Color {
  type Rgb = (Short, Short, Short)
  val MAX: Short = 255
  val CENTER: Short = 127
  val MIN: Short = 0
}
