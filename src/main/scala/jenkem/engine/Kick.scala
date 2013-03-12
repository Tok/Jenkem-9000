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

  def default = OFF
  def getAll = List(OFF, X, Y, XY)
  def valueOf(name: String): Value = {
    name.toUpperCase match {
      case "OFF" | "0" => OFF
      case "X" => X
      case "Y" => Y
      case "XY" => XY
      case _ => throw new IllegalArgumentException("Kick name must be one of: \"0\", \"X\", \"Y\" or \"XY\".")
    }
  }
}
