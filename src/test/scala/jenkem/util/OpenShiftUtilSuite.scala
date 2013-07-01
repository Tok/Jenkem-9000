package jenkem.util

import org.junit.runner.RunWith
import jenkem.AbstractTester
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class OpenShiftUtilSuite extends AbstractTester {
  test("Is Not OpenShift") {
    assert(!OpenShiftUtil.isOnOpenshift)
  }
}
