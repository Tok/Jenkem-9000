package jenkem.engine

sealed class Method(val name: String, val hasColor: Boolean)

object Method {
  case object Vortacular extends Method("Vortacular", true)
  case object Pwntari extends Method("Pwntari", true)
  case object Plain extends Method("Plain", false)
  case object Stencil extends Method("Stencil", false)
  val values = List(Vortacular, Pwntari, Plain, Stencil)
  def valueOf(name: String): Option[Method] = values.find(_.name.equalsIgnoreCase(name))
}
