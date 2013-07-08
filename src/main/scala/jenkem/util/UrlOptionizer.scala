/*
 * #%L
 * UrlOptionizer.scala - Jenkem - Tok - 2012
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
package jenkem.util

import java.net.MalformedURLException

object UrlOptionizer {
  object UrlExtractor {
    def unapply(u: java.net.URL): Some[(String, String, Int, String)] =
      Some((u.getProtocol, u.getHost, u.getPort, u.getPath))
  }
  def extract(link: String): Option[java.net.URL] = {
    try {
      val u = new java.net.URL(link)
      u match {
        case UrlExtractor(protocol, host, port, path) => Some(u)
      }
    } catch {
      case murle: MalformedURLException => None
    }
  }
}
