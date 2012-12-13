package jenkem.server.servlet

import java.io.IOException

import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import jenkem.server.JenkemServiceImpl
import jenkem.shared.data.JenkemImageCss

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