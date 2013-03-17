package jenkem.engine

object ConversionMethod {
  sealed class Value(val name: String)
  case object Vortacular extends Value("Vortacular")
  case object Plain extends Value("Plain")
  case object Stencil extends Value("Stencil")
  val values = List(Vortacular, Plain, Stencil)

  def valueOf(name: String): Option[Value] = values.find(_.name.equalsIgnoreCase(name))
}
