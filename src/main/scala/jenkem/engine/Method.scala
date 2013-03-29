package jenkem.engine

sealed class Method(val name: String)

object Method {
  case object Vortacular extends Method("Vortacular")
  case object Plain extends Method("Plain")
  case object Stencil extends Method("Stencil")
  val values = List(Vortacular, Plain, Stencil)
  def valueOf(name: String): Option[Method] = values.find(_.name.equalsIgnoreCase(name))
}
