package jenkem.servlet

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Servlet to retrieve and return stored CSS.
 */
@SerialVersionUID(-3333333333333333333L)
class CssOutputServlet extends AbstractOutputServlet {
  override def doGet(request: HttpServletRequest, response: HttpServletResponse): Unit = {
    response.setCharacterEncoding(encoding)
    response.setContentType("text/css")
    response.getWriter.println(jenkemService.getImageCssByName(obtainName(request)).getCss)
  }
}
