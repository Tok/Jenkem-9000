package jenkem.engine.color

sealed class Power(val name: String, val exponent: Float)

object Power {
  case object Linear extends Power("Linear", 1F)
  case object Quadratic extends Power("Quadratic", 2F)
  case object Cubic extends Power("Cubic", 3F)
  case object Quartic extends Power("Quartic", 4F)
  val values = List(Linear, Quadratic, Cubic, Quartic)
  def valueOf(name: String): Option[Power] = values.find(_.name.equalsIgnoreCase(name))
}
