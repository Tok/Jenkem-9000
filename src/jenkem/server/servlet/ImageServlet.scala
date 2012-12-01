package jenkem.server.servlet

import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.URL

import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Takes an URL parameter to an image and proxifies it back, so the client can
 * locally call this servlet in order to circumvent the same origin policies
 * set by the browser.
 */
class ImageServlet extends HttpServlet {

  override def doGet(request: HttpServletRequest, response: HttpServletResponse): Unit = {
    response.setContentType("image/jpeg")
    val urlString = request.getParameter("url")
    val url = new URL(urlString)
    val in: InputStream = url.openStream
    val out: OutputStream = response.getOutputStream
    Iterator.continually(in.read).takeWhile(-1 !=).foreach(out.write)
    in.close
    out.close
  }
}
