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
 * Abstract Servlet for output
 */
class AbstractOutputServlet extends HttpServlet {
  val jenkemService: JenkemServiceImpl = new JenkemServiceImpl
}
