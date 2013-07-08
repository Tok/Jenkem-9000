/*
 * #%L
 * OpenShiftUtilSuite.scala - Jenkem - Tok - 2012
 * %%
 * Copyright (C) 2012 - 2013 Lukas Steiger
 *                 <lsteiger4@gmail.com>
 * %%
 * This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar.
 * See http://www.wtfpl.net/ for more details.
 * #L%
 */
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
