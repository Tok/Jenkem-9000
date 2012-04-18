package jenkem.server

import com.google.gwt.user.server.rpc.RemoteServiceServlet
import java.util.ArrayList
import java.util.List
import java.util.logging.Level
import java.util.logging.Logger
import jenkem.client.service.JenkemService
import jenkem.shared.data.JenkemImageCss
import jenkem.shared.data.JenkemImageHtml
import jenkem.shared.data.JenkemImageInfo
import jenkem.shared.data.JenkemImageIrc
import javax.jdo.Extent
import javax.jdo.JDOHelper
import javax.jdo.PersistenceManager
import javax.jdo.PersistenceManagerFactory
import javax.jdo.Query

/**
 * Implementation of service to handle the persistence of reports.
 */
class JenkemServiceImpl extends RemoteServiceServlet with JenkemService {
  val LOG = Logger.getLogger(classOf[JenkemServiceImpl].getName())
  val QUERY_RANGE = 200L

  /**
   * Saves a converted JenkemImage.
   */
  override def saveJenkemImage(
    jenkemImageInfo: JenkemImageInfo,
    jenkemImageHtml: JenkemImageHtml,
    jenkemImageCss: JenkemImageCss,
    jenkemImageIrc: JenkemImageIrc): String = {
    val pm = PMF.get().getPersistenceManager()
    try {
      pm.makePersistent(jenkemImageInfo)
      pm.makePersistent(jenkemImageHtml)
      pm.makePersistent(jenkemImageCss)
      pm.makePersistent(jenkemImageIrc)
      LOG.log(Level.INFO, "Image stored!")
    } finally {
      pm.close()
    }
    return "%d".format(jenkemImageInfo.getCreateDate().getTime())
  }

  /**
   * Returns the HTML of the stored image corresponding to the provided name.
   * @param name
   * @return jenkemImageHtml
   */
  def getImageHtmlByName(name: String): JenkemImageHtml = {
    var jenkemImageHtml: JenkemImageHtml = null.asInstanceOf[JenkemImageHtml]
    if (name != null) {
      val pm = PMF.get().getPersistenceManager()
      try {
        val query = pm.newQuery(classOf[JenkemImageHtml])
        query.setFilter("name == n")
        query.setUnique(true)
        query.declareParameters("String n")
        jenkemImageHtml = query.execute(name).asInstanceOf[JenkemImageHtml]
      } finally {
        pm.close()
      }
    }
    return jenkemImageHtml
  }

  /**
   * Returns the CSS of the stored image corresponding to the provided name.
   * @param name
   * @return jenkemImageCss
   */
  def getImageCssByName(name: String): JenkemImageCss = {
    var jenkemImageCss: JenkemImageCss = null.asInstanceOf[JenkemImageCss]
    if (name != null) {
      val pm = PMF.get().getPersistenceManager()
      try {
        val query = pm.newQuery(classOf[JenkemImageCss])
        query.setFilter("name == n")
        query.setUnique(true)
        query.declareParameters("String n")
        jenkemImageCss = query.execute(name).asInstanceOf[JenkemImageCss]
      } finally {
        pm.close()
      }
    }
    return jenkemImageCss
  }

  /**
   * Returns the IRC representation of the stored image corresponding to the provided name.
   * @param name
   * @return jenkemImageIrc
   */
  def getImageIrcByName(name: String): JenkemImageIrc = {
    var jenkemImageIrc: JenkemImageIrc = null.asInstanceOf[JenkemImageIrc]
    if (name != null) {
      val pm = PMF.get().getPersistenceManager()
      try {
        val query = pm.newQuery(classOf[JenkemImageIrc])
        query.setFilter("name == n")
        query.setUnique(true)
        query.declareParameters("String n")
        jenkemImageIrc = query.execute(name).asInstanceOf[JenkemImageIrc]
      } finally {
        pm.close()
      }
    }
    return jenkemImageIrc
  }

  /**
   * Returns an ArrayList with the information of all images in range.
   * @return infoList
   */
  override def getAllImageInfo(): java.util.ArrayList[JenkemImageInfo] = {
    val infoList = new java.util.ArrayList[JenkemImageInfo]()
    val pm = PMF.get().getPersistenceManager()
    try {
      var query = pm.newQuery(classOf[JenkemImageInfo])
      query.setRange(0, QUERY_RANGE)
      query.setOrdering("createDate desc")
      val tmp = query.execute().asInstanceOf[java.util.List[JenkemImageInfo]]
      infoList.addAll(tmp)
    } finally {
      pm.close()
    }
    return infoList
  }

}