package jenkem.persistence

import org.datanucleus.api.jdo.JDOPersistenceManager
import org.datanucleus.api.jdo.JDOPersistenceManagerFactory
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import jenkem.persistence.data.ImageCss
import jenkem.persistence.data.ImageHtml
import jenkem.persistence.data.ImageInfo
import jenkem.persistence.data.ImageIrc
import jenkem.persistence.data.JenkemImage
import org.scalatest.junit.JUnitRunner
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.io.OutputStream
import org.scalatest.BeforeAndAfter

@RunWith(classOf[JUnitRunner])
class PersistenceSuite extends FunSuite with BeforeAndAfter {
  before {
    System.setErr(new PrintStream(new ByteArrayOutputStream))
  }

  after {
    System.setErr(System.err)
  }

  test("PMF Instance") {
    val pmf = PMF.get
    assert(pmf.isInstanceOf[JDOPersistenceManagerFactory])
    val pm = pmf.getPersistenceManager
    assert(pm.isInstanceOf[JDOPersistenceManager])
  }

  test("PMF Properties") {
    val connUrl = PMF.properties.getProperty("javax.jdo.option.ConnectionURL")
    assert(connUrl.equals("mongodb:/jenkem"))
    val pmfClass = PMF.properties.getProperty("javax.jdo.PersistenceManagerFactoryClass")
    assert(pmfClass.equals("org.datanucleus.api.jdo.JDOPersistenceManagerFactory"))
    val mapping = PMF.properties.getProperty("javax.jdo.option.Mapping")
    assert(mapping.equals("mongodb"))
  }

  test("Environment Variables") {
    assert(PMF.dbHost == None.orNull)
    assert(PMF.dbPort == None.orNull)
    assert(PMF.dbName == None.orNull)
    assert(PMF.dbUser == None.orNull)
    assert(PMF.dbPass == None.orNull)
  }

  test("PersistenceService Constants") {
    assert(PersistenceService.QUERY_RANGE == 200L)
  }

  val name = "name"
  val iinfo = new ImageInfo(name, "icon", "method", "characters", 1, 0, 10, 50, "date")
  val ihtml = new ImageHtml(name, "html")
  val icss = new ImageCss(name, "css")
  val iirc = new ImageIrc(name, "irc")
  val ji = new JenkemImage(iinfo, ihtml, icss, iirc)

  test("Get HTML By Name") {
    assert(PersistenceService.getImageHtmlByName(name) === None)
  }

  test("Get CSS By Name") {
    assert(PersistenceService.getImageCssByName(name) === None)
  }

  test("Get IRC By Name") {
    assert(PersistenceService.getImageIrcByName(name) === None)
  }

  test("Save Jenkem Image") {
    assert(!PersistenceService.saveJenkemImage(ji))
  }

  test("Get All Image Info") {
    assert(PersistenceService.getAllImageInfo === None)
  }
}
