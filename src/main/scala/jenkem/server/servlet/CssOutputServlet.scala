package jenkem.server.servlet

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import jenkem.server.JenkemServiceImpl

/**
 * Servlet to retrieve and return stored CSS.
 */
class CssOutputServlet extends AbstractOutputServlet {
  override def doGet(request: HttpServletRequest, response: HttpServletResponse): Unit = {
    response.setCharacterEncoding(encoding)
    response.setContentType("text/css")
    response.getWriter.println(jenkemService.getImageCssByName(obtainName(request)).getCss)
  }
}
