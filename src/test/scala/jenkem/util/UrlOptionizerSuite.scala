/*
 * #%L
 * UrlOptionizerSuite.scala - Jenkem - Tok - 2012
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

@RunWith(classOf[JUnitRunner])
class UrlOptionizerSuite extends AbstractTester {
  test("Extract valid") {
    val urlString = "http://host.com:80/path"
    val url = UrlOptionizer.extract(urlString)
    assert(url.get.isInstanceOf[java.net.URL])
    assert(url.get.toString === urlString)
  }

  test("Extract valid without port") {
    val urlString = "http://host.com/path"
    val url = UrlOptionizer.extract(urlString)
    assert(url.get.isInstanceOf[java.net.URL])
    assert(url.get.toString === urlString)
  }

  test("Try to extract invalid") {
    assert(UrlOptionizer.extract("ht--tp://host.com:1111/path") === None)
    assert(UrlOptionizer.extract("http://host.com:----/path") === None)
  }
}
