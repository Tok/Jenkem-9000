package jenkem.ui.tab

import scala.Array.canBuildFrom
import com.vaadin.data.util.BeanItemContainer
import com.vaadin.event.EventRouter
import com.vaadin.server.ExternalResource
import com.vaadin.ui.Link
import com.vaadin.ui.Table
import com.vaadin.ui.Table.Align
import com.vaadin.ui.VerticalLayout
import javax.jdo.annotations.PersistenceCapable
import jenkem.persistence.PersistenceService
import jenkem.persistence.data.ImageInfo
import jenkem.util.AwtImageUtil
import jenkem.util.HtmlUtil
import jenkem.ui.Notifications
import com.vaadin.server.Page

class GalleryTab(val eventRouter: EventRouter) extends VerticalLayout {
  setCaption("Gallery")
  setSizeFull
  setMargin(true)
  setSpacing(true)

  sealed abstract class Column(val name: String, val c: Class[_], val align: Align)
  case object ICON extends Column("Icon", classOf[Link], Table.Align.CENTER)
  case object NAME extends Column("Name", classOf[Link], Table.Align.LEFT)
  case object METHOD extends Column("Method", classOf[String], Table.Align.LEFT)
  case object CHARS extends Column("Characters", classOf[String], Table.Align.LEFT)
  case object CONT extends Column("Contrast", classOf[Int], Table.Align.RIGHT)
  case object BRIGHT extends Column("Brightness", classOf[Int], Table.Align.RIGHT)
  case object LINES extends Column("Lines", classOf[Int], Table.Align.RIGHT)
  case object WIDTH extends Column("Width", classOf[Int], Table.Align.RIGHT)
  case object CREATION extends Column("Creation", classOf[String], Table.Align.LEFT)
  val cols = Array(ICON, NAME, METHOD, CHARS, CONT, BRIGHT, LINES, WIDTH, CREATION)
  val names: Array[Object] = cols.map(_.name.toLowerCase)

  val table = new Table
  table.setWidth("100%")
  table.setHeight("1000px")
  table.setSelectable(false)
  table.setMultiSelect(false)
  table.setImmediate(true)
  table.setColumnReorderingAllowed(true)
  table.setColumnCollapsingAllowed(true)

  class ImageInfoBean(info: ImageInfo) {
    def getIcon: Link = {
      val link = new Link
      link.setTargetName("_blank")
      val ex = new ExternalResource(HtmlUtil.getHtmlUrl(info.name))
      link.setResource(ex)
      val buffered = AwtImageUtil.decodeFromBase64(info.icon)
      val resource = AwtImageUtil.makeVaadinResource(buffered, info.name)
      link.setIcon(resource)
      link
    }
    def getName: Link = {
      val ex = new ExternalResource(HtmlUtil.getHtmlUrl(info.name))
      val link = new Link(info.name, ex)
      link.setTargetName("_blank")
      link
    }
    def getMethod: String = info.method
    def getCharacters: String = info.characters
    def getContrast: Integer = info.contrast
    def getBrightness: Integer = info.brightness
    def getLines: Integer = info.lines
    def getWidth: Integer = info.lineWidth
    def getCreation: String = info.creation
  }

  cols.foreach(col => table.addContainerProperty(col.name.toLowerCase, col.c, classOf[String]))
  val bic = new BeanItemContainer[ImageInfoBean](classOf[ImageInfoBean])
  table.setContainerDataSource(bic)
  table.setVisibleColumns(names)
  cols.foreach(col => table.setColumnAlignment(col.name.toLowerCase, col.align))
  cols.foreach(col => table.setColumnHeader(col.name.toLowerCase, col.name))

  addComponent(table)

  update

  def update: Unit = {
    bic.removeAllItems
    PersistenceService.getAllImageInfo match {
      case Some(info) => info.foreach(info => bic.addItem(new ImageInfoBean(info)))
      case None => { Notifications.showDbNotConnected(Page.getCurrent) }
    }
  }
}
