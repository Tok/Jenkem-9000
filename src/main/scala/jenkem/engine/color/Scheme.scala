package jenkem.engine.color

object Scheme {
  sealed abstract class Value(val order: Int, val name: String)
  case object Full extends Value(0, "Full")
  case object Default extends Value(1, "Default")
  case object Mono extends Value(2, "Mono")
  case object Lsd extends Value(3, "LSD")
  case object Bwg extends Value(4, "BWG")
  case object Bw extends Value(5, "BW")
  def values = List(Full, Default, Mono, Lsd, Bwg, Bw)

  def valueOf(name: String): Value = {
    name.toUpperCase match {
      case "FULL" => Full
      case "DEFAULT" => Default
      case "MONO" => Mono
      case "LSD" => Lsd
      case "BWG" => Bwg
      case "BW" => Bw
      case _ => throw new IllegalArgumentException("Scheme must be one of: Full, Default, Mono, LSD, BWG or BW")
    }
  }

  type RGB = (Short, Short, Short)
  //IRC colors are defacto standard. scheme colors are loosely proportional to distance from center of cube
  sealed abstract class IrcColor(val name: String, val irc: Short, val rgb: RGB, val isDark: Boolean, val scheme: List[Short])
  case object White     extends IrcColor("white",     0, (255,255,255), false, List(100, 90,100, 10,100,100))
  case object Black     extends IrcColor("black",     1, (  0,  0,  0), true,  List(100, 90,100, 10,100,100))
  case object DarkBlue  extends IrcColor("darkBlue",  2, (  0,  0,127), true,  List(100, 66,  0, 75,  0,  0))
  case object DarkGreen extends IrcColor("darkGreen", 3, (  0,147,  0), true,  List(100, 71,  0, 75,  0,  0))
  case object Red       extends IrcColor("red",       4, (255,  0,  0), true,  List(100, 95,100,100,  0,  0))
  case object Brown     extends IrcColor("brown",     5, (127,  0,  0), true,  List(100, 66,  0, 75,  0,  0))
  case object Purple    extends IrcColor("purple",    6, (156,  0,156), true,  List(100, 48,  0, 60,  0,  0))
  case object Orange    extends IrcColor("orange",    7, (252,127,  0), false, List(100, 70,  0, 75,  0,  0))
  case object Yellow    extends IrcColor("yellow",    8, (255,255,  0), false, List(100, 95,100,100,  0,  0))
  case object Green     extends IrcColor("green",     9, (  0,255,  0), false, List(100, 95,100,100,  0,  0))
  case object Teal      extends IrcColor("teal",     10, (  0,147,147), true,  List(100, 43,  0, 75,  0,  0))
  case object Cyan      extends IrcColor("cyan",     11, (  0,255,255), false, List(100, 95,100,100,  0,  0))
  case object Blue      extends IrcColor("blue",     12, (  0,  0,255), true,  List(100, 95,100,100,  0,  0))
  case object Magenta   extends IrcColor("magenta",  13, (255,  0,255), false, List(100, 95,100,100,  0,  0))
  case object Gray      extends IrcColor("gray",     14, (127,127,127), true,  List(100, 13,  0,  2,100,  0))
  case object LightGray extends IrcColor("lightGray",15, (210,210,210), false, List(100, 25,  0,  5,100,  0))
  def ircColors = List(White, Black, DarkBlue, DarkGreen, Red, Brown, Purple, Orange,
      Yellow, Green, Teal, Cyan, Blue, Magenta, Gray, LightGray)

  def valuOfIrcColor(irc: Short): IrcColor = {
    //TODO simplify this
    irc match {
      case 0 => White
      case 1 => Black
      case 2 => DarkBlue
      case 3 => DarkGreen
      case 4 => Red
      case 5 => Brown
      case 6 => Purple
      case 7 => Orange
      case 8 => Yellow
      case 9 => Green
      case 10 => Teal
      case 11 => Cyan
      case 12 => Blue
      case 13 => Magenta
      case 14 => Gray
      case 15 => LightGray
      case _ => throw new IllegalArgumentException("Fail: Unknown color.")
    }
  }

  def createColorMap(value: Value): Map[IrcColor, Short] = {
    def createColorMap0(ic: List[IrcColor], map: Map[IrcColor, Short]): Map[IrcColor, Short] = {
      if (ic.isEmpty) { map }
      else { createColorMap0(ic.tail,
          map + ((ic.head, ic.head.scheme(value.order)))
              ) }
    }
    createColorMap0(ircColors, Map())
  }
}
