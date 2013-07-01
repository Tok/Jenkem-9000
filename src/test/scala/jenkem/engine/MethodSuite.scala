package jenkem.engine

import org.junit.runner.RunWith
import jenkem.AbstractTester
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class MethodSuite extends AbstractTester {
  test("Values") {
    assert(Method.valueOf("vortacular").get === Method.Vortacular)
    assert(Method.valueOf("pwntari").get === Method.Pwntari)
    assert(Method.valueOf("plain").get === Method.Plain)
    assert(Method.valueOf("stencil").get === Method.Stencil)
    assert(Method.valueOf("--") === None)
  }

  test("Has Color") {
    assert(Method.Vortacular.hasColor)
    assert(Method.Pwntari.hasColor)
    assert(!Method.Plain.hasColor)
    assert(!Method.Stencil.hasColor)
  }

  test("Has Kick") {
    assert(Method.Vortacular.hasKick)
    assert(!Method.Pwntari.hasKick)
    assert(Method.Plain.hasKick)
    assert(Method.Stencil.hasKick)
  }

  test("Pointless") {
    Method.values.foreach(testAny(_, true))
  }
}
