package jenkem

import com.vaadin.annotations.Theme
import com.vaadin.event.EventRouter
import com.vaadin.server.Page
import com.vaadin.server.ThemeResource
import com.vaadin.server.VaadinRequest
import com.vaadin.shared.ui.label.ContentMode
import com.vaadin.ui.Alignment
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Image
import com.vaadin.ui.Label
import com.vaadin.ui.UI
import com.vaadin.ui.VerticalLayout

import jenkem.ui.TabController

@Theme("jenkemtheme")
class JenkemInit extends UI {
  override def init(request: VaadinRequest): Unit = {
    val titleLayout = new HorizontalLayout
    val layout = new VerticalLayout
    layout.setSpacing(true)

    val eventRouter = new EventRouter

    val asciiLabel = new Label(
      "      __           _                             ________  _______  _______  _______ \n"
        + "     |  |         | |                           /   __   \\/   _   \\/   _   \\/   _   \\ \n"
        + "     |  |___ _ ___| | _ ___ _ __ __    _____   (   (__\\   \\  / \\   \\  / \\   \\  / \\   \\ \n"
        + " _   |  | _ \\ '_  \\ |/ / _ \\ '  '  \\  (_____)   \\______    )(   )   )(   )   )(   )   ) \n"
        + "/ \\__|  | __/ | | |   (  __/ || || |            ______/   /  \\_/   /  \\_/   /  \\_/   / \n"
        + "\\______/\\___)_| |_|_|\\_\\___)_||_||_|           (_________/\\_______/\\_______/\\_______/ \n\n")
    asciiLabel.setContentMode(ContentMode.PREFORMATTED)
    asciiLabel.setStyleName("asciiLabel")
    titleLayout.addComponent(asciiLabel)
    titleLayout.setComponentAlignment(asciiLabel, Alignment.MIDDLE_CENTER)

    val image = new Image(null, new ThemeResource("j.png"))

    titleLayout.addComponent(image)
    titleLayout.setComponentAlignment(image, Alignment.MIDDLE_CENTER)

    layout.addComponent(titleLayout)
    layout.setComponentAlignment(titleLayout, Alignment.TOP_CENTER)

    val tc = new TabController(eventRouter)

    val tabSheet = tc.getTabSheet(Page.getCurrent)
    layout.addComponent(tabSheet)
    layout.setComponentAlignment(tabSheet, Alignment.MIDDLE_CENTER)
    setContent(layout)

    val frag = Page.getCurrent.getUriFragment
    tc.selectTab(frag)
    tc.isReady = true
  }
}
