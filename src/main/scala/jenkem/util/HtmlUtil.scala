package jenkem.util

import scala.Array.canBuildFrom

import jenkem.engine.Method

object HtmlUtil {
  val SEP = "\n"
  val CLOSE = "\">"
  val HOST = "http://jenkem-9000.rhcloud.com"
  val CHARSET = "UTF-8"
  val DOCTYPE = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\"\n    \"http://www.w3.org/TR/html4/strict.dtd\">"
  val META = "<meta http-equiv=\"content-type\" content=\"text/html; charset=" + CHARSET + CLOSE

  def generateEmpty: String = DOCTYPE + "\n<html>\n<head>\n" + META + "\n<title></title>\n</head>\n" + "<body class=\"jenkemBody\"></body>\n</html>\n"

  def generateHtml(ircOutput: List[String], name: String, method: Method): (String, String) = {
    val html = new StringBuilder
    val css = new StringBuilder
    html.append(DOCTYPE)
    html.append("\n<html>\n<head>\n")
    html.append(META)
    html.append("\n<title>")
    html.append(name)
    html.append("</title>\n<link href=\"")
    html.append(getCssUrl(name))
    html.append("\" rel=\"stylesheet\" type=\"text/css\">\n</head>\n<body class=\"jenkemBody\">\n<div>\n")
    css.append("form { margin: 0 auto; padding: 0; }\n")
    css.append("html { background-color: #ffffff }\n")
    css.append("body { font-family: monospace; font-size: 1em; font-weight: bold; margin: 0; background-color: black; }\n")
    css.append("div { float: left; width: auto; clear: both; }\n")
    css.append("span { float: left; width: auto; }\n")
    val lines = ircOutput.filterNot(_.equals(""))
    if (!method.equals(Method.Vortacular)) {
      (0 until lines.length).foreach(i => generatePlain(html, css, lines(i), i))
    } else { (0 until lines.length).foreach(i => generateColored(html, css, lines(i), i)) }
    html.append("</div>\n<div class=\"ircBinary\"><a href=\"")
    html.append(getIrcUrl(name))
    html.append("\" onclick=\"this.target='blank'\">Download binary textfile for IRC</a></div>\n")
    html.append("<div class=\"validator\"><a href=\"http://validator.w3.org/check?uri=")
    html.append(HOST)
    html.append(getHtmlUrl(name))
    html.append("\"><img src=\"/VAADIN/images/valid-html.png\" alt=\"Valid HTML 4.01 Strict\" style=\"border: 0; width: 88px; height: 31px\"></a>")
    if (method.equals(Method.Vortacular)) {
      html.append("<a href=\"http://jigsaw.w3.org/css-validator/validator?uri=")
      html.append(HOST)
      html.append(getCssUrl(name))
      html.append("&amp;profile=css3\"><img src=\"/VAADIN/images/valid-css.png\" alt=\"CSS is valid!\" style=\"border: 0; width: 88px; height: 31px\"></a>")
    }
    html.append("</div>\n</body>\n</html>")
    (html.toString, css.toString)
  }

  def getCssUrl(name: String): String = "/jenkem/cssOutput?name=" + name + ".css"
  def getIrcUrl(name: String): String = "/jenkem/irc?name=" + name + ".txt"
  def getHtmlUrl(name: String): String = "/jenkem/output?name=" + name + ".html"

  def prepareHtmlForInline(inputHtml: String, inputCss: String): String = {
    val filtered = inputHtml.split(SEP).toList
      .filterNot(_.startsWith("<div class=\"ircBinary\">"))
      .filterNot(_.startsWith("<div class=\"validator\">"))
    val result = filtered.map(l => if (l.startsWith("<link href=")) { inputCss } else { l })
    result.map(l => l + SEP).mkString
  }

  def prepareCssForInline(inputCss: String): String = {
    val rep = ".jenkem { clear: both; font-family: monospace; font-weight: bold; font-size: 10px; line-height: 12px; }"
    val filtered = inputCss.split(SEP).toList.filterNot(_.startsWith("form {"))
      .filterNot(_.startsWith("body {")).filterNot(_.startsWith("html {"))
    val result = filtered.map(l => if (l.startsWith("div {")) { rep } else { l })
    "<style type=\"text/css\">\n" + result.map(l => l + SEP).mkString + "\n</style>"
  }

  private def generatePlain(html: StringBuilder, css: StringBuilder,
      line: String, lineId: Int): Unit = {
    html.append("<div class=\"jenkem\"><span id=\"id_")
    html.append(lineId.toString)
    html.append(CLOSE)
    html.append(escape(line))
    html.append("</span></div>\n")
    css.append("#id_")
    css.append(lineId.toString)
    css.append(" { color: #000000; background-color: #ffffff; }\n")
  }

  private def generateColored(html: StringBuilder, css: StringBuilder,
      line: String, lineId: Int): Unit = {
    html.append("<div class=\"jenkem\">")
    val sections = line.split(ColorUtil.CC)
    val filteredSections = sections.filterNot(_.equals("")).filterNot(_.equals(ColorUtil.BC))
    val parts = filteredSections.map(_.split(",").toList)
    val fg = parts.map(_(0))
    val bgAndChars = parts.map(_(1))
    val bg = bgAndChars.map(_.replaceAll("[^0-9]", "")) //remove nonnumeric
    val chars = bgAndChars.map(_.replaceAll("[0-9]", "")) //remove numeric
    for { id <- 0 until bgAndChars.length } yield {
      html.append("<span id=\"id_")
      html.append(lineId)
      html.append("_")
      html.append(id)
      html.append(CLOSE)
      html.append(escape(chars(id)))
      html.append("</span>")
      css.append("#id_")
      css.append(lineId)
      css.append("_")
      css.append(id)
      css.append(" { color: ")
      css.append(ColorUtil.ircToCss(fg(id))) //fg
      css.append("; background-color: ")
      css.append(ColorUtil.ircToCss(bg(id)))
      css.append("; }\n")
    }
    html.append("</div>\n")
  }

  private def escape(input: String) = input.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;").replaceAll(" ", "&nbsp;")
}
