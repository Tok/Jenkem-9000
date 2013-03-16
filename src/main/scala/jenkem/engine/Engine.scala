package jenkem.engine

import scala.Array.canBuildFrom
import scala.util.Random

import jenkem.engine.color.Cube
import jenkem.engine.color.Power
import jenkem.engine.color.Sample
import jenkem.shared.CharacterSet
import jenkem.shared.color.IrcColor
import jenkem.util.ColorUtil

class Engine {
  val MAX = 255
  val CENTER = 127
  val MIN = 0

  //var colorMap: Map[IrcColor, Int] = _
  var colorMap: java.util.Map[IrcColor, Integer] = _
  var imageRgb: Map[(Int, Int), (Short, Short, Short)] = Map() // ("row:column", rgb[])
  var settings: ProcSettings.Instance = _
  var hasAnsi: Boolean = _

  //TODO width should be inferred instead of passed.
  var width: Int = _ //number or columns (should be even)
  var contrast: Int = _
  var brightness: Int = _
  var charset: String = _
  var power: Power.Value = _

  def setParams(imageRgb: Map[(Int, Int), (Short, Short, Short)], width: Int,
    charset: String, contrast: Int, brightness: Int,
    settings: ProcSettings.Instance) {
    this.imageRgb = imageRgb
    this.width = width
    this.charset = charset
    this.hasAnsi = CharacterSet.hasAnsi(charset)
    this.contrast = contrast
    this.brightness = brightness
    this.settings = settings
  }

  def prepareEngine(colorMap: java.util.Map[IrcColor, Integer], power: Power.Value) {
    this.colorMap = colorMap
    this.power = power
  }

  def generateLine(method: ConversionMethod.Value, index: Int): String = {
    //try {
      if (method.equals(ConversionMethod.Vortacular)) { generateVortacularLine(index) }
      else { generatePlainLine(index) }
    //} catch {
    //  case t: Throwable => println(t.getStackTraceString); ""
    //}
  }

