/*
 * #%L
 * EventSuite.scala - Jenkem - Tok - 2012
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
package jenkem.event

import java.util.EventObject
import org.junit.runner.RunWith
import jenkem.AbstractTester
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class EventSuite extends AbstractTester {
  test("Do Conversion Event") {
    def testDoConversionEvent(event: DoConversionEvent): Unit = {
      assert(event.isInstanceOf[DoConversionEvent])
      assert(event.isInstanceOf[EventObject])
      assert(event.getSource.isInstanceOf[Object])
      assert(event.toString === "jenkem.event.DoConversionEvent[source=()]")
    }
    val falseFalseEvent = new DoConversionEvent(false, false)
    assert(!falseFalseEvent.prepareImage)
    assert(!falseFalseEvent.resize)
    testDoConversionEvent(falseFalseEvent)
    val falseTrueEvent = new DoConversionEvent(false, true)
    assert(!falseTrueEvent.prepareImage)
    assert(falseTrueEvent.resize)
    testDoConversionEvent(falseTrueEvent)
    val trueFalseEvent = new DoConversionEvent(true, false)
    assert(trueFalseEvent.prepareImage)
    assert(!trueFalseEvent.resize)
    testDoConversionEvent(trueFalseEvent)
    val trueTrueEvent = new DoConversionEvent(true, true)
    assert(trueTrueEvent.prepareImage)
    assert(trueTrueEvent.resize)
    testDoConversionEvent(trueTrueEvent)
  }

  test("Save Image Event") {
    val event = new SaveImageEvent
    assert(event.isInstanceOf[SaveImageEvent])
    assert(event.isInstanceOf[EventObject])
    assert(event.getSource.isInstanceOf[Object])
    assert(event.toString === "jenkem.event.SaveImageEvent[source=()]")
  }

  test("Send To IRC Event") {
    val event = new SendToIrcEvent
    assert(event.isInstanceOf[SendToIrcEvent])
    assert(event.isInstanceOf[EventObject])
    assert(event.getSource.isInstanceOf[Object])
    assert(event.toString === "jenkem.event.SendToIrcEvent[source=()]")
  }
}
