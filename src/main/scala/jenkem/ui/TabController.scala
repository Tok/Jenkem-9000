package jenkem.ui

import scala.collection.immutable.ListMap
import com.vaadin.server.Page
import com.vaadin.ui.TabSheet
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent
import jenkem.tab.GalleryTab
import jenkem.tab.InfoTab
import jenkem.tab.MainTab
import com.vaadin.event.EventRouter
import jenkem.event.SaveImageEvent

class TabController(val eventRouter: EventRouter) {
  var isReady = false
  val defaultUrl = "http://upload.wikimedia.org/wikipedia/commons/0/03/RGB_Colorcube_Corner_White.png"
  val tabSheet = new TabSheet
  val mainTab = new MainTab(eventRouter)
  val galleryTab = new GalleryTab(eventRouter)
  val infoTab = new InfoTab
  val tabs = ListMap(
    mainTab.getCaption.toLowerCase -> mainTab,
    galleryTab.getCaption.toLowerCase -> galleryTab,
    infoTab.getCaption.toLowerCase -> infoTab)

  def getTabSheet(page: Page): TabSheet = {
    tabSheet.setWidth("1024px")
    tabSheet.addSelectedTabChangeListener(new TabSheet.SelectedTabChangeListener() {
      override def selectedTabChange(event: SelectedTabChangeEvent) {
        if (isReady) {
          val tabsheet = event.getTabSheet
          val tab = tabsheet.getTab(tabsheet.getSelectedTab)
          if (tab != null) { page.setUriFragment(tab.getCaption.toLowerCase) }
        }
      }
    })
    tabs.foreach((t) => tabSheet.addTab(t._2, t._2.getCaption))
    tabSheet
  }

  def selectTab(frag: String) {
    if (frag == null) { selectMainWithDefault }
    else {
      val split = frag.split("/", 2)
      tabs.get(split(0)) match {
        case None => selectMainWithDefault
        case Some(tab) =>
          tabSheet.setSelectedTab(tab)
          if (tab.equals(mainTab)) {
            if (split.length > 1) mainTab.setLink(split(1))
            else mainTab.setLink(defaultUrl)
          }
      }
    }
    def selectMainWithDefault() {
      tabSheet.setSelectedTab(mainTab)
      mainTab.setLink(defaultUrl)
    }
  }

  eventRouter.addListener(classOf[SaveImageEvent],
    new { def save {
      mainTab.saveImage
      galleryTab.update
    }}, "save")

}
