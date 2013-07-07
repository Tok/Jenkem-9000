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
    val gt = new GalleryTab(eventRouter)
    gt.cols.foreach(testAny(_, true))
  }
}
