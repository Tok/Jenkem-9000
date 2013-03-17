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
object Kick {
  sealed abstract class Value(val hasX: Boolean, val hasY: Boolean, val xOffset: Int, val yOffset: Int)
  case object OFF extends Value(false, false, 0, 0)
  case object X extends Value(true, false, 1, 0)
  case object Y extends Value(false, true, 0, 1)
  case object XY extends Value(true, true, 1, 1)
  val values: List[Value] = List(OFF, X, Y, XY)

  def default: Value = OFF
  def valueOf(name: String): Option[Value] = {
    name.toUpperCase match {
      case "OFF" | "0" => Option(OFF)
      case "X" => Option(X)
      case "Y" => Option(Y)
      case "XY" => Option(XY)
      case _ => None
    }
  }
}