  private def generateVortacularLine(index: Int): String = {
    def makeColorSample(x: Int): ((Short, Short, Short), (Short, Short, Short),
        (Short, Short, Short), (Short, Short, Short)) = {
      Sample.makeColorSample(imageRgb, x, index, contrast, brightness)
    }
    val indices = (0 until width).filter(_ % 2 == 0)
    val sam = indices.map(makeColorSample(_)).toList
    val range = (0 until sam.length)

    lazy val leftDiff = sam.map(Sample.calcRgbDiff(_, Sample.LEFT)).toList
    lazy val rightDiff = sam.map(Sample.calcRgbDiff(_, Sample.RIGHT)).toList
    lazy val topDiff = sam.map(Sample.calcRgbDiff(_, Sample.TOP)).toList
    lazy val botDiff = sam.map(Sample.calcRgbDiff(_, Sample.BOT)).toList

    lazy val leftRgb = sam.map(Sample.calcRgbMean(_, Sample.LEFT)).toList
    lazy val rightRgb = sam.map(Sample.calcRgbMean(_, Sample.RIGHT)).toList
    lazy val topRgb = sam.map(Sample.calcRgbMean(_, Sample.TOP)).toList
    lazy val botRgb = sam.map(Sample.calcRgbMean(_, Sample.BOT)).toList

    lazy val left = leftRgb.map(Cube.getTwoNearestColors(_, colorMap, power)).toList
    lazy val right = rightRgb.map(Cube.getTwoNearestColors(_, colorMap, power)).toList
    lazy val top = topRgb.map(Cube.getTwoNearestColors(_, colorMap, power)).toList
    lazy val bot = botRgb.map(Cube.getTwoNearestColors(_, colorMap, power)).toList

    lazy val allR = range.map(i => ((leftRgb(i)._1 + rightRgb(i)._1 + topRgb(i)._1 + botRgb(i)._1) / 4).shortValue).toList
    lazy val allG = range.map(i => ((leftRgb(i)._2 + rightRgb(i)._2 + topRgb(i)._2 + botRgb(i)._2) / 4).shortValue).toList
    lazy val allB = range.map(i => ((leftRgb(i)._3 + rightRgb(i)._3 + topRgb(i)._3 + botRgb(i)._3) / 4).shortValue).toList
    lazy val allRgb = range.map(i => (allR(i), allG(i), allB(i))).toList

    lazy val chars = allRgb.map(Cube.getColorChar(colorMap, charset, power, _))
    lazy val totalBgs = chars.map(Cube.getBgCode(_))

    def getSwitched(i: Int): String = {
      def selectAppropriate(old: String, fix: String, first: String, second: String, third: String, character: String): String = {
        val prefix = fix + ","
        if (!fix.equals(first)) { prefix + first + character }
        else if (!fix.equals(second)) { prefix + second + character }
        else if (!fix.equals(third)) { prefix + third + character }
        else { old }
      }
      def upDown(old: String): String = {
        if (settings.has(ProcSettings.UPDOWN)) {
          lazy val td = topDiff(i)
          lazy val bd = botDiff(i)
          lazy val topBg = top(i).bg
          lazy val botBg = bot(i).bg
          lazy val topFg = top(i).fg
          lazy val botFg = bot(i).fg
          lazy val offset = ((settings.get(ProcSettings.UPDOWN) * -1) + 100) / 5
          if (td + offset < bd) {
            selectAppropriate(old, topBg, botBg, totalBgs(i), botFg, Scheme.get(Scheme.UP, hasAnsi))
          } else if (bd + offset < td) {
            selectAppropriate(old, botBg, topBg, totalBgs(i), topFg, Scheme.get(Scheme.DOWN, hasAnsi))
          } else { old }
        } else { old }
      }
      def leftRight(old: String): String = {
        if (settings.has(ProcSettings.LEFTRIGHT)) {
          lazy val ld = leftDiff(i)
          lazy val rd = rightDiff(i)
          lazy val leftBg = left(i).bg
          lazy val rightBg = right(i).bg
          lazy val leftFg = left(i).fg
          lazy val rightFg = right(i).fg
          lazy val offset = ((settings.get(ProcSettings.LEFTRIGHT) * -1) + 100) / 5
          if (ld + offset < rd) {
            selectAppropriate(old, leftBg, rightBg, totalBgs(i), rightFg, Scheme.get(Scheme.LEFT, hasAnsi))
          } else if (rd + offset < ld) {
            selectAppropriate(old, rightBg, leftBg, totalBgs(i), leftFg, Scheme.get(Scheme.RIGHT, hasAnsi))
          } else { old }
        } else { old }
      }
      upDown(leftRight(chars(i)))
    }

    val switched = range.map(ColorUtil.CC + getSwitched(_)).toList
    val charsOnly = switched.map(_.last.toString)
    val pp = postProcess(charsOnly.mkString).toCharArray.map(_.toString).toList

    /*
    def change(c: String): String = {
      if (Scheme.get(Scheme.DOWN_UP, hasAnsi).contains(c)) { Scheme.get(Scheme.UP_DOWN, hasAnsi) }
      if (Scheme.get(Scheme.UP_DOWN, hasAnsi).contains(c)) { Scheme.get(Scheme.DOWN_UP, hasAnsi) }
      if (Scheme.get(Scheme.LEFT_UP, hasAnsi).contains(c)) { Scheme.get(Scheme.RIGHT_UP, hasAnsi) }
      if (Scheme.get(Scheme.LEFT_DOWN, hasAnsi).contains(c)) { Scheme.get(Scheme.RIGHT_DOWN, hasAnsi) }
      if (Scheme.get(Scheme.RIGHT_UP, hasAnsi).contains(c)) { Scheme.get(Scheme.LEFT_UP, hasAnsi) }
      if (Scheme.get(Scheme.RIGHT_DOWN, hasAnsi).contains(c)) { Scheme.get(Scheme.LEFT_DOWN, hasAnsi) }
      else { c }
    }*/

    //val changed = pp.map(change(_))
    //val finalLine = range.map(i => switched(i).init + changed(i)).toList
    val finalLine = range.map(i => switched(i).init + pp(i)).toList

    def makeValid(i: Int, list: List[String]): String = {
      val thisOne = list(i)
      if (i == 0) { thisOne }
      else {
        if (thisOne.init.equals(switched(i - 1).init)) { thisOne.last.toString }
        else { thisOne }
      }
    }

    range.map(makeValid(_, finalLine)).mkString
  }

  private def getFg(s: String): Int = s.split(",")(0).tail.toInt
  private def getBg(s: String): Int = s.split(",")(1).init.toInt

