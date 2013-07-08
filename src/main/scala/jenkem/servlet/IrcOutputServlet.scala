/*
 * #%L
 * IrcOutputServlet.scala - Jenkem - Tok - 2012
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

import jenkem.persistence.data.ImageIrc
import javax.jdo.annotations.PersistenceCapable
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Servlet to retrieve and return stored IRC output.
 */
class IrcOutputServlet extends AbstractOutputServlet {
  override def doGet(request: HttpServletRequest, response: HttpServletResponse): Unit = {
    response.setCharacterEncoding(encoding)
    response.setContentType("text/plain")
    jenkemService.getImageIrcByName(obtainName(request)) match {
      case Some(imageIrc) =>
        Option(imageIrc.irc) match {
          case Some(irc) => response.getWriter.write(irc)
          case None => response.getWriter.write("Fail: IRC text is empty.")
        }
      case None => response.getWriter.write("Fail: IRC text couldn't be obtained.")
    }
  }
}
