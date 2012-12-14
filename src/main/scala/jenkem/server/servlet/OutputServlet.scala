package jenkem.server.servlet

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import jenkem.server.JenkemServiceImpl
import jenkem.shared.HtmlUtil
import jenkem.shared.data.ImageHtml

/**
 * Servlet to retrieve and return stored HTML.
 */
@SerialVersionUID(-999999999L)
class OutputServlet extends AbstractOutputServlet {
  override def doGet(request: HttpServletRequest, response: HttpServletResponse): Unit = {
    response.setCharacterEncoding(encoding)
    response.setContentType("text/html")
    val imageHtml: ImageHtml = jenkemService.getImageHtmlByName(obtainName(request))
    if (imageHtml != null && imageHtml.getHtml != null) {
      response.getWriter.println(imageHtml.getHtml)
    } else {
      response.getWriter.println(HtmlUtil.generateEmpty)
    }
  }
}
