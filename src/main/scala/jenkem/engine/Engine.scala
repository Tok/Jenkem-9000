package jenkem.engine

import scala.Array.canBuildFrom
import scala.util.Random
import jenkem.engine.color.Cube
import jenkem.engine.color.Power
import jenkem.engine.color.Sample
import jenkem.util.ColorUtil
import jenkem.engine.color.Scheme
import jenkem.engine.color.Color

object Engine {
  class Params(
      val method: Method,
      val imageRgb: Map[Sample.Coords, Color.Rgb],
      val colorMap: Map[Scheme.IrcColor, Short],
      val charset: String,
      val settings: Setting.Instance,
      val contrast: Int,
      val brightness: Int,
      val power: Power) {
    val hasAnsi = Pal.hasAnsi(charset)
  }

  def generateLine(par: Params, index: Int): String = {
    if (par.method.equals(Method.Vortacular)) { generateVortacularLine(par, index) }
    else { generatePlainLine(par, index) }
  }

  private def generateVortacularLine(par: Params, index: Int): String = {
    def makeColorSample(x: Int): Sample.Colored = {
      Sample.makeColorSample(par.imageRgb, x, index, par.contrast, par.brightness)
    }
    val indices = (0 until inferWidth(par.imageRgb)).filter(_ % 2 == 0)
    val sam = indices.map(makeColorSample(_)).toList
    val range = (0 until sam.length)
    lazy val diffs = Sample.dirs.map(d => (d, sam.map(Sample.calcRgbDiff(_, d)).toList)).toMap
    lazy val means = Sample.dirs.map(d => (d, sam.map(Sample.calcRgbMean(_, d)).toList)).toMap
    lazy val colors = Sample.dirs.map(d => (d, means.get(d).get.map(Cube.getTwoNearestColors(_, par.colorMap, par.power)).toList)).toMap
    val chars = sam.map(Sample.getAllRgb(_)).map(Cube.getColorChar(par.colorMap, par.charset, par.power, _))
    val totalBgs = chars.map(ColorUtil.getBgString(_))
    def getSwitched(i: Int): String = {
      def direct(old: String, setting: Setting, first: Sample.Dir, second: Sample.Dir): String = {
        //TODO test if PBN pays off here
        if (!par.settings.has(setting)) { old }
        else {
          lazy val f: Color = colors.get(first).get(i)
          lazy val s: Color = colors.get(second).get(i)
          val fd: Short = diffs.get(first).get(i)
          val sd: Short = diffs.get(second).get(i)
          val offset = ((par.settings.get(setting) * -1) + 100) / 5
          if (fd + offset < sd) {
            val ch = if (setting.equals(Setting.LEFTRIGHT)) { Pal.LEFT } else { Pal.UP }
            selectAppropriate(old, f.bg, s.bg, totalBgs(i), s.fg, Pal.get(ch, par.hasAnsi, par.charset))
          } else if (sd + offset < fd) {
            val ch = if (setting.equals(Setting.LEFTRIGHT)) { Pal.RIGHT } else { Pal.DOWN }
            selectAppropriate(old, s.bg, f.bg, totalBgs(i), f.fg, Pal.get(ch, par.hasAnsi, par.charset))
          } else { old }
        }
      }
      val lr = direct(chars(i), Setting.LEFTRIGHT, Sample.LEFT, Sample.RIGHT)
      direct(lr, Setting.UPDOWN, Sample.TOP, Sample.BOT)
    }
    val switched = range.map(ColorUtil.CC + getSwitched(_)).toList
    val charsOnly = switched.map(_.last.toString)
    val pp = postProcess(par, charsOnly.mkString).toCharArray.map(_.toString).toList
    lazy val reps = Pal.pairs.map(p => (p, Pal.getValChars(p._1, par.hasAnsi))).toMap
    def change(c: String): String = {
      reps.keys.find(r => reps(r).contains(c)).map(r => Pal.get(r._2, par.hasAnsi, par.charset)).getOrElse(c)
    }
    val changed = pp.map(change(_))
    val finalLine = range.map(i => switched(i).init + changed(i)).toList
    range.map(makeValid(_, finalLine, switched)).mkString
  }

  private def selectAppropriate(old: String, fix: String, first: String, second: => String, third: => String, character: String): String = {
    val prefix = fix + ","
    if (!fix.equals(first)) { prefix + first + character }
    else if (!fix.equals(second)) { prefix + second + character }
    else if (!fix.equals(third)) { prefix + third + character }
    else { old }
  }

