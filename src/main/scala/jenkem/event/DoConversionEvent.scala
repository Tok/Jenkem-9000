package jenkem.event

import java.util.EventObject

class DoConversionEvent(val prepareImage: Boolean, val resize: Boolean)
  extends EventObject {
}
