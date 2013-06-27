package jenkem.engine

import scala.Array.canBuildFrom
import scala.util.Random
import jenkem.engine.color.Cube
import jenkem.engine.color.Power
import jenkem.engine.color.Sample
import jenkem.util.ColorUtil
import jenkem.engine.color.Scheme
import jenkem.engine.color.Color
import jenkem.util.ImageUtil

object Engine {
  val comma = ","

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
    par.method match {
      case Method.Vortacular => generateVortacularLine(par, index)
      case Method.Pwntari => generatePwntariLine(par, index)
      case _ => generatePlainLine(par, index)
    }
  }

  private def generatePwntariLine(par: Params, index: Int): String = {
    def consolidateDuplicates(chars: List[String]): List[String] = {
      def consolidateDuplicates0(chars: List[String], accu: List[String], i: Int): List[String] = {
        if (i == chars.length) { accu }
        else {
          val thisOne = chars(i)
          if (i == 0) { consolidateDuplicates0(chars, accu ::: List(thisOne), i + 1) }
          else {
            val eq = thisOne.equals(chars(i - 1))
            val newChar = if (eq) { List(par.charset.head.toString) } else { List(thisOne) }
            consolidateDuplicates0(chars, accu ::: newChar, i + 1)
          }
        }
      }
      consolidateDuplicates0(chars, Nil, 0)
    }
    val indices = (0 until inferWidth(par.imageRgb))
    val t = indices.map(i => ImageUtil.getPixels(par.imageRgb, i, index))
    val b = indices.map(i => ImageUtil.getPixels(par.imageRgb, i, index + 1))
    val tIrc = t.map(Cube.getNearest(_, par.colorMap))
    val bIrc = b.map(Cube.getNearest(_, par.colorMap))
    val chars = indices.map(i => ColorUtil.makePwnIrc(bIrc(i), tIrc(i))).toList
    consolidateDuplicates(chars).mkString
  }

  private def generateVortacularLine(par: Params, index: Int): String = {
    def makeColorSample(x: Int): Sample.Colored = {
      Sample.makeColorSample(par.imageRgb, x, index, par.contrast, par.brightness)
    }
    val sam = (0 until inferWidth(par.imageRgb)).filter(_ % 2 == 0).map(makeColorSample(_)).toList
    val range = (0 until sam.length)
    lazy val diffs = Sample.dirs.map(d => (d, sam.map(Sample.calcRgbDiff(_, d)).toList)).toMap
    lazy val means = Sample.dirs.map(d => (d, sam.map(Sample.calcRgbMean(_, d)).toList)).toMap
    lazy val colors = Sample.dirs.map(d => (d, means.get(d).get.map(Cube.getTwoNearestColors(_, par.colorMap, par.power)).toList)).toMap
    lazy val tl = sam.map(_._1)
    lazy val tr = sam.map(_._2)
    lazy val bl = sam.map(_._3)
    lazy val br = sam.map(_._4)
    lazy val tIrc = range.map(i => Sample.calcMean(tl(i), tr(i))).map(Cube.getNearest(_, par.colorMap)).toList
    lazy val bIrc = range.map(i => Sample.calcMean(bl(i), br(i))).map(Cube.getNearest(_, par.colorMap)).toList
    lazy val lIrc = range.map(i => Sample.calcMean(tl(i), bl(i))).map(Cube.getNearest(_, par.colorMap)).toList
    lazy val rIrc = range.map(i => Sample.calcMean(tr(i), br(i))).map(Cube.getNearest(_, par.colorMap)).toList
    val chars = sam.map(Sample.getAllRgb(_)).map(Cube.getColorChar(par.colorMap, par.charset, par.power, _))
    val totalBgs = chars.map(ColorUtil.getBgString(_))
    def getSwitched(i: Int): String = {
      def direct(old: String, setting: Setting): String = {
        if (!par.settings.has(setting)) { old }
        else {
          val offset = ((par.settings.get(setting) + 100) * -1) / 4
          if (setting.equals(Setting.LEFTRIGHT)) {
            if (lIrc(i) == rIrc(i)) { old }
            else {
              val leftDiff: Short = diffs.get(Sample.LEFT).get(i)
              val rightDiff: Short = diffs.get(Sample.RIGHT).get(i)
              if (leftDiff + offset < rightDiff) {
                lIrc(i) + comma + rIrc(i) + Pal.get(Pal.LEFT, par.hasAnsi, par.charset)
              } else if (rightDiff + offset < leftDiff) {
                rIrc(i) + comma + lIrc(i) + Pal.get(Pal.RIGHT, par.hasAnsi, par.charset)
              } else { old }
            }
          } else { //setting.equals(Setting.UPDOWN)
            if (tIrc(i) == bIrc(i)) { old }
            else {
              val topDiff: Short = diffs.get(Sample.TOP).get(i)
              val botDiff: Short = diffs.get(Sample.BOT).get(i)
              if (topDiff + offset < botDiff) {
                tIrc(i) + comma + bIrc(i) + Pal.get(Pal.UP, par.hasAnsi, par.charset)
              } else if (botDiff + offset < topDiff) {
                bIrc(i) + comma + tIrc(i) + Pal.get(Pal.DOWN, par.hasAnsi, par.charset)
              } else { old }
            }
          }
        }
      }
      direct(direct(chars(i), Setting.LEFTRIGHT), Setting.UPDOWN)
    }
    val switched = range.map(ColorUtil.CC + getSwitched(_)).toList
    val charsOnly = switched.map(_.last.toString)
    val pp = postProcess(par, charsOnly.mkString, true).toCharArray.map(_.toString).toList

    lazy val reps = Pal.pairs.map(p => (p, Pal.getValChars(p._1, par.hasAnsi))).toMap
    def change(c: String): String = {
      reps.keys.find(r => reps(r).contains(c)).map(r => Pal.get(r._2, par.hasAnsi, par.charset)).getOrElse(c)
    }
    val changed = pp.map(change(_))
    val finalLine = range.map(i => switched(i).init + changed(i)).toList

    //val finalLine = range.map(i => switched(i).init + pp(i)).toList
    range.map(makeValid(_, finalLine, switched)).mkString
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
          getFor(par.settings.get(setting) / 10, fs, ss, fp, sp)
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
    postProcess(par, line, false)
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

  private def postProcess(par: Params, line: String, hasCol: Boolean): String = {
    lazy val chr: CharMapType = Pal.values.map(v => (v, Pal.get(v, par.hasAnsi, par.charset))).toMap
    val range = (0 until line.length)
    val lineList = line.map(_.toString).toList
    val dbqp = dbqpLine(lineList, range, par, chr, hasCol)
    val diag = diagLine(dbqp, range, par, chr)
    val hor = horLine(diag, range, par, chr)
    val vert = vertLine(hor, range, par, chr).mkString
    vert
  }

  private def dbqpLine(in: List[String], range: Range, par: Params, chr: CharMapType, hasCol: Boolean): List[String] = {
    if (!par.settings.has(Setting.DBQP)) { in.toList } else { changeDbqp(in, range, par, chr, hasCol) }
  }

  private def changeDbqp(in: List[String], range: Range, par: Params, chr: CharMapType, hasCol: Boolean): List[String] = {
    def getD: String = if (hasCol) { chr.get(Pal.LEFT_DOWN).get } else { chr.get(Pal.RIGHT_DOWN).get }
    def getB: String = if (hasCol) { chr.get(Pal.RIGHT_DOWN).get } else { chr.get(Pal.LEFT_DOWN).get }
    def getQ: String = if (hasCol) { chr.get(Pal.LEFT_UP).get } else { chr.get(Pal.RIGHT_UP).get }
    def getP: String = if (hasCol) { chr.get(Pal.RIGHT_UP).get } else { chr.get(Pal.LEFT_UP).get }
    val d = range.map(changeDbqp(in.toList, range, par, _, chr.get(Pal.DOWN).get, getD, false))
    val b = range.map(changeDbqp(d.toList, range, par, _, chr.get(Pal.DOWN).get, getB, true))
    val q = range.map(changeDbqp(b.toList, range, par, _, chr.get(Pal.UP).get, getQ, false))
    val p = range.map(changeDbqp(q.toList, range, par, _, chr.get(Pal.UP).get, getP, true))
    p.toList
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
