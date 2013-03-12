package jenkem.ui

import com.vaadin.event.EventRouter
import com.vaadin.ui.Button
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.GridLayout
import com.vaadin.ui.Label
import com.vaadin.ui.TextField
import com.vaadin.ui.VerticalLayout

import jenkem.bot.BotStatus
import jenkem.bot.IrcService
import jenkem.event.SendToIrcEvent

class IrcConnector(val eventRouter: EventRouter) extends GridLayout {
  //TODO move defaults to properties file
  //For deployments on OpenShift, the servers must listen on port 8001
  val defaultNetwork = "irc.freenode.net"
  val defaultPort = "8001"
  val defaultChannel = "#Jenkem"
  val defaultNick = "J_"

  setWidth("400px")

  setRows(5)
  setColumns(3)

  //   0       1       2
  //0 [Label ][Box   ][Box   ]
  //1 [Label ][Box           ]
  //2 [Label ][Box           ]
  //3 [Label ][Buttons       ]
  //4 [Label ][Status][Button]

  val networkCaption = new Label("IRC Network: ")
  networkCaption.setWidth("150px")
  val networkBox = new TextField
  networkBox.setWidth("210px")
  val portBox = new TextField
  portBox.setWidth("40px")
  val channelCaption = new Label("Channel: ")
  val channelBox = new TextField
  channelBox.setWidth("250px")
  val nickCaption = new Label("Nick: ")
  val nickBox = new TextField
  nickBox.setWidth("250px")
  val actionCaption = new Label("Actions: ")
  val buttonLayout = new GridLayout
  val connectButton = new Button("Connect")
  val disconnectButton = new Button("Disconnect")
  val sendButton = new Button("Send Conversion")
  val statusCaption = new Label("Status: ")
  val statusLayout = new VerticalLayout
  val statusLabel = new Label("")
  statusLabel.setWidth("250px")
  val refreshButton = new Button("Refresh Bot Status")

  //addComponent(component, column1, row1, column2, row2)
  addComponent(networkCaption, 0, 0)
  addComponent(networkBox, 1, 0)
  addComponent(portBox, 2, 0)
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

  networkBox.setValue(defaultNetwork)
  portBox.setValue(defaultPort)
  portBox.setReadOnly(true)
  channelBox.setValue(defaultChannel)
  nickBox.setValue(defaultNick)
  refresh

  connectButton.addClickListener(new Button.ClickListener {
    override def buttonClick(event: ClickEvent) {
      statusLabel.setValue("Connecting...")
      connectButton.setEnabled(false)
      val network = networkBox.getValue
      val port = Integer.parseInt(portBox.getValue)
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

  def sendToIrc(message: java.util.List[String]) = IrcService.sendMessage(message); refresh
  def refresh = showBotStatus(IrcService.getBotStatus)
  def showBotStatus(botStatus: BotStatus) {
    if (botStatus.isConnected || botStatus.isSending) {
      if (botStatus.isSending) { statusLabel.setValue("Bot is busy...") }
      else { statusLabel.setValue("Bot is connected.") }
      networkBox.setValue(botStatus.network)
      networkBox.setEnabled(false)
      channelBox.setValue(botStatus.channel)
      channelBox.setEnabled(false)
      nickBox.setValue(botStatus.name)
      nickBox.setEnabled(false)
      connectButton.setEnabled(false)
      disconnectButton.setEnabled(true)
      sendButton.setEnabled(!botStatus.isSending)
    } else {
      statusLabel.setValue("Bot is not connected.")
      networkBox.setEnabled(true)
      channelBox.setEnabled(true)
      nickBox.setEnabled(true)
      connectButton.setEnabled(true)
      disconnectButton.setEnabled(false)
      sendButton.setEnabled(false)
    }
  }
}
