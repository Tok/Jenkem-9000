/*
 * #%L
 * HtmlUtilSuite.scala - Jenkem - Tok - 2012
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

import org.junit.runner.RunWith
import org.scalatest.PrivateMethodTester.PrivateMethod
import org.scalatest.PrivateMethodTester.anyRefToInvoker
import jenkem.AbstractTester
import jenkem.engine.Method
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class HtmlUtilSuite extends AbstractTester {
  val name = "name"
  val nameIs = "name="
  val htmlType = ".html"
  val cssType = ".css"
  val txtType = ".txt"

  test("Test Empty") {
    assert(HtmlUtil.generateEmpty.startsWith(HtmlUtil.DOCTYPE))
  }

  test("Test HTML URL") {
    assert(HtmlUtil.getHtmlUrl(name).contains(name.toString))
    assert(HtmlUtil.getHtmlUrl(name).endsWith(htmlType))
  }

  test("Test CSS URL") {
    assert(HtmlUtil.getCssUrl(name).contains(name.toString))
    assert(HtmlUtil.getCssUrl(name).endsWith(cssType))
  }

  test("Test IRC URL") {
    assert(HtmlUtil.getIrcUrl(name).contains(name.toString))
    assert(HtmlUtil.getIrcUrl(name).endsWith(txtType))
  }

  test("Test Escape") {
    val escape = PrivateMethod[String]('escape)
    assert(HtmlUtil.invokePrivate(escape("&")) === "&amp;")
    assert(HtmlUtil.invokePrivate(escape("<")) === "&lt;")
    assert(HtmlUtil.invokePrivate(escape(">")) === "&gt;")
    assert(HtmlUtil.invokePrivate(escape(" ")) === "&nbsp;")
  }

  test("Test Host") {
    assert(HtmlUtil.HOST.startsWith("http://"))
  }

  test("New Line") {
    assert(HtmlUtil.SEP.length === 1)
  }

  test("Inline HTML") {
    val empty = HtmlUtil.generateEmpty + "\n<link href=\" \"></link>"
    val cssContent = ".white { background: #ffffff; }"
    val css = HtmlUtil.prepareCssForInline(cssContent)
    val result = HtmlUtil.prepareHtmlForInline(empty, css)
    assert(!result.contains("<div class=\"ircBinary\">"))
    assert(!result.contains("<div class=\"validator\">"))
    assert(!result.contains("<link href="))
  }

  test("Inline CSS") {
    val cssContent = ".white { background: #ffffff; }"
    val moreCss = cssContent + HtmlUtil.SEP + "div { }"
    val result = HtmlUtil.prepareCssForInline(moreCss)
    assert(result.startsWith("<style type=\"text/css\">"))
    assert(result.contains(cssContent))
    assert(result.endsWith("</style>"))
  }

  test("Generate Plain") {
    val generatePlain = PrivateMethod[Unit]('generatePlain)
    val html = new StringBuilder
    val css = new StringBuilder
    val line = "##"
    val lineId = 0
    HtmlUtil.invokePrivate(generatePlain(html, css, line, lineId))
    assert(html.toString === "<div class=\"jenkem\"><span id=\"id_0\">" + line + "</span></div>" + HtmlUtil.SEP)
    assert(css.toString === "#id_0 { color: #000000; background-color: #ffffff; }" + HtmlUtil.SEP)
  }

  test("Generate Colored") {
    val generateColored = PrivateMethod[Unit]('generateColored)
    val html = new StringBuilder
    val css = new StringBuilder
    val char = "#"
    val line = ColorUtil.CC + "1,0" + char
    val lineId = 0
    HtmlUtil.invokePrivate(generateColored(html, css, line, lineId))
    assert(html.toString.startsWith("<div class=\"jenkem\"><span id=\"id_0_0\">" + char + "</span></div>"))
    assert(css.toString === "#id_0_0 { color: #000000; background-color: #ffffff; }" + HtmlUtil.SEP)
  }

  test("Generate HTML") {
    val cc = ColorUtil.CC
    val ircOutputVort = List(
        cc + "1,0#" + cc + "0,1X",
        cc + "2,3x" + cc + "4,5-",
        cc + "6,7o" + cc + "8,9O")
    val nameVort = "vort"
    val methodVort = Method.Vortacular
    val (htmlVort, cssVort) = HtmlUtil.generateHtml(ircOutputVort, nameVort, methodVort)
    assert(htmlVort.contains(nameIs + nameVort + cssType))
    assert(htmlVort.contains(nameIs + nameVort + txtType))
    assert(htmlVort.contains("<title>" + nameVort + "</title>"))
    assert(htmlVort.contains(nameVort + cssType + "\" rel=\"stylesheet\" type=\"text/css\">"))
    assert(htmlVort.contains("ircBinary"))
    assert(htmlVort.contains("validator"))
    assert(htmlVort.contains("id_0_0"))
    assert(cssVort.contains("#id_0_0"))

    val ircOutputPlain = List("#X", "x-", ".o", "O@")
    val namePlain = "plain"
    val methodPlain = Method.Plain
    val (htmlPlain, cssPlain) = HtmlUtil.generateHtml(ircOutputPlain, namePlain, methodPlain)
    assert(htmlPlain.contains(nameIs + namePlain + cssType))
    assert(htmlPlain.contains(nameIs + namePlain + txtType))
    assert(htmlPlain.contains("<title>" + namePlain + "</title>"))
    assert(htmlPlain.contains(namePlain + cssType + "\" rel=\"stylesheet\" type=\"text/css\">"))
    assert(htmlPlain.contains("ircBinary"))
    assert(htmlPlain.contains("validator"))
    assert(htmlPlain.contains("id_0"))
    assert(cssPlain.contains("#id_0"))
  }
}
