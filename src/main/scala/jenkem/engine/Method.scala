package jenkem.engine

sealed abstract class Method(val name: String, val hasColor: Boolean, val hasKick: Boolean)

object Method {
  case object Vortacular extends Method("Vortacular", true, true)
  case object Pwntari extends Method("Pwntari", true, false)
  case object Plain extends Method("Plain", false, true)
  case object Stencil extends Method("Stencil", false, true)
  val values = List(Vortacular, Pwntari, Plain, Stencil)
  def valueOf(name: String): Option[Method] = values.find(_.name.equalsIgnoreCase(name))
  val default = Vortacular
}
