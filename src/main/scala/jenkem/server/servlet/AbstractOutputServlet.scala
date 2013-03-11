package jenkem.server.servlet

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import jenkem.server.PersistenceService
import jenkem.server.PersistenceService

/**
 * Abstract Servlet for output
 */
@SerialVersionUID(1111111111111111111L)
abstract class AbstractOutputServlet extends HttpServlet {
  val encoding = "UTF-8"
  val jenkemService = PersistenceService

  def obtainName(request: HttpServletRequest): String = {
    val extendedName = request.getParameter("name")
    extendedName.substring(0, extendedName.lastIndexOf('.'));
  }
}
