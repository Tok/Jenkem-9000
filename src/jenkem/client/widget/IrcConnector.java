package jenkem.client.widget;

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
    //private final IrcServiceAsync xmppService = GWT.create(IrcService.class);
    private final HorizontalPanel mainPanel = new HorizontalPanel();
    private final FlexTable flex = new FlexTable();
    private final TextBox channelBox = new TextBox();
    private final Button connectButton = new Button("Connect and Send");
    private final Label statusLabel = new Label();

    /**
     * Default constructor.
     */
    public IrcConnector() {
        /*
        connectButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                final String network = "pratchett.freenode.net"; //"irc.freenode.org";
                final String channel = "dsfargeg";
                final String message = "Test test test.";
                xmppService.sendMessage(network, channel, message, new AsyncCallback<String>() {
                    @Override
                    public void onSuccess(final String result) {
                        statusLabel.setText(result);
                    }
                    @Override
                    public void onFailure(final Throwable caught) {
                        statusLabel.setText("Fail sending message." + caught);
                    }
                });
            }
        });
         */
        flex.setWidget(0, 0, channelBox);
        flex.setWidget(1, 0, connectButton);
        flex.setWidget(3, 0, statusLabel);

        mainPanel.add(flex);
        initWidget(mainPanel);
    }

}