  private def generatePlainLine(index: Int): String = {
    def makeGreySample(x: Int): (Short, Short, Short, Short) = {
      Sample.makeGreySample(imageRgb, x, index, contrast, brightness)
    }
    val indices = (0 until width).filter(_ % 2 == 0)
    val sam = indices.map(makeGreySample(_)).toList
    val range = (0 until sam.length)
    lazy val left = sam.map(s =>
          Math.abs(Sample.getGrey(s, Sample.LEFT, Sample.TOP)
              + Sample.getGrey(s, Sample.LEFT, Sample.BOT)) / 2).toList
    lazy val right = sam.map(s =>
          Math.abs(Sample.getGrey(s, Sample.RIGHT, Sample.TOP)
              + Sample.getGrey(s, Sample.RIGHT, Sample.BOT)) / 2).toList
    lazy val top = sam.map(s =>
        Math.abs(Sample.getGrey(s, Sample.LEFT, Sample.TOP)
              + Sample.getGrey(s, Sample.RIGHT, Sample.TOP)) / 2).toList
    lazy val bot = sam.map(s =>
        Math.abs(Sample.getGrey(s, Sample.LEFT, Sample.BOT)
              + Sample.getGrey(s, Sample.RIGHT, Sample.BOT)) / 2).toList
    lazy val allGrey = sam.map(s =>
       (Sample.getGrey(s, Sample.LEFT, Sample.TOP) +
        Sample.getGrey(s, Sample.RIGHT, Sample.TOP) +
        Sample.getGrey(s, Sample.LEFT, Sample.BOT) +
        Sample.getGrey(s, Sample.RIGHT, Sample.BOT)) / 4).toList
    def getChar(i: Int): String = {
      val default = Scheme.getCharAbs(charset, allGrey(i))
      def getFor(offset: Int, first: Int, second: Int, firstChar: String, secondChar: String): String = {
        val fCond = first <= CENTER + offset && second > CENTER - offset
        val sCond = first > CENTER - offset && second <= CENTER + offset
        if (fCond && sCond) { Random.shuffle(List(firstChar, secondChar)).head }
        else if (fCond) { firstChar }
        else if (sCond) { secondChar }
        else { "" }
      }
      /*
      if (settings.has(ProcSettings.DBQP)) {
        val dbqpOffset = (100 - settings.get(ProcSettings.DBQP)) / 3
        val tl = sam(i)._1 - dbqpOffset < (sam(i)._2 + sam(i)._3 + sam(i)._4) / 3
        val tr = sam(i)._2 - dbqpOffset < (sam(i)._1 + sam(i)._3 + sam(i)._4) / 3
        val bl = sam(i)._3 - dbqpOffset < (sam(i)._1 + sam(i)._2 + sam(i)._4) / 3
        val br = sam(i)._4 - dbqpOffset < (sam(i)._1 + sam(i)._2 + sam(i)._3) / 3
        if ( tl && !tr && !bl && !br) { return scheme.getLeftUp }
        if (!tl &&  tr && !bl && !br) { return scheme.getRightUp }
        if (!tl && !tr &&  bl && !br) { return scheme.getLeftDown }
        if (!tl && !tr && !bl &&  br) { return scheme.getRightDown }
      }
      */
      /*
      if (settings.has(ProcSettings.DIAGONAL)) {
        val bltr = (sam(i)._3 + sam(i)._2) / 2
        val brtl = (sam(i)._4 + sam(i)._1) / 2
        val diag = getFor(settings.get(ProcSettings.DIAGONAL), bltr, brtl, scheme.getUpDown, scheme.getDownUp)
        if (!diag.equals("")) { return diag }
      }*/
      if (settings.has(ProcSettings.UPDOWN)) {
        val u = Scheme.get(Scheme.UP, hasAnsi)
        val d = Scheme.get(Scheme.DOWN, hasAnsi)
        val ud = getFor(settings.get(ProcSettings.UPDOWN), top(i), bot(i), u, d)
        if (!ud.equals("")) { return ud }
      }
      if (settings.has(ProcSettings.LEFTRIGHT)) {
        val l = Scheme.get(Scheme.LEFT, hasAnsi)
        val r = Scheme.get(Scheme.RIGHT, hasAnsi)
        val lr = getFor(settings.get(ProcSettings.LEFTRIGHT), left(i), right(i), l, r)
        if (!lr.equals("")) { return lr }
      }
      default
    }

    val line = range.map(i => getChar(i)).mkString
    postProcess(line)
  }

