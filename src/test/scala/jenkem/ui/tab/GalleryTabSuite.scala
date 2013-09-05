/*
 * #%L
 * GalleryTabSuite.scala - Jenkem - Tok - 2012
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
package jenkem.ui.tab

import org.junit.runner.RunWith
import com.vaadin.event.EventRouter
import jenkem.AbstractTester
import jenkem.DbTest
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class GalleryTabSuite extends AbstractTester {
  val eventRouter = new EventRouter

  test("Pointless", DbTest) {
    val gt = new GalleryTab(eventRouter, false)
    gt.cols.foreach(testAny(_, true))
  }
}
