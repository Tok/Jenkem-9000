package jenkem.persistence

import org.datanucleus.api.jdo.JDOPersistenceManager
import org.datanucleus.api.jdo.JDOPersistenceManagerFactory
import org.junit.runner.RunWith
import javax.jdo.spi.PersistenceCapable
import jenkem.AbstractTester
import jenkem.persistence.data.ImageCss
import jenkem.persistence.data.ImageHtml
import jenkem.persistence.data.ImageInfo
import jenkem.persistence.data.ImageIrc
import jenkem.persistence.data.JenkemImage
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class PersistenceSuite extends AbstractTester {
  test("PMF Instance") {
    val pmf = PMF.get
    assert(pmf.isInstanceOf[JDOPersistenceManagerFactory])
    val pm = pmf.getPersistenceManager
    assert(pm.isInstanceOf[JDOPersistenceManager])
  }

  test("PMF Properties") {
    val connUrl = PMF.properties.getProperty("javax.jdo.option.ConnectionURL")
    assert(connUrl === "mongodb:/jenkem")
    val pmfClass = PMF.properties.getProperty("javax.jdo.PersistenceManagerFactoryClass")
    assert(pmfClass === "org.datanucleus.api.jdo.JDOPersistenceManagerFactory")
    val mapping = PMF.properties.getProperty("javax.jdo.option.Mapping")
    assert(mapping === "mongodb")
  }

  test("Environment Variables") {
    assert(PMF.dbHost === None.orNull)
    assert(PMF.dbPort === None.orNull)
    assert(PMF.dbName === None.orNull)
    assert(PMF.dbUser === None.orNull)
    assert(PMF.dbPass === None.orNull)
  }

  test("PersistenceService Constants") {
    assert(PersistenceService.QUERY_RANGE === 200L)
  }

  val nameString = "name"
  val ircString = "irc"
  val htmlString = "html"
  val cssString = "css"
  val iconString = "icon"
  val methodString = "method"
  val charactersString = "characters"
  val contrast: Short = 1
  val brightness: Short = 0
  val lines: Short = 10
  val lineWidth: Short = 50
  val dateString = "date"
  val iinfo = new ImageInfo(nameString, iconString, methodString, charactersString, contrast, brightness, lines, lineWidth, dateString)
  val ihtml = new ImageHtml(nameString, htmlString)
  val icss = new ImageCss(nameString, cssString)
  val iirc = new ImageIrc(nameString, ircString)
  val ji = new JenkemImage(iinfo, ihtml, icss, iirc)

  test("Get HTML By Name") {
    assert(PersistenceService.getImageHtmlByName(nameString) === None)
  }

  test("Get CSS By Name") {
    assert(PersistenceService.getImageCssByName(nameString) === None)
  }

  test("Get IRC By Name") {
    assert(PersistenceService.getImageIrcByName(nameString) === None)
  }

  test("Save Jenkem Image") {
    assert(!PersistenceService.saveJenkemImage(ji))
  }

  test("Get All Image Info") {
    assert(PersistenceService.getAllImageInfo === None)
  }

  test("Image IRC") {
    assert(iirc._id === nameString)
    assert(iirc.toString === nameString)
    assert(iirc.name === nameString)
    assert(iirc.irc === ircString)
    val pc = iirc.asInstanceOf[PersistenceCapable]
    testPc(pc)
  }

  test("Image HTML") {
    assert(ihtml._id === nameString)
    assert(ihtml.toString === nameString)
    assert(ihtml.name === nameString)
    assert(ihtml.html === htmlString)
    val pc = ihtml.asInstanceOf[PersistenceCapable]
    testPc(pc)
  }

  test("Image CSS") {
    assert(icss._id === nameString)
    assert(icss.toString === nameString)
    assert(icss.name === nameString)
    assert(icss.css === cssString)
    val pc = icss.asInstanceOf[PersistenceCapable]
    testPc(pc)
  }

  test("Image Info") {
    assert(iinfo._id === nameString)
    assert(iinfo.toString === nameString)
    assert(iinfo.name === nameString)
    assert(iinfo.icon === iconString)
    assert(iinfo.method === methodString)
    assert(iinfo.contrast === contrast)
    assert(iinfo.brightness === brightness)
    assert(iinfo.lines === lines)
    assert(iinfo.lineWidth === lineWidth)
    assert(iinfo.creation === dateString)
    val pc = iinfo.asInstanceOf[PersistenceCapable]
    testPc(pc)
  }

  test("Jenkem Image") {
    assert(ji.html.toString === nameString)
    assert(ji.css.toString === nameString)
    assert(ji.irc.toString === nameString)
    assert(ji.info.toString === nameString)
    assert(ji.values.contains(ji.HTML))
    assert(ji.values.contains(ji.CSS))
    assert(ji.values.contains(ji.IRC))
    assert(ji.values.contains(ji.INFO))
    testPart(ji.HTML, "html")
    testPart(ji.CSS, "css")
    testPart(ji.IRC, "irc")
    testPart(ji.INFO, "info")
  }

  private def testPart(part: ji.Part, name: String): Unit = {
    assert(part !== None.orNull)
    assert(part.toString.equalsIgnoreCase(name))
  }

  private def testPc(pc: PersistenceCapable): Unit = {
    assert(!pc.jdoIsDeleted)
    assert(!pc.jdoIsDetached)
    assert(!pc.jdoIsDirty)
    assert(!pc.jdoIsNew)
    assert(!pc.jdoIsPersistent)
    assert(!pc.jdoIsTransactional)
    assert(pc.jdoGetObjectId === None.orNull)
    assert(pc.jdoGetPersistenceManager === None.orNull)
    assert(pc.jdoGetTransactionalObjectId === None.orNull)
    assert(pc.jdoGetVersion === None.orNull)
    pc.jdoMakeDirty(nameString)
    assert(!pc.jdoIsDirty) //still not dirty
    assert(pc.jdoNewObjectIdInstance.toString === nameString)
    pc.jdoReplaceFlags
  }
}
