package jenkem.servlet

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Servlet to retrieve and return stored CSS.
 */
class CssOutputServlet extends AbstractOutputServlet {
  override def doGet(request: HttpServletRequest, response: HttpServletResponse): Unit = {
    response.setCharacterEncoding(encoding)
    response.setContentType("text/css")
    jenkemService.getImageCssByName(obtainName(request)) match {
      case Some(imageCss) => response.getWriter.write(imageCss.css)
      case None => response.getWriter.write("Fail: CSS couldn't be obtained.")
    }
  }
}
