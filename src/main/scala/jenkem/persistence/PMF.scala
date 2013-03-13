package jenkem.persistence

import java.util.Properties

import javax.jdo.JDOHelper
import javax.jdo.PersistenceManagerFactory

/**
 * Holds a single instance of the PersistenceManagerFactory.
 */
object PMF {
  final val dbHost = System.getenv("OPENSHIFT_MONGODB_DB_HOST")
  final val dbPort = System.getenv("OPENSHIFT_MONGODB_DB_PORT")
  final val dbName = System.getenv("OPENSHIFT_APP_NAME")
  final val dbUser = System.getenv("OPENSHIFT_MONGODB_DB_USERNAME")
  final val dbPass = System.getenv("OPENSHIFT_MONGODB_DB_PASSWORD")

  final val properties: Properties = new Properties
  //properties.setProperty("datanucleus.cache.level2.type", "xmemcached")
  properties.setProperty("javax.jdo.PersistenceManagerFactoryClass", "org.datanucleus.api.jdo.JDOPersistenceManagerFactory")
  properties.setProperty("javax.jdo.option.Mapping", "mongodb")

  Option(dbHost) match {
    case Some(v) if (!v.isEmpty) =>
      properties.setProperty("javax.jdo.option.ConnectionURL", "mongodb:" + v + ":" + dbPort + "/" + dbName)
      properties.setProperty("javax.jdo.option.ConnectionUserName", dbUser)
      properties.setProperty("javax.jdo.option.ConnectionPassword", dbPass)
    case None =>
      properties.setProperty("javax.jdo.option.ConnectionURL", "mongodb:/jenkem")
  }

  final val pmf: PersistenceManagerFactory = JDOHelper.getPersistenceManagerFactory(properties)
  def get: PersistenceManagerFactory = pmf
}