  private def makeValid(i: Int, list: List[String], switched: List[String]): String = {
    val thisOne = list(i)
    if (i != 0 && thisOne.init.equals(switched(i - 1).init)) { thisOne.last.toString }
    else { thisOne }
  }

  private def generatePlainLine(par: Params, index: Int): String = {
    def makeGreySample(x: Int): Sample.Grey = {
      Sample.makeGreySample(par.imageRgb, x, index, par.contrast, par.brightness)
    }
    val indices = (0 until inferWidth(par.imageRgb)).filter(_ % 2 == 0)
    val sam = indices.map(makeGreySample(_)).toList
    def getChar(i: Int): String = {
      def getProcessed(setting: Setting,
          firstPal: Pal, secondPal: Pal,
          firstDir: Sample.Dir, secondDir: Sample.Dir): Option[String] = {
        if (!par.settings.has(setting)) { None }
        else {
          val fp = Pal.get(firstPal, par.hasAnsi, par.charset)
          val sp = Pal.get(secondPal, par.hasAnsi, par.charset)
          val fs = Sample.getDirectedGrey(sam(i), firstDir)
          val ss = Sample.getDirectedGrey(sam(i), secondDir)
          getFor(par.settings.get(setting), fs, ss, fp, sp)
        }
      }
      getProcessed(Setting.UPDOWN, Pal.UP, Pal.DOWN, Sample.TOP, Sample.BOT) match {
        case Some(ud) => ud //first possibility
        case None =>
          getProcessed(Setting.LEFTRIGHT, Pal.LEFT, Pal.RIGHT, Sample.LEFT, Sample.RIGHT) match {
            case Some(lr) => lr //second possibility
            case None => Pal.getCharAbs(par.charset, Sample.getMeanGrey(sam(i))) //default
          }
      }
    }
    val line = (0 until sam.length).map(i => getChar(i)).mkString
    postProcess(par, line)
  }

  private def getFor(offset: Int, first: Int, second: Int, firstChar: String, secondChar: String): Option[String] = {
    val fCond = first <= Color.CENTER + offset && second > Color.CENTER - offset
    val sCond = first > Color.CENTER - offset && second <= Color.CENTER + offset
    if (fCond && sCond) { Some(Random.shuffle(List(firstChar, secondChar)).head) }
    else if (fCond) { Some(firstChar) }
    else if (sCond) { Some(secondChar) }
    else { None }
  }

  type CharMapType = Map[Product, String]

  private def postProcess(par: Params, line: String): String = {
    lazy val chr: CharMapType = Pal.values.map(v => (v, Pal.get(v, par.hasAnsi, par.charset))).toMap
    val range = (0 until line.length)
    val lineList = line.map(_.toString).toList
    val dbqp = dbqpLine(lineList, range, par, chr)
    val diag = diagLine(dbqp, range, par, chr)
    val hor = horLine(diag, range, par, chr)
    val vert = vertLine(hor, range, par, chr).mkString
    vert
  }

  private def dbqpLine(in: List[String], range: Range, par: Params, chr: CharMapType): List[String] = {
    if (!par.settings.has(Setting.DBQP)) { in.toList } else { changeDbqP(in, range, par, chr) }
  }

  private def changeDbqP(in: List[String], range: Range, par: Params, chr: CharMapType): List[String] = {
    val d = range.map(changeDbqp(in.toList, range, par, _, chr.get(Pal.DOWN).get, chr.get(Pal.RIGHT_DOWN).get, false))
    val b = range.map(changeDbqp(d.toList, range, par, _, chr.get(Pal.DOWN).get, chr.get(Pal.LEFT_DOWN).get, true))
    val q = range.map(changeDbqp(b.toList, range, par, _, chr.get(Pal.UP).get, chr.get(Pal.RIGHT_UP).get, false))
    val p = range.map(changeDbqp(q.toList, range, par, _, chr.get(Pal.UP).get, chr.get(Pal.LEFT_UP).get, true)).toList
    p
  }

