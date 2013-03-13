package jenkem.ui

import scala.collection.immutable.ListMap

import com.vaadin.event.EventRouter
import com.vaadin.server.Page
import com.vaadin.ui.TabSheet
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent

import jenkem.event.SaveImageEvent
import jenkem.ui.tab.GalleryTab
import jenkem.ui.tab.InfoTab
import jenkem.ui.tab.MainTab

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
          Option(tab) match {
            case Some(tab) =>
              if (tab.getCaption.toLowerCase.equals("main") && !mainTab.hasLink) {
                page.setUriFragment(tab.getCaption.toLowerCase + "/" + defaultUrl)
                mainTab.setLink(defaultUrl)
              } else {
                page.setUriFragment(tab.getCaption.toLowerCase)
              }
            case None => { }
          }
        }
      }
    })
    tabs.foreach((t) => tabSheet.addTab(t._2, t._2.getCaption))
    tabSheet
  }

  def selectTab(frag: String) {
    def selectMainWithDefault() {
      tabSheet.setSelectedTab(mainTab)
      mainTab.setLink(defaultUrl)
    }
    Option(frag) match {
      case Some(frag) =>
        val split = frag.split("/", 2)
        tabs.get(split(0)) match {
          case None => selectMainWithDefault
          case Some(tab) =>
            tabSheet.setSelectedTab(tab)
            if (tab.equals(mainTab)) {
              if (split.length > 1) { mainTab.setLink(split(1)) }
              else { mainTab.setLink(defaultUrl) }
            }
        }
      case None => selectMainWithDefault
    }
  }

  eventRouter.addListener(classOf[SaveImageEvent],
    new {
      def save {
        mainTab.saveImage
        galleryTab.update
      }
    }, "save")
}
