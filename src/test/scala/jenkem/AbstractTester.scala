package jenkem

import java.io.ByteArrayOutputStream
import java.io.PrintStream

import org.junit.runner.RunWith
import org.scalatest.BeforeAndAfter
import org.scalatest.FunSuite
import org.scalatest.mock.EasyMockSugar

abstract class AbstractTester extends FunSuite with EasyMockSugar with BeforeAndAfter {
  val printStream = System.err

  before {
    System.setErr(new PrintStream(new ByteArrayOutputStream))
  }

  after {
    System.setErr(printStream)
  }

  def testAny(a: Any): Unit = {
    val p = a.asInstanceOf[Product]
    assert(p.productIterator.isInstanceOf[Iterator[Any]])
    val e = intercept[IndexOutOfBoundsException] { p.productElement(0) }
    assert(e.isInstanceOf[IndexOutOfBoundsException])
    val eq = a.asInstanceOf[Equals]
    assert(!eq.canEqual(new Object))
    assert(p.productPrefix.equalsIgnoreCase(a.toString))
    assert(p.productArity === 0)
  }
}
