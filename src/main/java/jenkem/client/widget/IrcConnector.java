package jenkem.client.widget;

import java.util.List;
import jenkem.client.event.SendToIrcEvent;
import jenkem.client.service.IrcService;
import jenkem.client.service.IrcServiceAsync;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
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
    private final HandlerManager eventBus;
    private final IrcSettings ircSettings = GWT.create(IrcSettings.class);
    private final IrcServiceAsync ircService = GWT.create(IrcService.class);
    private final HorizontalPanel mainPanel = new HorizontalPanel();
    private final FlexTable flex = new FlexTable();
    private final TextBox networkBox = new TextBox();
    private final TextBox portBox = new TextBox();
    private final TextBox channelBox = new TextBox();
    private final TextBox nickBox = new TextBox();
    private final Button connectButton = new Button("Connect");
    private final Button disconnectButton = new Button("Disconnect");
    private final Button sendButton = new Button("Send Conversion");
    private final Button updateLogButton = new Button("Update Log"); //TODO remove
    private final Label statusLabel = new Label();

    /**
     * Default constructor.
     */
    public IrcConnector(final HandlerManager eventBus) {
        this.eventBus = eventBus;

        networkBox.setText(ircSettings.network());
        portBox.setText(ircSettings.port());
        portBox.setEnabled(false); //XXX port-settings disabled
        channelBox.setText(ircSettings.channel());
        nickBox.setText(ircSettings.nick());

        flex.setWidth("400px");
        //[Label ][Box   ][Box   ]
        //[Label ][Box           ]
        //[Label ][Box           ]
        //[Button][Button][Button]
        //[Label ][Status        ]
        flex.setWidget(0, 0, new Label("IRC Network: "));
        networkBox.setWidth("290px");
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
        connectButton.setWidth("100px");
        flex.setWidget(3, 0, connectButton);
        disconnectButton.setWidth("100px");
        flex.setWidget(3, 1, disconnectButton);
        sendButton.setWidth("200px");
        flex.setWidget(3, 2, sendButton);

        flex.setWidget(4, 0, updateLogButton);
        flex.getFlexCellFormatter().setColSpan(4, 0, 3);

        flex.setWidget(5, 0, new Label("Status: "));
        flex.setWidget(5, 1, statusLabel);
        flex.getFlexCellFormatter().setColSpan(5, 1, 2);

        bind();
        ircService.isBotConnected(new AsyncCallback<Boolean>() {
            @Override public void onSuccess(final Boolean result) {
                setConnectionState(result);
            }
            @Override public void onFailure(final Throwable caught) {
                statusLabel.setText("Fail getting bot state: " + caught);
            }
        });

        mainPanel.add(flex);
        initWidget(mainPanel);
    }

    private void bind() {
        connectButton.addClickHandler(new ClickHandler() {
            @Override public void onClick(final ClickEvent event) {
                final String network = networkBox.getText();
                final int port = Integer.parseInt(portBox.getText());
                final String channel = channelBox.getText();
                final String nick = nickBox.getText();
                ircService.connect(network, port, channel, nick, new AsyncCallback<String>() {
                    @Override public void onSuccess(final String result) {
                        statusLabel.setText(result);
                        setConnectionState(true);
                    }
                    @Override public void onFailure(final Throwable caught) {
                        statusLabel.setText("Fail connecting: " + caught);
                    }
                });
            }});
        disconnectButton.addClickHandler(new ClickHandler() {
            @Override public void onClick(final ClickEvent event) {
                ircService.disconnect(new AsyncCallback<String>() {
                    @Override public void onSuccess(final String result) {
                        statusLabel.setText(result);
                        setConnectionState(false);
                    }
                    @Override public void onFailure(final Throwable caught) {
                        statusLabel.setText("Fail disconnecting: " + caught);
                    }
                });
            }});
        sendButton.addClickHandler(new ClickHandler() {
            @Override public void onClick(final ClickEvent event) {
                eventBus.fireEvent(new SendToIrcEvent());
            }});
        updateLogButton.addClickHandler(new ClickHandler() {
            @Override public void onClick(final ClickEvent event) {
                ircService.getLog(new AsyncCallback<String>() {
                    @Override
                    public void onSuccess(final String result) {
                        statusLabel.setText(result);
                    }
                    @Override
                    public void onFailure(final Throwable caught) {
                        statusLabel.setText("Fail updating log: " + caught);
                    }
                });
            }});
    }

    private void setConnectionState(final boolean isConnected) {
        networkBox.setEnabled(!isConnected);
        channelBox.setEnabled(!isConnected);
        nickBox.setEnabled(!isConnected);
        connectButton.setEnabled(!isConnected);
        disconnectButton.setEnabled(isConnected);
        sendButton.setEnabled(isConnected);
    }

    public final void sendMessage(final List<String> message) {
        ircService.isBotConnected(new AsyncCallback<Boolean>() {
            @Override public void onSuccess(final Boolean isConnected) {
                setConnectionState(isConnected);
                if (isConnected) {
                    ircService.sendMessage(message, new AsyncCallback<String>() {
                        @Override public void onSuccess(final String result) {
                            statusLabel.setText(result);
                        }
                        @Override public void onFailure(final Throwable caught) {
                            statusLabel.setText("Fail sending message: " + caught);
                        }
                    });
                } else {
                    statusLabel.setText("Fail: Bot is not connected.");
                }
            }
            @Override public void onFailure(final Throwable caught) {
                statusLabel.setText("Fail testing connection status: " + caught);
            }
        });
    }
}
