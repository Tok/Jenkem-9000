package jenkem.server

import com.google.gwt.user.server.rpc.RemoteServiceServlet
import javax.jdo.Transaction
import jenkem.client.service.JenkemService
import jenkem.shared.data.JenkemImageCss
import jenkem.shared.data.JenkemImageHtml
import jenkem.shared.data.JenkemImageInfo
import jenkem.shared.data.JenkemImageIrc
import jenkem.shared.data.JenkemImage

/**
 * Implementation of service to handle the persistence of reports.
 */
class JenkemServiceImpl extends RemoteServiceServlet with JenkemService {
  val QUERY_RANGE = 200L

  /**
   * Saves a converted JenkemImage.
   */
  override def saveJenkemImage(jenkemImage: JenkemImage) {
    val pm = PMF.get.getPersistenceManager
    val tx: Transaction = pm.currentTransaction
    try {
      tx.begin
      pm.makePersistent(jenkemImage.getInfo)
      pm.makePersistent(jenkemImage.getHtml)
      pm.makePersistent(jenkemImage.getCss)
      pm.makePersistent(jenkemImage.getIrc)
      tx.commit
    } finally {
      if (tx.isActive) { tx.rollback }
      pm.close
    }
  }

  /**
   * Returns the HTML of the stored image corresponding to the provided name.
   * @param name
   * @return jenkemImageHtml
   */
  def getImageHtmlByName(name: String): JenkemImageHtml = {
    getByName[JenkemImageHtml](name, classOf[JenkemImageHtml])
  }

  /**
   * Returns the CSS of the stored image corresponding to the provided name.
   * @param name
   * @return jenkemImageCss
   */
  def getImageCssByName(name: String): JenkemImageCss = {
    getByName[JenkemImageCss](name, classOf[JenkemImageCss])
  }

  /**
   * Returns the IRC representation of the stored image corresponding to the provided name.
   * @param name
   * @return jenkemImageIrc
   */
  def getImageIrcByName(name: String): JenkemImageIrc = {
    getByName[JenkemImageIrc](name, classOf[JenkemImageIrc])
  }

   /**
   * Returns the representation of the stored type corresponding to the provided name.
   * @param name
   * @param type
   * @return type
   */
  def getByName[T](name: String, c: java.lang.Class[T]): T = {
    if (name != null) {
      val pm = PMF.get.getPersistenceManager
      try {
        val query = pm.newQuery(c)
        query.setFilter("name == n")
        query.setUnique(true)
        query.declareParameters("String n")
        val result = query.execute(name).asInstanceOf[T]
        result
      } finally {
        pm.close
      }
    } else { null.asInstanceOf[T] }
  }

  /**
   * Returns an ArrayList with the information of all images in range.
   * @return infoList
   */
  override def getAllImageInfo(): java.util.ArrayList[JenkemImageInfo] = {
    val infoList = new java.util.ArrayList[JenkemImageInfo]
    val pm = PMF.get.getPersistenceManager
    try {
      val query = pm.newQuery(classOf[JenkemImageInfo])
      query.setRange(0, QUERY_RANGE)
      query.setOrdering("creation desc")
      val tmp = query.execute().asInstanceOf[java.util.List[JenkemImageInfo]]
      infoList.addAll(tmp)
    } finally {
      pm.close
    }
    infoList
  }
}
