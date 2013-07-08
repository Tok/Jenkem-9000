/*
 * #%L
 * HtmlOutputServlet.scala - Jenkem - Tok - 2012
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
    jenkemService.getImageHtmlByName(obtainName(request)) match {
      case Some(imageHtml) =>
        Option(imageHtml.html) match {
          case Some(html) => response.getWriter.write(html)
          case None => response.getWriter.write("Fail: HTML is empty.")
        }
      case None => response.getWriter.write("Fail: HTML couldn't be obtained.")
    }
  }
}
