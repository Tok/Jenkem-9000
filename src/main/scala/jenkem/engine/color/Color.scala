package jenkem.engine.color

case class Color(
  val fg: String, val fgRgb: Color.RGB,
  val bg: String, val bgRgb: Color.RGB, val strength: Float)

object Color {
  type RGB = (Short, Short, Short)
  val MAX: Short = 255
  val CENTER: Short = 127
  val MIN: Short = 0
}
