package jenkem.engine.color

case class Color(
    val fg: String, val fgRgb: (Short, Short, Short),
    val bg: String, val bgRgb: (Short, Short, Short), val strength: Double)
