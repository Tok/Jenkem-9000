package jenkem.server.servlet

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import jenkem.server.JenkemServiceImpl

/**
 * Abstract Servlet for output
 */
@SerialVersionUID(1111111111111111111L)
class AbstractOutputServlet extends HttpServlet {
  val encoding = "UTF-8"
  val jenkemService: JenkemServiceImpl = new JenkemServiceImpl

  def obtainName(request: HttpServletRequest): String = {
    val extendedName = request.getParameter("name")
    extendedName.substring(0, extendedName.lastIndexOf('.'));
  }
}