  private def isFirstOrLast(i: Int, range: Range): Boolean = i == 0 || i == range.last
  private def changeDbqp(list: List[String], range: Range, par: Params, i: Int, thisOne: String, to: String, darkLeft: Boolean): String = {
    lazy val current: String = list(i)
    lazy val last: String = list(i - 1)
    lazy val next: String = list(i + 1)
    lazy val isCurrentEqual: Boolean = current.equals(thisOne)
    def isLastEqual: Boolean = thisOne.equals(last)
    def isNextEqual: Boolean = thisOne.equals(next)
    def isLastDark: Boolean = Pal.isDark(par.charset, last)
    def isNextDark: Boolean = Pal.isDark(par.charset, next)
    def isLastBright: Boolean = Pal.isBright(par.charset, last)
    def isNextBright: Boolean = Pal.isBright(par.charset, next)
    def isLastBrightOrEqual: Boolean = isLastBright || isLastEqual
    def isNextBrightOrEqual: Boolean = isNextBright || isNextEqual
    def leftCondition: Boolean = darkLeft && isLastDark && isNextBrightOrEqual
    def rightCondition: Boolean = !darkLeft && isNextDark && isLastBrightOrEqual
    def notLeftAndNotRight: Boolean = !leftCondition && !rightCondition
    if (isFirstOrLast(i, range) || !isCurrentEqual || notLeftAndNotRight) { current } else { to }
  }

  private def diagLine(in: List[String], range: Range, par: Params, chr: CharMapType): List[String] = {
    if (!par.settings.has(Setting.DIAGONAL)) { in.toList }
    else {
      def changeDiag(list: List[String], i: Int): String = {
        lazy val current: String = list(i)
        lazy val currentEqualsDown = current.equals(chr.get(Pal.DOWN).get)
        lazy val currentEqualsUp = current.equals(chr.get(Pal.UP).get)
        lazy val last: String = list(i - 1)
        lazy val lastEqualsDown = last.equals(chr.get(Pal.DOWN).get)
        lazy val lastEqualsUp = last.equals(chr.get(Pal.UP).get)
        lazy val next: String = list(i + 1)
        lazy val nextEqualsDown = next.equals(chr.get(Pal.DOWN).get)
        lazy val nextEqualsUp = next.equals(chr.get(Pal.UP).get)
        lazy val currentOrNextIsUp = currentEqualsUp || nextEqualsUp
        lazy val currentOrLastIsUp = currentEqualsUp || lastEqualsUp
        if (isFirstOrLast(i, range)) { current }
        else if (lastEqualsDown && currentOrNextIsUp) { chr.get(Pal.DOWN_UP).get }
        else if (currentOrLastIsUp && nextEqualsDown) { chr.get(Pal.UP_DOWN).get }
        else { current }
      }
      range.map(changeDiag(in.toList, _)).toList
    }
  }

  private def horLine(in: List[String], range: Range, par: Params, chr: CharMapType): List[String] = {
    if (!par.settings.has(Setting.HORIZONTAL)) { in.toList }
    else {
      def changeHor(list: List[String], i: Int, from: String, to: String): String = {
        lazy val current: String = list(i)
        lazy val last: String = list(i - 1)
        lazy val next: String = list(i + 1)
        if (isFirstOrLast(i, range)) { current }
        else if (last.equals(Pal.darkest(par.charset)) && current.equals(from) && next.equals(from)) { to }
        else if (last.equals(from) && current.equals(from) && next.equals(Pal.darkest(par.charset))) { to }
        else { current }
      }
      val h = range.map(changeHor(in.toList, _, chr.get(Pal.DOWN).get, chr.get(Pal.H_LINE).get))
      range.map(changeHor(h.toList, _, chr.get(Pal.UP).get, chr.get(Pal.H_LINE).get)).toList
    }
  }

  private def vertLine(in: List[String], range: Range, par: Params, chr: CharMapType): List[String] = {
    if (!par.settings.has(Setting.VERTICAL)) { in.toList }
    else {
      def changeVert(list: List[String], i: Int, from: String, to: String): String = {
        if (list(i).equals(from)) { to } else { list(i) }
      }
      val foo = range.map(changeVert(in.toList, _, chr.get(Pal.LEFT).get, chr.get(Pal.V_LINE).get))
      range.map(changeVert(foo.toList, _, chr.get(Pal.RIGHT).get, chr.get(Pal.V_LINE).get)).toList
    }
  }

  private def inferWidth(imageRgb: Map[Sample.Coords, Color.Rgb]): Short = {
    (imageRgb.keys.toList.map(t => t._2).max).shortValue
  }
}
