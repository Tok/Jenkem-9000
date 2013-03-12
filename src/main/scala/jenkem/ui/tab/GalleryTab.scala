package jenkem.ui.tab

import scala.collection.JavaConversions.asScalaBuffer

import com.vaadin.data.util.BeanItemContainer
import com.vaadin.event.EventRouter
import com.vaadin.server.ExternalResource
import com.vaadin.ui.Link
import com.vaadin.ui.Table
import com.vaadin.ui.VerticalLayout

import jenkem.persistence.PersistenceService
import jenkem.shared.HtmlUtil
import jenkem.shared.data.ImageInfo
import jenkem.util.AwtImageUtil

class GalleryTab(val eventRouter: EventRouter) extends VerticalLayout {
  setCaption("Gallery")
  setSizeFull
  setMargin(true)
  setSpacing(true)

  val table = new Table
  table.setWidth("100%")
  table.setHeight("1000px")
  table.setSelectable(false)
  table.setMultiSelect(false)
  table.setImmediate(true)
  table.setColumnReorderingAllowed(true)
  table.setColumnCollapsingAllowed(true)

  class ImageInfoBean(info: ImageInfo) {
    def getIcon = {
      val link = new Link
      link.setTargetName("_blank")
      val ex = new ExternalResource(HtmlUtil.getHtmlUrl(info.getName))
      link.setResource(ex)
      val buffered = AwtImageUtil.decodeFromBase64(info.getIcon)
      val resource = AwtImageUtil.makeVaadinResource(buffered, info.getName)
      link.setIcon(resource)
      link
    }
    def getName = {
      val ex = new ExternalResource(HtmlUtil.getHtmlUrl(info.getName))
      val link = new Link(info.getName, ex)
      link.setTargetName("_blank")
      link
    }
    def getMethod = info.getMethod
    def getCharacters = info.getCharacters
    def getContrast = info.getContrast
    def getBrightness = info.getBrightness
    def getLines = info.getLines
    def getWidth = info.getLineWidth
    def getCreation = info.getCreation
  }

  table.addContainerProperty("icon", classOf[Link], null)
  table.addContainerProperty("name", classOf[Link], null)
  table.addContainerProperty("method", classOf[String], null)
  table.addContainerProperty("characters", classOf[String], null)
  table.addContainerProperty("contrast", classOf[Integer], null)
  table.addContainerProperty("brightness", classOf[Integer], null)
  table.addContainerProperty("lines", classOf[Integer], null)
  table.addContainerProperty("width", classOf[Integer], null)
  table.addContainerProperty("creation", classOf[String], null)

  val bic = new BeanItemContainer[ImageInfoBean](classOf[ImageInfoBean])
  table.setContainerDataSource(bic)

  table.setVisibleColumns(Array[Object](
      "icon", "name", "method",
      "characters", "contrast", "brightness",
      "lines", "width", "creation"
  ))

  table.setColumnAlignment("icon", Table.Align.CENTER)
  table.setColumnAlignment("contrast", Table.Align.RIGHT)
  table.setColumnAlignment("brightness", Table.Align.RIGHT)
  table.setColumnAlignment("lines", Table.Align.RIGHT)
  table.setColumnAlignment("width", Table.Align.RIGHT)

  table.setColumnHeader("icon", "Icon")
  table.setColumnHeader("name", "Name")
  table.setColumnHeader("method", "Method")
  table.setColumnHeader("characters", "Characters")
  table.setColumnHeader("contrast", "Contrast")
  table.setColumnHeader("brightness", "Brightness")
  table.setColumnHeader("lines", "Lines")
  table.setColumnHeader("width", "Width")
  table.setColumnHeader("creation", "Creation Date")

  addComponent(table)

  update

  def update {
    bic.removeAllItems
    val info: java.util.ArrayList[ImageInfo] = PersistenceService.getAllImageInfo
    info.toList.map(info => bic.addItem(new ImageInfoBean(info)))
  }
}
