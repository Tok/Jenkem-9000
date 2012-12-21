package jenkem.client.widget;

import java.util.List;
import jenkem.client.event.SendToIrcEvent;
import jenkem.client.service.IrcService;
import jenkem.client.service.IrcServiceAsync;
import jenkem.shared.BotStatus;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Widget to send messages to IRC.
 */
public class IrcConnector extends Composite {
    private final int REFRESH_INTERVAL = 2000; //poll bot status every two seconds
    private final Timer refreshTimer = new Timer() { @Override public void run() { getBotStatus(); }};
    private final HandlerManager eventBus;
    private final IrcSettings ircSettings = GWT.create(IrcSettings.class);
    private final IrcServiceAsync ircService = GWT.create(IrcService.class);
    private final HorizontalPanel connectionPanel = new HorizontalPanel();
    private final HorizontalPanel controlPanel = new HorizontalPanel();
    private final FlexTable flex = new FlexTable();
    private final TextBox networkBox = new TextBox();
    private final TextBox portBox = new TextBox();
    private final TextBox channelBox = new TextBox();
    private final TextBox nickBox = new TextBox();
    private final Button connectButton = new Button("Connect");
    private final Button disconnectButton = new Button("Disconnect");
    private final Button sendButton = new Button("Send Conversion");
    private final Label statusLabel = new Label();
    private boolean doPoll = true;

    /**
     * Default constructor.
     */
    public IrcConnector(final HandlerManager eventBus) {
        this.eventBus = eventBus;

        flex.setWidth("400px");
        //[Label ][Box   ][Box   ]
        //[Label ][Box           ]
        //[Label ][Box           ]
        //[Button][Button][Button]
        //[Label ][Status        ]
        final Label nLabel = new Label("IRC Network: ");
        nLabel.setWidth("100px");
        flex.setWidget(0, 0, nLabel);
        networkBox.setWidth("236px");
        portBox.setWidth("40px");
        flex.setWidget(0, 1, networkBox);
        flex.setWidget(0, 2, portBox);

        flex.setWidget(1, 0, new Label("Channel: "));
        channelBox.setWidth("290px");
        flex.setWidget(1, 1, channelBox);
        flex.getFlexCellFormatter().setColSpan(1, 1, 2);

        flex.setWidget(2, 0, new Label("Nick: "));
        nickBox.setWidth("290px");
        flex.setWidget(2, 1, nickBox);
        flex.getFlexCellFormatter().setColSpan(2, 1, 2);

        connectButton.setWidth("150px");
        disconnectButton.setWidth("150px");
        connectionPanel.add(connectButton);
        connectionPanel.add(disconnectButton);
        flex.setWidget(3, 1, connectionPanel);
        flex.getFlexCellFormatter().setColSpan(3, 1, 2);

        sendButton.setWidth("300px");
        controlPanel.add(sendButton);
        flex.setWidget(4, 1, controlPanel);
        flex.getFlexCellFormatter().setColSpan(4, 1, 2);

        flex.setWidget(5, 0, new Label("Status: "));
        flex.setWidget(5, 1, statusLabel);
        flex.getFlexCellFormatter().setColSpan(5, 1, 2);

        bind();

        ircService.getBotStatus(new AsyncCallback<BotStatus>() {
            @Override public void onSuccess(final BotStatus botStatus) {
                if (!botStatus.isConnected()) {
                    networkBox.setText(ircSettings.network());
                    portBox.setText(ircSettings.port());
                    channelBox.setText(ircSettings.channel());
                    nickBox.setText(ircSettings.nick());
                }
            }
            @Override public void onFailure(final Throwable caught) {
                statusLabel.setText("Fail getting bot status: " + caught);
            }
        });

        portBox.setEnabled(false); //XXX port-settings disabled
        refreshTimer.scheduleRepeating(REFRESH_INTERVAL);

        initWidget(flex);
    }

    private void bind() {
        connectButton.addClickHandler(new ClickHandler() {
            @Override public void onClick(final ClickEvent event) {
                doPoll = false;
                statusLabel.setText("Connecting...");
                connectButton.setEnabled(false);
                final String network = networkBox.getText();
                final int port = Integer.parseInt(portBox.getText());
                final String channel = channelBox.getText();
                final String nick = nickBox.getText();
                ircService.connect(network, port, channel, nick, new AsyncCallback<String>() {
                    @Override public void onSuccess(final String result) {
                        statusLabel.setText(result);
                        doPoll = true;
                    }
                    @Override public void onFailure(final Throwable caught) {
                        connectButton.setEnabled(true);
                        statusLabel.setText("Fail connecting: " + caught);
                    }
                });
            }});
        disconnectButton.addClickHandler(new ClickHandler() {
            @Override public void onClick(final ClickEvent event) {
                ircService.disconnect(new AsyncCallback<String>() {
                    @Override public void onSuccess(final String result) {
                        statusLabel.setText(result);
                    }
                    @Override public void onFailure(final Throwable caught) {
                        statusLabel.setText("Fail disconnecting: " + caught);
                    }
                });
            }});
        sendButton.addClickHandler(new ClickHandler() {
            @Override public void onClick(final ClickEvent event) {
                doPoll = false;
                sendButton.setEnabled(false);
                eventBus.fireEvent(new SendToIrcEvent());
            }});
    }

    private void getBotStatus() {
        if (doPoll) {
            ircService.getBotStatus(new AsyncCallback<BotStatus>() {
                @Override public void onSuccess(final BotStatus botStatus) {
                    setBotStatus(botStatus);
                }
                @Override public void onFailure(final Throwable caught) {
                    statusLabel.setText("Fail getting bot status: " + caught);
                }
            });
        }
    }

    private void setBotStatus(final BotStatus botStatus) {
        if (botStatus.isConnected()) {
            networkBox.setText(botStatus.getNetwork());
            channelBox.setText(botStatus.getChannel());
            nickBox.setText(botStatus.getName());
        }
        networkBox.setEnabled(!botStatus.isConnected());
        channelBox.setEnabled(!botStatus.isConnected());
        nickBox.setEnabled(!botStatus.isConnected());
        connectButton.setEnabled(!botStatus.isConnected());
        disconnectButton.setEnabled(botStatus.isConnected());
        sendButton.setEnabled(botStatus.isConnected() && !botStatus.isSending());
        if (botStatus.isSending()) {
            statusLabel.setText("Bot is busy...");
        } else {
            statusLabel.setText(botStatus.isConnected() ? "Bot is connected." : "Bot is not connected,");
        }
    }

    /**
     * This method is only called from other classes.
     * To trigger a send here, fire the SendToIrcEvent in the event bus.
     * @param message
     */
    public final void sendMessage(final List<String> message) {
        ircService.getBotStatus(new AsyncCallback<BotStatus>() {
            @Override public void onSuccess(final BotStatus botStatus) {
                setBotStatus(botStatus);
                sendButton.setEnabled(false);
                if (botStatus.isConnected()) {
                    ircService.sendMessage(message, new AsyncCallback<String>() {
                        @Override public void onSuccess(final String result) {
                            statusLabel.setText(result);
                            doPoll = true;
                        }
                        @Override public void onFailure(final Throwable caught) {
                            statusLabel.setText("Fail sending message: " + caught);
                            doPoll = true;
                        }
                    });
                } else {
                    statusLabel.setText("Fail: Bot is not connected.");
                    doPoll = true;
                }
            }
            @Override public void onFailure(final Throwable caught) {
                statusLabel.setText("Fail testing connection status: " + caught);
                doPoll = true;
            }
        });
    }
}
