package jenkem.servlet

import jenkem.persistence.data.ImageHtml
import javax.jdo.annotations.PersistenceCapable
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import jenkem.util.HtmlUtil

/**
 * Servlet to retrieve and return stored HTML.
 */
class HtmlOutputServlet extends AbstractOutputServlet {
  override def doGet(request: HttpServletRequest, response: HttpServletResponse): Unit = {
    response.setCharacterEncoding(encoding)
    response.setContentType("text/html")
    val imageHtml: ImageHtml = jenkemService.getImageHtmlByName(obtainName(request))
    Option(imageHtml.html) match {
      case Some(html) => response.getWriter.write(html)
      case None => response.getWriter.write(HtmlUtil.generateEmpty)
    }
  }
}
