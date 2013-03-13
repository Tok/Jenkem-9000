package jenkem.servlet

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import jenkem.persistence.PersistenceService

/**
 * Abstract Servlet for output
 */
abstract class AbstractOutputServlet extends HttpServlet {
  val encoding = "UTF-8"
  val jenkemService = PersistenceService
  def obtainName(request: HttpServletRequest): String = {
    val extendedName = request.getParameter("name")
    extendedName.substring(0, extendedName.lastIndexOf('.'));
  }
}
