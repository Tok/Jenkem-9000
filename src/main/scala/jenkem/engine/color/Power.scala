package jenkem.engine.color

sealed class Power(val name: String, val exponent: Double)

object Power {
  case object Linear extends Power("Linear", 1D)
  case object Quadratic extends Power("Quadratic", 2D)
  case object Cubic extends Power("Cubic", 3D)
  case object Quartic extends Power("Quartic", 4D)
  val values = List(Linear, Quadratic, Cubic, Quartic)
  def valueOf(name: String): Option[Power] = values.find(_.name.equalsIgnoreCase(name))
}
