package jenkem.server

import java.util.Properties
import javax.jdo.JDOHelper
import javax.jdo.PersistenceManagerFactory
import org.datanucleus.api.jdo.JDOPersistenceManagerFactory
import javax.jdo.PersistenceManager

/**
 * Holds a single instance of the PersistenceManagerFactory.
 */
object PMF {
  val dbHost = System.getenv("OPENSHIFT_MONGODB_DB_HOST");
  val dbPort = System.getenv("OPENSHIFT_MONGODB_DB_PORT");
  val dbName = System.getenv("OPENSHIFT_APP_NAME");
  val dbUser = System.getenv("OPENSHIFT_MONGODB_DB_USERNAME");
  val dbPass = System.getenv("OPENSHIFT_MONGODB_DB_PASSWORD");

  val properties: Properties = new Properties
  properties.setProperty("javax.jdo.PersistenceManagerFactoryClass", "org.datanucleus.api.jdo.JDOPersistenceManagerFactory");
  properties.setProperty("javax.jdo.option.Mapping", "mongodb");
  if (dbHost != null) { //configure for openshift
    properties.setProperty("javax.jdo.option.ConnectionURL", "mongodb:" + dbHost + ":" + dbPort + "/" + dbName);
    properties.setProperty("javax.jdo.option.ConnectionUserName", dbUser);
    properties.setProperty("javax.jdo.option.ConnectionPassword", dbPass);
  } else { //use local config
    properties.setProperty("javax.jdo.option.ConnectionURL", "mongodb:/jenkem" );
  }

  final val pmf: PersistenceManagerFactory = JDOHelper.getPersistenceManagerFactory(properties)
  def get: PersistenceManagerFactory = pmf
}
