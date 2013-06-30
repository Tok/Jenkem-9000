package jenkem

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import com.vaadin.ui.UI

@RunWith(classOf[JUnitRunner])
class JenkemInitSuite extends FunSuite {
  test("Init Test") {
    val ji = new JenkemInit
    assert(ji.isInstanceOf[JenkemInit])
    assert(ji.isInstanceOf[UI])
  }
}
