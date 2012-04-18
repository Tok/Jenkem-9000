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
class CssOutputServlet extends HttpServlet {
  val jenkemService: JenkemServiceImpl = new JenkemServiceImpl()

  override def doGet(request: HttpServletRequest, response: HttpServletResponse): Unit = {
    val name = request.getParameter("name")
    val imageCss: JenkemImageCss = jenkemService.getImageCssByName(name)
    response.setCharacterEncoding("utf-8")
    response.setContentType("text/css")
    response.getWriter().println(imageCss.getCss())
  }
}