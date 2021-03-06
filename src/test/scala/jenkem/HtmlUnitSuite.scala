/*
 * #%L
 * HtmlUnitSuite.scala - Jenkem - Tok - 2012
 * %%
 * Copyright (C) 2012 - 2013 Lukas Steiger
 *                 <lsteiger4@gmail.com>
 * %%
 * This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar.
 * See http://www.wtfpl.net/ for more details.
 * #L%
 */
package jenkem

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.HtmlPage
import org.scalatest.FunSuite
import org.scalatest.BeforeAndAfter
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.InputStream
import java.io.File
import com.gargoylesoftware.htmlunit.JavaScriptPage
import com.gargoylesoftware.htmlunit.TextPage
import org.scalatest.Ignore

@Ignore
@RunWith(classOf[JUnitRunner])
class HtmlUnitSuite extends FunSuite with BeforeAndAfter {
  val localhost = "http://localhost:8080/"
  val dir = new java.io.File(".").getCanonicalPath
  val execDir = dir + "/deployments/standalone/jenkem/"
  var proc: Process = None.orNull

  before {
    val r = Runtime.getRuntime
    proc = r.exec("java -jar " + execDir + "Jenkem-1.0-war-exec.jar")
    val in: InputStream = proc.getInputStream
    val err: InputStream = proc.getErrorStream
  }

  after {
    proc.destroy
  }

  test("Default") {
    val wc = new WebClient
    val htmlPage = wc.getPage[HtmlPage](localhost)
    val head = htmlPage.getDocumentElement.getHtmlElementsByTagName("head")
    val body = htmlPage.getBody
    assert(!body.getHtmlElementsByTagName("noscript").isEmpty)
    wc.closeAllWindows
  }
}
