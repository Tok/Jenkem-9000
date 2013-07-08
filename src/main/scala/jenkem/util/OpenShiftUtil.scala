package jenkem.util

import java.util.NoSuchElementException

object OpenShiftUtil {
  def isOnOpenshift: Boolean =
    try { !Option(System.getenv("OPENSHIFT_APP_DNS")).get.isEmpty }
    catch { case nsee: NoSuchElementException => false }
}
