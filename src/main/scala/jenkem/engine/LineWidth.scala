package jenkem.engine

/**
 * Number of characters in one line of output.
 * May be 1 less depending on X Kick.
 */
object LineWidth {
  sealed abstract class Value(val value: Int)
  case object Huge extends Value(72)
  case object Default extends Value(68)
  case object Large extends Value(64)
  case object Medium extends Value(56)
  case object Small extends Value(48)
  case object Mini extends Value(40)
  case object Icon extends Value(32)
  case object Tiny extends Value(16)

  def default = Default
  def getAll = List(Huge, Default, Large, Medium, Small, Mini, Icon, Tiny)
}
