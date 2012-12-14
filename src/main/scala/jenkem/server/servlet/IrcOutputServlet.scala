package jenkem.server.servlet

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import jenkem.shared.data.ImageIrc

/**
 * Servlet to retrieve and return stored IRC output.
 */
@SerialVersionUID(-44444444444L)
class IrcOutputServlet extends AbstractOutputServlet {
  override def doGet(request: HttpServletRequest, response: HttpServletResponse): Unit = {
    response.setCharacterEncoding(encoding)
    response.setContentType("text/plain")
    val imageIrc: ImageIrc = jenkemService.getImageIrcByName(obtainName(request))
    if (imageIrc != null && imageIrc.getIrc != null) {
      response.getWriter.println(imageIrc.getIrc)
    }
  }
}