  private def postProcess(line: String): String = {
    lazy val down = Scheme.get(Scheme.DOWN, hasAnsi)
    lazy val up = Scheme.get(Scheme.UP, hasAnsi)
    lazy val left = Scheme.get(Scheme.LEFT, hasAnsi)
    lazy val right = Scheme.get(Scheme.RIGHT, hasAnsi)
    lazy val du = Scheme.get(Scheme.DOWN_UP, hasAnsi)
    lazy val ud = Scheme.get(Scheme.UP_DOWN, hasAnsi)
    lazy val lu = Scheme.get(Scheme.LEFT_UP, hasAnsi)
    lazy val ld = Scheme.get(Scheme.LEFT_DOWN, hasAnsi)
    lazy val ru = Scheme.get(Scheme.RIGHT_UP, hasAnsi)
    lazy val rd = Scheme.get(Scheme.RIGHT_DOWN, hasAnsi)
    lazy val hl = Scheme.get(Scheme.H_LINE, hasAnsi)
    lazy val vl = Scheme.get(Scheme.V_LINE, hasAnsi)
    val sels = List("DBQP", "DIAG", "HOR", "VERT")
    val range = (0 until line.length)
    def postProcess0(in: List[String], sel: String): String = {
      def dbqpLine(in: List[String]): List[String] = {
        if (settings.has(ProcSettings.DBQP)) {
          def changeDbqp(list: List[String], i: Int, thisOne: String, to: String, darkLeft: Boolean): String = {
            if (i == 0 || i == range.last) { list(i) }
            else if (darkLeft
                && Scheme.isDark(charset, list(i-1)) && list(i).equals(thisOne)
                && (Scheme.isBright(charset, list(i+1)) || list(i+1).equals(thisOne))) { to }
            else if (!darkLeft
                && (Scheme.isBright(charset, list(i-1)) || list(i-1).equals(thisOne))
                && list(i).equals(thisOne) && Scheme.isDark(charset, list(i+1))) { to }
            else { list(i) }
          }
          val d = range.map(changeDbqp(in.toList, _, down, rd, false))
          val b = range.map(changeDbqp(d.toList, _, down, ld, true))
          val q = range.map(changeDbqp(b.toList, _, up, ru, false))
                  range.map(changeDbqp(q.toList, _, up, lu, true)).toList
        } else { in.toList }
      }
      def diagLine(in: List[String]): List[String] = {
        if (settings.has(ProcSettings.DIAGONAL)) {
          def changeDiag(list: List[String], i: Int): String = {
            if (i == 0 || i == range.last) { list(i) }
            else if (list(i-1).equals(down) && list(i).equals(up)) { du }
            else if (list(i-1).equals(down) && list(i+1).equals(up)) { du }
            else if (list(i).equals(up) && list(i+1).equals(down)) { ud }
            else if (list(i-1).equals(up) && list(i+1).equals(down)) { ud }
            else { list(i) }
          }
          range.map(changeDiag(in.toList, _)).toList
        } else { in.toList }
      }
      def horLine(in: List[String]): List[String] = {
        if (settings.has(ProcSettings.HORIZONTAL)) {
          def changeHor(list: List[String], i: Int, from: String, to: String): String = {
            if (i == 0 || i == range.last) { list(i) }
            else if (list(i-1).equals(darkest) && list(i).equals(from) && list(i+1).equals(from)) { to }
            else if (list(i-1).equals(from) && list(i).equals(from) && list(i+1).equals(darkest)) { to }
            else { list(i) }
          }
          val h = range.map(changeHor(in.toList, _, down, hl))
          range.map(changeHor(h.toList, _, up, hl)).toList
        } else { in.toList }
      }
      def vertLine(in: List[String]): List[String] = {
        if (settings.has(ProcSettings.VERTICAL)) {
          def changeVert(list: List[String], i: Int, from: String, to: String): String = {
            if (list(i).equals(from)) { to } else { list(i) }
          }
          val foo = range.map(changeVert(in.toList, _, left, vl))
          range.map(changeVert(foo.toList, _, right, vl)).toList
        } else { in.toList }
      }
      if (sel.equals("DBQP")) { postProcess0(dbqpLine(in), "DIAG") }
      else if (sel.equals("DIAG")) { postProcess0(diagLine(in), "HOR") }
      else if (sel.equals("HOR")) { postProcess0(horLine(in), "VERT") }
      else if (sel.equals("VERT")) { postProcess0(vertLine(in), "") }
      else { in.mkString }
    }
    postProcess0(line.map(_.toString).toList, sels.head)
  }

  private def darkest: String = charset.last.toString
  private def brightest: String = charset.head.toString

  @Deprecated
  def makeLegacy(imageRgb: Map[(Int, Int), (Short, Short, Short)]): java.util.Map[String, Array[Integer]] = {
    val legacyImageRgb: java.util.Map[String, Array[Integer]] =
      new java.util.HashMap[String, Array[Integer]]()
    imageRgb.foreach(p => legacyImageRgb.put(
        (p._1)._1 + ":" + (p._1)._2,
        Array(new Integer(p._2._1), new Integer(p._2._2), new Integer(p._2._3))))
    legacyImageRgb
  }
}
