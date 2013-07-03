package jenkem.ui.tab

import org.junit.runner.RunWith
import com.vaadin.event.EventRouter
import jenkem.AbstractTester
import jenkem.DbTest
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class GalleryTabSuite extends AbstractTester {
  val eventRouter = new EventRouter
  var gt: GalleryTab = None.orNull

  test("Init", DbTest) {
    gt = new GalleryTab(eventRouter)
  }

  test("Pointless") {
    gt.cols.foreach(testAny(_, true))
  }
}
