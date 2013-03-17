package jenkem.persistence

import scala.Array.canBuildFrom
import javax.jdo.Transaction
import data.JenkemImage
import data.ImageInfo
import data.ImageHtml
import data.JenkemImage
import data.ImageIrc
import data.ImageCss


/**
 * Implementation of service to handle the persistence of reports.
 */
object PersistenceService {
  val QUERY_RANGE = 200L

  /**
   * Saves a converted JenkemImage.
   */
  def saveJenkemImage(jenkemImage: JenkemImage) {
    synchronized {
      val pm = PMF.get.getPersistenceManager
      val tx: Transaction = pm.currentTransaction
      try {
        val name = jenkemImage.info.name
        Option(getByName[ImageInfo](name, classOf[ImageInfo])) match {
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
      } finally {
        if (tx.isActive) { tx.rollback }
        pm.close
      }
    }
  }

  /**
   * Returns the HTML of the stored image corresponding to the provided name.
   * @param name
   * @return jenkemImageHtml
   */
  def getImageHtmlByName(name: String): Option[ImageHtml] = getByName[ImageHtml](name, classOf[ImageHtml])

  /**
   * Returns the CSS of the stored image corresponding to the provided name.
   * @param name
   * @return jenkemImageCss
   */
  def getImageCssByName(name: String): Option[ImageCss] = getByName[ImageCss](name, classOf[ImageCss])

  /**
   * Returns the IRC representation of the stored image corresponding to the provided name.
   * @param name
   * @return jenkemImageIrc
   */
  def getImageIrcByName(name: String): Option[ImageIrc] = getByName[ImageIrc](name, classOf[ImageIrc])

  /**
   * Returns the representation of the stored type corresponding to the provided name.
   * @param name
   * @param type
   * @return type
   */
  private def getByName[T](name: String, c: java.lang.Class[T]): Option[T] = {
    Option(name) match {
      case Some(name) =>
        val pm = PMF.get.getPersistenceManager
        try {
          val query = pm.newQuery(c)
          query.setUnique(true)
          query.setFilter("name == n")
          query.declareParameters("String n")
          val result = query.execute(name).asInstanceOf[T]
          Option(result)
        } finally {
          pm.close
        }
      case None => None
    }
  }

  /**
   * Returns an ArrayList with the information of all images in range.
   * @return infoList
   */
  def getAllImageInfo(): java.util.ArrayList[ImageInfo] = {
    val infoList = new java.util.ArrayList[ImageInfo]
    val pm = PMF.get.getPersistenceManager
    try {
      val query = pm.newQuery(classOf[ImageInfo])
      query.setRange(0, QUERY_RANGE)
      query.setOrdering("creation desc")
      val tmp = query.execute().asInstanceOf[java.util.List[ImageInfo]]
      infoList.addAll(tmp)
      infoList //TODO cache this!
    } finally {
      pm.close
    }
  }
}
