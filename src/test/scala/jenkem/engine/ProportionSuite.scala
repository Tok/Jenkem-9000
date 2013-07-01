package jenkem.engine

import org.junit.runner.RunWith
import jenkem.AbstractTester
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ProportionSuite extends AbstractTester {
  val optimize = "optimize"
  val exact = "exact"

  test("Default") {
    assert(Proportion.default === Proportion.Optimize)
  }

  test("Values") {
    assert(Proportion.valueOf(optimize).isInstanceOf[Option[Proportion]])
    assert(Proportion.valueOf(exact).isInstanceOf[Option[Proportion]])
    assert(Proportion.valueOf(optimize).get === Proportion.Optimize)
    assert(Proportion.valueOf(exact).get === Proportion.Exact)
    assert(Proportion.valueOf("--") === None)
    assert(Proportion.Optimize.toString.equalsIgnoreCase(optimize))
    assert(Proportion.Exact.toString.equalsIgnoreCase(exact))
  }

  test("Width And Height") {
    assert(Proportion.getWidthAndHeight(Proportion.Optimize, Method.Vortacular, 10, 100, 200) === (20, 20))
    assert(Proportion.getWidthAndHeight(Proportion.Optimize, Method.Pwntari, 10, 100, 200) === (10, 20))
    assert(Proportion.getWidthAndHeight(Proportion.Optimize, Method.Plain, 10, 100, 200) === (20, 20))
    assert(Proportion.getWidthAndHeight(Proportion.Optimize, Method.Stencil, 10, 100, 200) === (20, 20))
    assert(Proportion.getWidthAndHeight(Proportion.Exact, Method.Vortacular, 10, 100, 200) === (20, 20))
    assert(Proportion.getWidthAndHeight(Proportion.Exact, Method.Pwntari, 10, 100, 200) === (10, 20))
    assert(Proportion.getWidthAndHeight(Proportion.Exact, Method.Plain, 10, 100, 200) === (20, 20))
    assert(Proportion.getWidthAndHeight(Proportion.Exact, Method.Stencil, 10, 100, 200) === (20, 20))
  }

  test("Pointless") {
    Proportion.values.foreach(testAny(_))
  }
}
