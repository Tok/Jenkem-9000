package jenkem.engine

object ProcSettings {
  sealed abstract class Setting(val caption: String)
  case object UPDOWN extends Setting("\"_")
  case object LEFTRIGHT extends Setting("[]")
  case object DBQP extends Setting("dbqp")
  case object DIAGONAL extends Setting("\\ /")
  case object VERTICAL extends Setting("|")
  case object HORIZONTAL extends Setting("-")
  val settings = List(UPDOWN, LEFTRIGHT, DBQP, DIAGONAL, VERTICAL, HORIZONTAL)

  val min = -100
  val max = 100
  val default = 0

  type Pair = (Boolean, Int)
  class Instance(val ud: Pair, val lr: Pair, val dbqp: Boolean, val diag: Boolean, val vert: Boolean, val hor: Boolean) {
    def has(setting: Setting): Boolean = {
      if (setting.equals(DBQP)) { dbqp }
      else if (setting.equals(DIAGONAL)) { diag }
      else if (setting.equals(VERTICAL)) { vert }
      else if (setting.equals(HORIZONTAL)) { hor }
      else { getPair(setting)._1 }
    }
    def get(setting: Setting): Int = getPair(setting)._2
    private def getPair(setting: Setting): Pair = {
      setting match {
        case UPDOWN => ud
        case LEFTRIGHT => lr
        case DBQP | DIAGONAL | VERTICAL | HORIZONTAL => (false, default)
      }
    }
  }

  def getInitial(hasAnsi: Boolean): Instance = {
    new Instance((true, default), (true, default), (!hasAnsi), (!hasAnsi), (false), (false))
  }
}
