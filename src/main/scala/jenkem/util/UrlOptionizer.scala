package jenkem.util

import java.net.MalformedURLException

object UrlOptionizer {
  object UrlExtractor {
    def unapply(u: java.net.URL) = Some((u.getProtocol, u.getHost, u.getPort, u.getPath))
  }
  def extract(link: String): Option[java.net.URL] = {
    try {
      val u = new java.net.URL(link)
      u match {
        case UrlExtractor(protocol, host, port, path) => Some(u)
      }
    } catch {
      case murle: MalformedURLException => None
    }
  }
}
