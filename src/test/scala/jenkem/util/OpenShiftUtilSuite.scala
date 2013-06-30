package jenkem.util

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class OpenShiftUtilSuite extends FunSuite {
  test("Is Not OpenShift") {
    assert(!OpenShiftUtil.isOnOpenshift)
  }
}
