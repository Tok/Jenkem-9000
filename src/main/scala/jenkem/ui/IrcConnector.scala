package jenkem.ui

import com.vaadin.event.EventRouter
import com.vaadin.server.Page
import com.vaadin.ui.Button
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.GridLayout
import com.vaadin.ui.Label
import com.vaadin.ui.TextField
import com.vaadin.ui.VerticalLayout
import jenkem.bot.BotStatus
import jenkem.bot.IrcService
import jenkem.event.SendToIrcEvent
import com.vaadin.ui.ComboBox
import jenkem.util.OpenShiftUtil
import com.vaadin.data.Property.ValueChangeListener
import com.vaadin.data.Property
import com.vaadin.data.Property.ValueChangeEvent

class IrcConnector(val eventRouter: EventRouter) extends GridLayout {
  val freenodeServer = "irc.freenode.net"
  val freenodePort = "8001"
  val efnetServer = "efnet.xs4all.nl" //"irc.efnet.org"
  val efnetPort = "6669"

  val networkCombo = createCombo(freenodeServer)
  val portCombo = createCombo(freenodePort)

  lockCombo(networkCombo, false)
  lockCombo(portCombo, false)
  if (OpenShiftUtil.isOnOpenshift) {
    portCombo.setReadOnly(true)
  } else {
    //more options if application is not running on OpenShift
    networkCombo.addItem(efnetServer)
    portCombo.addItem(efnetPort)
  }

  networkCombo.addValueChangeListener(new Property.ValueChangeListener {
    override def valueChange(event: ValueChangeEvent) {
      if (networkCombo.getValue.toString.equalsIgnoreCase(freenodeServer)) {
        portCombo.select(freenodePort)
      } else if (networkCombo.getValue.toString.equalsIgnoreCase(efnetServer)) {
        portCombo.select(efnetPort)
      }
    }
  })

  val defaultChannel = "#Jenkem"
  val defaultNick = "J_"

  val width = "400px"
  val compWidth = "250px"

  setWidth(width)

  setRows(5)
  setColumns(3)

  //   0       1       2
  //0 [Label ][ComboBox][ComboBox]
  //1 [Label ][Box               ]
  //2 [Label ][Box               ]
  //3 [Label ][Buttons           ]
  //4 [Label ][Status  ][Button   ]

  val networkCaption = new Label("IRC Network: ")
  networkCaption.setWidth("150px")
  networkCombo.setWidth("180px")
  portCombo.setWidth("70px")
  val channelCaption = new Label("Channel: ")
  val channelBox = new TextField
  channelBox.setWidth(compWidth)
  val nickCaption = new Label("Nick: ")
  val nickBox = new TextField
  nickBox.setWidth(compWidth)
  val actionCaption = new Label("Actions: ")
  val buttonLayout = new GridLayout
  val connectButton = new Button("Connect")
  val disconnectButton = new Button("Disconnect")
  val sendButton = new Button("Send Conversion")
  val statusCaption = new Label("Status: ")
  val statusLayout = new VerticalLayout
  val statusLabel = new Label("")
  statusLabel.setWidth(compWidth)
  val refreshButton = new Button("Refresh Bot Status")

  //addComponent(component, column1, row1, column2, row2)
  addComponent(networkCaption, 0, 0)
  addComponent(networkCombo, 1, 0)
  addComponent(portCombo, 2, 0)
  addComponent(channelCaption, 0, 1)
  addComponent(channelBox, 1, 1, 2, 1)
  addComponent(nickCaption, 0, 2)
  addComponent(nickBox, 1, 2, 2, 2)
  addComponent(actionCaption, 0, 3)
  addComponent(buttonLayout, 1, 3, 2, 3)
  addComponent(statusCaption, 0, 4)
  addComponent(statusLayout, 1, 4, 2, 4)
  buttonLayout.setRows(2)
  buttonLayout.setColumns(2)
  buttonLayout.addComponent(connectButton, 0, 0)
  buttonLayout.addComponent(disconnectButton, 1, 0)
  buttonLayout.addComponent(sendButton, 0, 1, 1, 1)
  statusLayout.addComponent(statusLabel)
  statusLayout.addComponent(refreshButton)

  channelBox.setValue(defaultChannel)
  nickBox.setValue(defaultNick)
  refresh

  private def createCombo(server: String): ComboBox = {
    val combo = new ComboBox
    combo.addItem(server)
    combo.select(server)
    combo.setNullSelectionAllowed(false)
    combo.setImmediate(true)
    combo
  }

  private def lockCombo(combo: ComboBox, value: Boolean) {
    combo.setTextInputAllowed(!value)
    combo.setNewItemsAllowed(!value)
  }

  connectButton.addClickListener(new Button.ClickListener {
    override def buttonClick(event: ClickEvent) {
      Notifications.showConnectToIrc(Page.getCurrent)
      connectButton.setEnabled(false)
      val network = networkCombo.getValue.toString
      val port = Integer.parseInt(portCombo.getValue.toString)
      val channel = channelBox.getValue
      val nick = nickBox.getValue
      val message = IrcService.connect(network, port, channel, nick)
      statusLabel.setValue(message)
    }
  })

  disconnectButton.addClickListener(new Button.ClickListener {
    override def buttonClick(event: ClickEvent) {
      disconnectButton.setEnabled(false)
      statusLabel.setValue(IrcService.disconnect)
    }
  })

  sendButton.addClickListener(new Button.ClickListener {
    override def buttonClick(event: ClickEvent) {
      sendButton.setEnabled(false)
      statusLabel.setValue("Sending...")
      eventRouter.fireEvent(new SendToIrcEvent)
    }
  })

  refreshButton.addClickListener(new Button.ClickListener {
    override def buttonClick(event: ClickEvent) { refresh }
  })

  def sendToIrc(message: java.util.List[String]) {
    IrcService.sendMessage(message)
    Notifications.showPlayToIrc(Page.getCurrent)
    refresh
  }

  def refresh { showBotStatus(IrcService.getBotStatus) }

  private def showBotStatus(botStatus: BotStatus) {
    if (botStatus.isConnected || botStatus.isSending) {
      if (botStatus.isSending) { statusLabel.setValue("Bot is busy...") }
      else { statusLabel.setValue("Bot is connected.") }
      networkCombo.setValue(botStatus.network)
      networkCombo.setEnabled(false)
      channelBox.setValue(botStatus.channel)
      channelBox.setEnabled(false)
      nickBox.setValue(botStatus.name)
      nickBox.setEnabled(false)
      connectButton.setEnabled(false)
      disconnectButton.setEnabled(true)
      sendButton.setEnabled(!botStatus.isSending)
    } else {
      statusLabel.setValue("Bot is not connected.")
      networkCombo.setEnabled(true)
      channelBox.setEnabled(true)
      nickBox.setEnabled(true)
      connectButton.setEnabled(true)
      disconnectButton.setEnabled(false)
      sendButton.setEnabled(false)
    }
  }
}
