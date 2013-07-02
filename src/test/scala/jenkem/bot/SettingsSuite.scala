package jenkem.bot

import org.junit.runner.RunWith
import jenkem.AbstractTester
import jenkem.bot.status.Disconnected
import jenkem.bot.status.NotSending
import org.scalatest.junit.JUnitRunner
import jenkem.bot.status.BotStatus
import jenkem.engine.Method
import jenkem.engine.color.Scheme
import jenkem.engine.Pal
import jenkem.engine.Kick
import jenkem.engine.color.Power
import jenkem.engine.Proportion
import jenkem.engine.color.Color
import jenkem.util.InitUtil

@RunWith(classOf[JUnitRunner])
class SettingsSuite extends AbstractTester {
  val blackMap: Color.RgbMap = makeColorMap((0, 0, 0))
  val whiteMap: Color.RgbMap = makeColorMap((255, 255, 255))
  val grayMap: Color.RgbMap = makeColorMap((127, 127, 127))
  val redMap: Color.RgbMap = makeColorMap((255, 0, 0))

  test("Default") {
    val cs = new ConversionSettings
    assert(cs.width === 68)
    assert(cs.method === Method.default)
    assert(cs.colorMap === Scheme.createColorMap(Scheme.default))
    assert(cs.schemeName === Scheme.default.toString)
    assert(cs.chars === Pal.default.chars)
    assert(cs.kick === Kick.OFF)
    assert(cs.power === Power.default)
    assert(cs.proportion === Proportion.default)
  }

  test("Parameters") {
    val blackParams = (new ConversionSettings).getParams(blackMap)
    assert(blackParams.method === Method.Stencil)
    assert(blackParams.colorMap === Scheme.createColorMap(Scheme.Bwg))
    assert(blackParams.characters === Pal.HCrude.chars)
    val whiteParams = (new ConversionSettings).getParams(whiteMap)
    assert(whiteParams.method === Method.Stencil)
    assert(whiteParams.colorMap === Scheme.createColorMap(Scheme.Bwg))
    assert(whiteParams.characters === Pal.HCrude.chars)
    val grayParams = (new ConversionSettings).getParams(grayMap)
    assert(grayParams.method === Method.default)
    assert(grayParams.colorMap === Scheme.createColorMap(Scheme.Bwg))
    assert(grayParams.characters === Pal.default.chars)
    val redParams = (new ConversionSettings).getParams(redMap)
    assert(redParams.method === Method.default)
    assert(redParams.colorMap === Scheme.createColorMap(Scheme.default))
    assert(redParams.characters === Pal.default.chars)
  }

  def makeColorMap(color: Color.Rgb): Color.RgbMap = {
    Map((0, 0) -> color, (0, 1) -> color, (1, 0) -> color, (1, 1) -> color)
  }
}
