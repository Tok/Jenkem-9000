package jenkem.engine

import jenkem.util.InitUtil

sealed abstract class Proportion
object Proportion {
  case object Optimize extends Proportion
  case object Exact extends Proportion
  val values = List[Proportion](Optimize, Exact)
  def valueOf(name: String): Option[Proportion] = values.find(_.toString.equalsIgnoreCase(name))
  def default: Proportion = Optimize
  def getWidthAndHeight(prop: Proportion, method: Method, maxWidth: Int,
      originalWidth: Int, originalHeight: Int): (Int, Int) = {
    if (prop.equals(Proportion.Optimize)) {
      InitUtil.calculateProportionalSize(method, maxWidth, originalWidth, originalHeight)
    } else {
      InitUtil.calculateNewSize(method, maxWidth, originalWidth, originalHeight)
    }
  }
}
