/*
 * #%L
 * GoogleUtilSuite.scala - Jenkem - Tok - 2012
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
import org.scalatest.PrivateMethodTester.PrivateMethod
import org.scalatest.PrivateMethodTester.anyRefToInvoker
import jenkem.AbstractTester
import org.scalatest.junit.JUnitRunner
import jenkem.OnlineTest

@RunWith(classOf[JUnitRunner])
class GoogleUtilSuite extends AbstractTester {
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

  test("Get URL From Term", OnlineTest) {
    val someUrl = GoogleUtil.getUrlForTerm("foo")
    assert(someUrl.isInstanceOf[Some[String]])
  }

  test("Test Constants") {
    assert(GoogleUtil.searchUrl.contains("google.com"))
    assert(GoogleUtil.searchUrl.contains("images"))
    assert(GoogleUtil.searchUrl.startsWith("http"))
    assert(GoogleUtil.attributes.contains("safe=off"))
    assert(GoogleUtil.encoding === "latin1")
    assert(GoogleUtil.agent === "Jenkem/9000")
  }
}
