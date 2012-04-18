package jenkem.server.servlet

import java.io.IOException

import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import jenkem.server.JenkemServiceImpl
import jenkem.shared.HtmlUtil
import jenkem.shared.data.JenkemImageHtml

/**
 * Servlet to retrieve and return stored HTML.
 */
class OutputServlet extends HttpServlet {
  val jenkemService: JenkemServiceImpl = new JenkemServiceImpl()
  val htmlUtil = new HtmlUtil()

  override def doGet(request: HttpServletRequest, response: HttpServletResponse): Unit = {
    val name = request.getParameter("name")
    val imageHtml: JenkemImageHtml = jenkemService.getImageHtmlByName(name)
    if (imageHtml != null && imageHtml.getHtml() != null) {
      response.getWriter().println(imageHtml.getHtml())
    } else {
      response.getWriter().println(htmlUtil.generateEmpty())
    }
    response.setCharacterEncoding("utf-8")
    response.setContentType("text/html")
  }
}