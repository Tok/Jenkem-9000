/*
 * #%L
 * CssOutputServlet.scala - Jenkem - Tok - 2012
 * %%
 * Copyright (C) 2012 - 2013 Lukas Steiger <lsteiger4@gmail.com>
 * %%
 * This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar.
 * See http://www.wtfpl.net/ for more details.
 * #L%
 */
package jenkem.servlet

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Servlet to retrieve and return stored CSS.
 */
class CssOutputServlet extends AbstractOutputServlet {
  override def doGet(request: HttpServletRequest, response: HttpServletResponse): Unit = {
    response.setCharacterEncoding(encoding)
    response.setContentType("text/css")
    jenkemService.getImageCssByName(obtainName(request)) match {
      case Some(imageCss) => response.getWriter.write(imageCss.css)
      case None => response.getWriter.write("Fail: CSS couldn't be obtained.")
    }
  }
}
