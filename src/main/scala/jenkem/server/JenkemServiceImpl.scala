package jenkem.server

import com.google.gwt.user.server.rpc.RemoteServiceServlet
import javax.jdo.Transaction
import jenkem.client.service.JenkemService
import jenkem.shared.data.ImageCss
import jenkem.shared.data.ImageHtml
import jenkem.shared.data.ImageInfo
import jenkem.shared.data.JenkemImage
import jenkem.shared.data.ImageIrc

/**
 * Implementation of service to handle the persistence of reports.
 */
@SerialVersionUID(-3333333333333333333L)
class JenkemServiceImpl extends RemoteServiceServlet with JenkemService {
  val QUERY_RANGE = 200L

  /**
   * Saves a converted JenkemImage.
   */
  override def saveJenkemImage(jenkemImage: JenkemImage) {
    synchronized {
      val pm = PMF.get.getPersistenceManager
      val tx: Transaction = pm.currentTransaction
      try {
        val name = jenkemImage.getInfo.getName
        val exists: Boolean = getByName[ImageInfo](name, classOf[ImageInfo]) != null
        if (exists) {
          tx.begin
          JenkemImage.Part.values.map(part => pm.deletePersistent(pm.getObjectById(part.obtainClass, name)))
          tx.commit
        }
        tx.begin
        JenkemImage.Part.values.map(part => pm.makePersistent(jenkemImage.getComponents.get(part)))
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
  def getImageHtmlByName(name: String): ImageHtml = getByName[ImageHtml](name, classOf[ImageHtml])

  /**
   * Returns the CSS of the stored image corresponding to the provided name.
   * @param name
   * @return jenkemImageCss
   */
  def getImageCssByName(name: String): ImageCss = getByName[ImageCss](name, classOf[ImageCss])

  /**
   * Returns the IRC representation of the stored image corresponding to the provided name.
   * @param name
   * @return jenkemImageIrc
   */
  def getImageIrcByName(name: String): ImageIrc = getByName[ImageIrc](name, classOf[ImageIrc])

   /**
   * Returns the representation of the stored type corresponding to the provided name.
   * @param name
   * @param type
   * @return type
   */
  def getByName[T](name: String, c: java.lang.Class[T]): T = {
    if (name == null) { null.asInstanceOf[T] }
    else {
      val pm = PMF.get.getPersistenceManager
      try {
        val query = pm.newQuery(c)
        query.setUnique(true)
        query.setFilter("name == n")
        query.declareParameters("String n")
        val result = query.execute(name).asInstanceOf[T]
        result
      } finally {
        pm.close
      }
    }
  }

  /**
   * Returns an ArrayList with the information of all images in range.
   * @return infoList
   */
  override def getAllImageInfo(): java.util.ArrayList[ImageInfo] = {
    val infoList = new java.util.ArrayList[ImageInfo]
    val pm = PMF.get.getPersistenceManager
    try {
      val query = pm.newQuery(classOf[ImageInfo])
      query.setRange(0, QUERY_RANGE)
      query.setOrdering("creation desc")
      val tmp = query.execute().asInstanceOf[java.util.List[ImageInfo]]
      infoList.addAll(tmp)
      infoList
    } finally {
      pm.close
    }
  }
}
