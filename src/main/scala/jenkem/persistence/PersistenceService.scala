package jenkem.persistence

import scala.collection.JavaConversions.asScalaBuffer
import data.ImageCss
import data.ImageHtml
import data.ImageInfo
import data.ImageIrc
import data.JenkemImage
import javax.jdo.Transaction
import javax.jdo.annotations.PersistenceCapable
import jenkem.persistence.data.ImageCss
import jenkem.persistence.data.ImageHtml
import jenkem.persistence.data.ImageInfo
import jenkem.persistence.data.ImageIrc
import com.mongodb.MongoException


/**
 * Implementation of service to handle the persistence of reports.
 */
object PersistenceService {
  val QUERY_RANGE = 200L

  def saveJenkemImage(jenkemImage: JenkemImage): Boolean = {
    synchronized {
      val pm = PMF.get.getPersistenceManager
      val tx: Transaction = pm.currentTransaction
      try {
        val name = jenkemImage.info.name
        getByName[ImageInfo](name, classOf[ImageInfo]) match {
          case Some(t) =>
            tx.begin
            jenkemImage.values.foreach(part => pm.deletePersistent(pm.getObjectById(part.c, name)))
            tx.commit
          case None => { }
        }
        tx.begin
        pm.makePersistent(jenkemImage.info)
        pm.makePersistent(jenkemImage.html)
        pm.makePersistent(jenkemImage.css)
        pm.makePersistent(jenkemImage.irc)
        tx.commit
        true
      } catch {
        case me: MongoException => false
      } finally {
        if (tx.isActive) { tx.rollback }
        pm.close
      }
    }
  }

  def getImageHtmlByName(name: String): Option[ImageHtml] = getByName[ImageHtml](name, classOf[ImageHtml])
  def getImageCssByName(name: String): Option[ImageCss] = getByName[ImageCss](name, classOf[ImageCss])
  def getImageIrcByName(name: String): Option[ImageIrc] = getByName[ImageIrc](name, classOf[ImageIrc])

  private def getByName[T](name: String, c: java.lang.Class[T]): Option[T] = {
    Option(name) match {
      case Some(name) =>
        val pm = PMF.get.getPersistenceManager
        try {
          val query = pm.newQuery(c)
          query.setUnique(true)
          query.setFilter("name == n")
          query.declareParameters("String n")
          val result: T = query.execute(name).asInstanceOf[T]
          if (result != None.orNull) { Some(result) } else { None }
        } finally {
          pm.close
        }
      case None => None
    }
  }

  def getAllImageInfo: Option[List[ImageInfo]] = {
    val pm = PMF.get.getPersistenceManager
    try {
      val query = pm.newQuery(classOf[ImageInfo])
      query.setRange(0, QUERY_RANGE)
      query.setOrdering("creation desc")
      val result = query.execute.asInstanceOf[java.util.List[ImageInfo]]
      Some(result.toList)
    } catch {
      case me: MongoException => None
    } finally {
      pm.close
    }
  }
}
