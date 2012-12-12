package jenkem.server

import com.google.gwt.user.server.rpc.RemoteServiceServlet

import javax.jdo.Transaction
import jenkem.client.service.JenkemService
import jenkem.shared.data.JenkemImageCss
import jenkem.shared.data.JenkemImageHtml
import jenkem.shared.data.JenkemImageInfo
import jenkem.shared.data.JenkemImageIrc

/**
 * Implementation of service to handle the persistence of reports.
 */
class JenkemServiceImpl extends RemoteServiceServlet with JenkemService {
  val QUERY_RANGE = 200L

  /**
   * Saves a converted JenkemImage.
   */
  override def saveJenkemImage(
        jenkemImageInfo: JenkemImageInfo,
        jenkemImageHtml: JenkemImageHtml,
        jenkemImageCss: JenkemImageCss,
        jenkemImageIrc: JenkemImageIrc) {
    val pm = PMF.get.getPersistenceManager
    val tx: Transaction = pm.currentTransaction
    try {
      tx.begin
      pm.makePersistent(jenkemImageInfo)
      pm.makePersistent(jenkemImageHtml)
      pm.makePersistent(jenkemImageCss)
      pm.makePersistent(jenkemImageIrc)
      tx.commit
    } finally {
      if (tx.isActive) { tx.rollback }
      pm.close
    }
  }

  /**
   * Returns the Info of the stored image corresponding to the provided name.
   * @param name
   * @return jenkemImageInfo
   */
  @deprecated("not used", "10.12.2012")
  def getImageInfoByName(name: String): JenkemImageInfo = {
    if (name != null) {
      val pm = PMF.get.getPersistenceManager
      try {
        val query = pm.newQuery(classOf[JenkemImageInfo])
        query.setFilter("name == n")
        query.setUnique(true)
        query.declareParameters("String n")
        val result = query.execute(name).asInstanceOf[JenkemImageInfo]
        result
      } finally {
        pm.close
      }
    } else { null.asInstanceOf[JenkemImageInfo] }
  }

  /**
   * Returns the HTML of the stored image corresponding to the provided name.
   * @param name
   * @return jenkemImageHtml
   */
  def getImageHtmlByName(name: String): JenkemImageHtml = {
    if (name != null) {
      val pm = PMF.get.getPersistenceManager
      try {
        val query = pm.newQuery(classOf[JenkemImageHtml])
        query.setFilter("name == n")
        query.setUnique(true)
        query.declareParameters("String n")
        val result = query.execute(name).asInstanceOf[JenkemImageHtml]
        result
      } finally {
        pm.close
      }
    } else { null.asInstanceOf[JenkemImageHtml] }
  }

  /**
   * Returns the CSS of the stored image corresponding to the provided name.
   * @param name
   * @return jenkemImageCss
   */
  def getImageCssByName(name: String): JenkemImageCss = {
    if (name != null) {
      val pm = PMF.get.getPersistenceManager
      try {
        val query = pm.newQuery(classOf[JenkemImageCss])
        query.setFilter("name == n")
        query.setUnique(true)
        query.declareParameters("String n")
        val result = query.execute(name).asInstanceOf[JenkemImageCss]
        result
      } finally {
        pm.close
      }
    } else { null.asInstanceOf[JenkemImageCss] }
  }

  /**
   * Returns the IRC representation of the stored image corresponding to the provided name.
   * @param name
   * @return jenkemImageIrc
   */
  def getImageIrcByName(name: String): JenkemImageIrc = {
    if (name != null) {
      val pm = PMF.get.getPersistenceManager
      try {
        val query = pm.newQuery(classOf[JenkemImageIrc])
        query.setFilter("name == n")
        query.setUnique(true)
        query.declareParameters("String n")
        val result = query.execute(name).asInstanceOf[JenkemImageIrc]
        result
      } finally {
        pm.close
      }
    } else { null.asInstanceOf[JenkemImageIrc] }
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
