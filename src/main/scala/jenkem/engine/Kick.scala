package jenkem.engine

/**
 * Represents the possible kicks for the conversion.
 * Depending on the Kick, Methods that convert two rows or columns at a time
 * will start at position 0 or 1 in rows (Y) or columns (X).
 *   X
 * Y +---------->
 *   | ## ## ##
 *   | ## ## ##
 *   | ## ## ##
 *   | ## ## ##
 *   v
 */
sealed abstract class Kick(val xOffset: Int, val yOffset: Int)

object Kick {
  case object OFF extends Kick(0, 0)
  case object X extends Kick(1, 0)
  case object Y extends Kick(0, 1)
  case object XY extends Kick(1, 1)
  val values: List[Kick] = List(OFF, X, Y, XY)
  def valueOf(name: String): Option[Kick] = values.find(_.toString.equalsIgnoreCase(name))
  def default: Kick = OFF
}
