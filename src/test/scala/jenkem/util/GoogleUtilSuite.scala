package jenkem.util

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.PrivateMethodTester._
import org.scalatest.PrivateMethodTester.PrivateMethod
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class GoogleUtilSuite extends FunSuite {
  test("Make Random Search Term") {
    val makeRandomSearchTerm = PrivateMethod[String]('makeRandomSearchTerm)
    val range = 0 to 100
    def testSearch: Unit = {
      val term = GoogleUtil.invokePrivate(makeRandomSearchTerm())
      assert(term.startsWith("IMG0") || term.startsWith("DSC0"))
      assert(term.length === 8)
    }
    range.foreach(_ => testSearch)
  }

  test("Test Constants") {
    assert(GoogleUtil.searchUrl.contains("google.com"))
    assert(GoogleUtil.searchUrl.contains("images"))
    assert(GoogleUtil.searchUrl.startsWith("http"))
    assert(GoogleUtil.attributes.contains("safe=off"))
    assert(GoogleUtil.encoding.equals("latin1"))
    assert(GoogleUtil.agent.equals("Jenkem/9000"))
  }
}
