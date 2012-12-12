package jenkem.server.servlet

import java.io.IOException

import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import jenkem.server.JenkemServiceImpl
import jenkem.shared.data.JenkemImageIrc

import scala.collection.JavaConversions._

/**
 * Servlet to retrieve and return stored IRC output.
 */
class IrcOutputServlet extends AbstractOutputServlet {
  override def doGet(request: HttpServletRequest, response: HttpServletResponse): Unit = {
    val imageIrc: JenkemImageIrc = jenkemService.getImageIrcByName(request.getParameter("name"))
    response.setCharacterEncoding("utf-8")
    response.setContentType("text/plain")
    if (imageIrc != null && imageIrc.getIrc != null) {
      response.getWriter.println(imageIrc.getIrc)
    }
  }
}
