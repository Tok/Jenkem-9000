package jenkem.engine.color

object Power {
  sealed class Value(val name: String, val exponent: Double)
  case object Linear extends Value("Linear", 1D)
  case object Quadratic extends Value("Quadratic", 2D)
  case object Cubic extends Value("Cubic", 3D)
  case object Quartic extends Value("Quartic", 4D)
  val values = List(Linear, Quadratic, Cubic, Quartic)

  def valueOf(name: String): Value = {
    name.toUpperCase match {
      case "LINEAR" => Linear
      case "QUADRATIC" => Quadratic
      case "CUBIC" => Cubic
      case "QUARTIC" => Quartic
      case _ => throw new IllegalArgumentException("Power must be \"Linear\", \"Quadratic\", \"Cubic\" or \"Quartic\".")
    }
  }
}
