package jenkem.event

import java.util.EventObject

class CropsChangeEvent(val xs: Int, val xe: Int, val ys: Int, val ye: Int)
    extends EventObject {
}
