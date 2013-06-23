package jenkem.util

object OpenShiftUtil {
  def isOnOpenshift: Boolean = {
    Option(System.getenv("OPENSHIFT_APP_DNS")) match {
      case Some(name) if (!name.isEmpty) => true
      case None => false
    }
  }
}
