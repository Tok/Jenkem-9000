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
import com.google.appengine.api.memcache.MemcacheServiceFactory

/**
 * Implementation of service to handle the persistence of reports.
 */
class JenkemServiceImpl extends RemoteServiceServlet with JenkemService {
  val CACHE = MemcacheServiceFactory.getMemcacheService
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
    val pm = PMF.get.getPersistenceManager
    try {
      pm.makePersistent(jenkemImageInfo)
      pm.makePersistent(jenkemImageHtml)
      pm.makePersistent(jenkemImageCss)
      pm.makePersistent(jenkemImageIrc)
      LOG.log(Level.INFO, "Image stored!")
    } finally {
      pm.close
    }
    CACHE.clearAll
    "%d".format(jenkemImageInfo.getCreateDate().getTime())
  }

  /**
   * Returns the HTML of the stored image corresponding to the provided name.
   * @param name
   * @return jenkemImageHtml
   */
  def getImageHtmlByName(name: String): JenkemImageHtml = {
    val key = "HTML:" + name
    val cached = CACHE.get(key).asInstanceOf[JenkemImageHtml]
    if (cached != null) { cached
    } else {
      if (name != null) {
        val pm = PMF.get.getPersistenceManager
        try {
          val query = pm.newQuery(classOf[JenkemImageHtml])
          query.setFilter("name == n")
          query.setUnique(true)
          query.declareParameters("String n")
          val result = query.execute(name).asInstanceOf[JenkemImageHtml]
          CACHE.put(key, result)
          result
        } finally {
          pm.close
        }
      } else { null.asInstanceOf[JenkemImageHtml] }
    }
  }

  /**
   * Returns the CSS of the stored image corresponding to the provided name.
   * @param name
   * @return jenkemImageCss
   */
  def getImageCssByName(name: String): JenkemImageCss = {
    val key = "CSS:" + name
    val cached = CACHE.get(key).asInstanceOf[JenkemImageCss]
    if (cached != null) { cached
    } else {
      if (name != null) {
        val pm = PMF.get.getPersistenceManager
        try {
          val query = pm.newQuery(classOf[JenkemImageCss])
          query.setFilter("name == n")
          query.setUnique(true)
          query.declareParameters("String n")
          val result = query.execute(name).asInstanceOf[JenkemImageCss]
          CACHE.put(key, result)
          result
        } finally {
          pm.close
        }
      } else { null.asInstanceOf[JenkemImageCss] }
    }
  }

  /**
   * Returns the IRC representation of the stored image corresponding to the provided name.
   * @param name
   * @return jenkemImageIrc
   */
  def getImageIrcByName(name: String): JenkemImageIrc = {
    val key = "IRC:" + name
    val cached = CACHE.get(key).asInstanceOf[JenkemImageIrc]
    if (cached != null) { cached
    } else {
      if (name != null) {
        val pm = PMF.get.getPersistenceManager
        try {
          val query = pm.newQuery(classOf[JenkemImageIrc])
          query.setFilter("name == n")
          query.setUnique(true)
          query.declareParameters("String n")
          val result = query.execute(name).asInstanceOf[JenkemImageIrc]
          CACHE.put(key, result)
          result
        } finally {
          pm.close
        }
      } else { null.asInstanceOf[JenkemImageIrc] }
    }
  }

  /**
   * Returns an ArrayList with the information of all images in range.
   * @return infoList
   */
  override def getAllImageInfo(): java.util.ArrayList[JenkemImageInfo] = {
    val key = "INFO"
    val cached = CACHE.get(key).asInstanceOf[java.util.ArrayList[JenkemImageInfo]]
    if (cached != null) { cached
    } else {
      val infoList = new java.util.ArrayList[JenkemImageInfo]
      val pm = PMF.get.getPersistenceManager
      try {
        val query = pm.newQuery(classOf[JenkemImageInfo])
        query.setRange(0, QUERY_RANGE)
        query.setOrdering("createDate desc")
        val tmp = query.execute().asInstanceOf[java.util.List[JenkemImageInfo]]
        infoList.addAll(tmp)
      } finally {
        pm.close
      }
      CACHE.put(key, infoList)
      infoList
    }
  }

}
