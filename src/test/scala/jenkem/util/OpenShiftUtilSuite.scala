package jenkem.util

import org.junit.runner.RunWith
import jenkem.AbstractTester
import org.scalatest.junit.JUnitRunner
import java.util.Collections
import java.lang.reflect.Field

@RunWith(classOf[JUnitRunner])
class OpenShiftUtilSuite extends AbstractTester {
  val osKey = "OPENSHIFT_APP_DNS"

  test("Is Not OpenShift") {
    assert(!OpenShiftUtil.isOnOpenshift)
  }

  test("Is OpenShift") {
    putKey(osKey, "testName")
    assert(OpenShiftUtil.isOnOpenshift)
  }

  test("Is Null") {
    putKey(osKey, None.orNull)
    assert(!OpenShiftUtil.isOnOpenshift)
  }

  test("Is Empty") {
    putKey(osKey, "")
    assert(!OpenShiftUtil.isOnOpenshift)
  }

  def putKey(key: String, value: String): Unit = {
    val processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment")
    val theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment")
    theEnvironmentField.setAccessible(true)
    val env = theEnvironmentField.get(None.orNull).asInstanceOf[java.util.Map[String, String]]
    env.clear
    val theCaseInsensitiveEnvironmentField = processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment")
    theCaseInsensitiveEnvironmentField.setAccessible(true)
    val cienv = theCaseInsensitiveEnvironmentField.get(None.orNull).asInstanceOf[java.util.Map[String, String]]
    cienv.clear
    cienv.putAll(env)
    cienv.put(key, value)
  }
}
