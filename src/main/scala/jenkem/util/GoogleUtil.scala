/*
 * #%L
 * GoogleUtil.scala - Jenkem - Tok - 2012
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

import java.net.URL
import scala.io.Source
import scala.util.Random
import scala.io.Codec
import java.net.HttpURLConnection
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.MalformedInputException

object GoogleUtil {
  val searchUrl = "http://images.google.com/images?q="
  val attributes = "&num=255&start=0&safe=off"
  val encoding = "latin1" //prevents encoding errors (don't use UTF-8)
  val agent = "Jenkem/9000"

  def getUrlForTerm(term: String): Option[String] = {
    if (term.equals("")) { getUrlForTerm(makeRandomSearchTerm) }
    else {
      val imagePageUrl = new URL(searchUrl + term + attributes)
      val conn = imagePageUrl.openConnection.asInstanceOf[HttpURLConnection]
      conn.setConnectTimeout(5000)
      conn.setRequestProperty("user-agent", agent)
      try {
        conn.connect
        val page = Source.fromInputStream(conn.getInputStream, encoding).mkString
        //getting images from google cache at gstatic.con instead of original
        val images = page.split("src=\"").filter(_.startsWith("http://")).filter(_.contains("gstatic.com"))
        val imgUrls = images.map(_.split("\"")(0))
        Some(Random.shuffle(imgUrls.toList).head)
      } catch {
        case pokemon: Throwable => None
      } finally {
        conn.disconnect
      }
    }
  }

  private def makeRandomSearchTerm: String = {
    val imageName = if(Random.nextBoolean) { "DSC0" } else { "IMG0" }
    val number = Random.nextDouble * 10
    imageName + number.toString.take(5).filterNot(".".contains(_))
  }
}
