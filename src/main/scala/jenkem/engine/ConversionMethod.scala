package jenkem.engine

object ConversionMethod {
  sealed class Value(val name: String)
  case object Vortacular extends Value("Vortacular")
  case object Plain extends Value("Plain")
  case object Stencil extends Value("Stencil")
  val values = List(Vortacular, Plain, Stencil)

  def valueOf(name: String): Value = {
    name.toUpperCase match {
      case "VORTACULAR" => Vortacular
      case "PLAIN" => Plain
      case "STENCIL" => Stencil
      case _ => throw new IllegalArgumentException("Method must be one of: \"Vortacular\", \"Plain\" or \"Stencil\".")
    }
  }
}
