package jenkem.server.servlet

import java.io.IOException

import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import jenkem.server.JenkemServiceImpl
import jenkem.shared.data.JenkemImageIrc

import com.google.appengine.api.datastore.Text

import scala.collection.JavaConversions._

/**
 * Servlet to retrieve and return stored IRC output.
 */
class IrcOutputServlet extends HttpServlet {
  val jenkemService: JenkemServiceImpl = new JenkemServiceImpl()

  override def doGet(request: HttpServletRequest, response: HttpServletResponse): Unit = {
    val name = request.getParameter("name")
    val imageIrc: JenkemImageIrc = jenkemService.getImageIrcByName(name)
    response.setCharacterEncoding("utf-8")
    response.setContentType("text/plain")
    if (imageIrc != null && imageIrc.getIrc() != null) {
      for (text: Text <- imageIrc.getIrc()) {
        response.getWriter().println(text.getValue());
      }
    }
  }
}