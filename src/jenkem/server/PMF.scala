package jenkem.server

import javax.jdo.PersistenceManagerFactory
import javax.jdo.JDOHelper

/**
* Holds a single instance of the PersistenceManagerFactory.
*/
object PMF {
  final val pmf: PersistenceManagerFactory = JDOHelper.getPersistenceManagerFactory("transactions-optional")
  def get: PersistenceManagerFactory = pmf
}
