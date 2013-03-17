package jenkem.engine.color

object Power {
  sealed class Value(val name: String, val exponent: Double)
  case object Linear extends Value("Linear", 1D)
  case object Quadratic extends Value("Quadratic", 2D)
  case object Cubic extends Value("Cubic", 3D)
  case object Quartic extends Value("Quartic", 4D)
  val values = List(Linear, Quadratic, Cubic, Quartic)

  def valueOf(name: String): Option[Value] = values.find(_.name.equalsIgnoreCase(name))
}
