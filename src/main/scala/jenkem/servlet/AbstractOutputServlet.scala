/*
 * #%L
 * AbstractOutputServlet.scala - Jenkem - Tok - 2012
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
    extendedName.substring(0, extendedName.lastIndexOf('.'))
  }
}
