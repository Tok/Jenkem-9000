package jenkem.event

import java.util.EventObject
import org.junit.runner.RunWith
import jenkem.AbstractTester
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class EventSuite extends AbstractTester {
  test("Do Conversion Event") {
    val falseFalseEvent = new DoConversionEvent(false, false)
    assert(falseFalseEvent.isInstanceOf[DoConversionEvent])
    assert(falseFalseEvent.isInstanceOf[EventObject])
    val falseTrueEvent = new DoConversionEvent(false, true)
    assert(falseTrueEvent.isInstanceOf[DoConversionEvent])
    assert(falseTrueEvent.isInstanceOf[EventObject])
    val trueFalseEvent = new DoConversionEvent(true, false)
    assert(trueFalseEvent.isInstanceOf[DoConversionEvent])
    assert(trueFalseEvent.isInstanceOf[EventObject])
    val trueTrueEvent = new DoConversionEvent(true, true)
    assert(trueTrueEvent.isInstanceOf[DoConversionEvent])
    assert(trueTrueEvent.isInstanceOf[EventObject])
  }

  test("Save Image Event") {
    val event = new SaveImageEvent
    assert(event.isInstanceOf[SaveImageEvent])
    assert(event.isInstanceOf[EventObject])
  }

  test("Send To IRC Event") {
    val event = new SendToIrcEvent
    assert(event.isInstanceOf[SendToIrcEvent])
    assert(event.isInstanceOf[EventObject])
  }
}
