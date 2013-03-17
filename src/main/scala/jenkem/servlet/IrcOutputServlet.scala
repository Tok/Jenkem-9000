package jenkem.servlet

import jenkem.persistence.data.ImageIrc
import javax.jdo.annotations.PersistenceCapable
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Servlet to retrieve and return stored IRC output.
 */
class IrcOutputServlet extends AbstractOutputServlet {
  override def doGet(request: HttpServletRequest, response: HttpServletResponse): Unit = {
    response.setCharacterEncoding(encoding)
    response.setContentType("text/plain")
    val imageIrc: ImageIrc = jenkemService.getImageIrcByName(obtainName(request))
    Option(imageIrc.irc) match {
      case Some(irc) => response.getWriter.write(irc)
      case None => response.getWriter.write("")
    }
  }
}
