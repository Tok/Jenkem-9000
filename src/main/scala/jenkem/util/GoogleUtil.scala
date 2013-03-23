package jenkem.util

import java.net.URL
import scala.io.Source
import scala.util.Random
import scala.io.Codec
import java.net.HttpURLConnection
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.MalformedInputException

object GoogleUtil {
  val searchUrl = "http://images.google.com/images?q="
  val attributes = "&num=255&start=0&safe=off"
  val encoding = "latin1" //prevents encoding errors (don't use UTF-8)
  val agent = "Jenkem/9000"

  def getUrlForTerm(term: String): Option[String] = {
    if (term.equals("")) { getUrlForTerm(makeRandomSearchTerm) }
    else {
      val imagePageUrl = new URL(searchUrl + term + attributes)
      val conn = imagePageUrl.openConnection.asInstanceOf[HttpURLConnection]
      conn.setConnectTimeout(5000)
      conn.setRequestProperty("user-agent", agent)
      try {
        conn.connect
        val page = Source.fromInputStream(conn.getInputStream, encoding).mkString
        val images = page.split("&amp;").filter(_.contains("imgurl=")).map(_.split("=").last)
        val imgUrls = images.filter(u => u.toUpperCase.endsWith(".JPG") || u.toUpperCase.endsWith(".PNG"))
        Some(Random.shuffle(imgUrls.toList).head)
      } catch {
        case pokemon: Throwable => None
      } finally {
        conn.disconnect
      }
    }
  }

  private def makeRandomSearchTerm: String = {
    val imageName = if(Random.nextBoolean) { "DSC0" } else { "IMG0" }
    val number = Random.nextDouble.toString
    imageName + number.take(4)
  }
}