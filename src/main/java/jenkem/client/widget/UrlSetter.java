package jenkem.client.widget;

import jenkem.client.event.DoConversionEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Widget to set URLs.
 */
public class UrlSetter extends Composite {
    private static final int SPACING = 5;
    private final Image busyImage = new Image("/images/busy.gif");
    private final HandlerManager eventBus;
    private final FlexTable flex = new FlexTable();
    private final HorizontalPanel submissionPanel = new HorizontalPanel();
    private final Panel busyPanel = new HorizontalPanel();
    private final Label statusLabel = new Label("Enter URL to an image:");
    private final Button showButton = new Button("Convert Image");
    private final ExtendedTextBox inputTextBox = new ExtendedTextBox();

    public UrlSetter(final HandlerManager eventBus) {
        this.eventBus = eventBus;
        flex.setWidth("800px");

        inputTextBox.setWidth("800px");
        inputTextBox.setFocus(true);
        submissionPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        submissionPanel.setSpacing(SPACING);

        final String link = com.google.gwt.user.client.Window.Location.getParameter("link");
        if (link != null && !link.equals("")) { inputTextBox.setText(link); }
        submissionPanel.add(inputTextBox);
        showButton.setWidth("137px");
        submissionPanel.add(showButton);
        submissionPanel.add(busyPanel);

        bind();
        flex.setWidget(0, 0, statusLabel);
        flex.setWidget(1, 0, submissionPanel);
        initWidget(flex);
    }

    private void bind() {
        inputTextBox.addKeyPressHandler(new KeyPressHandler() {
            @Override public void onKeyPress(final KeyPressEvent event) {
                if (event.getCharCode() == KeyCodes.KEY_ENTER) { replaceUrl(); }
            }});
        showButton.addClickHandler(new ClickHandler() {
            @Override public void onClick(final ClickEvent event) { replaceUrl(); }
        });
    }

    /**
     * Creates a new history event if required.
     */
    public final void replaceUrl() {
        final String currentToken = History.getToken();
        final String currentUrl = inputTextBox.getValue();
        if (!currentToken.endsWith(currentUrl)) {
            History.newItem("main/" + inputTextBox.getValue());
        } else {
            eventBus.fireEvent(new DoConversionEvent(false));
        }
    }

    public final void setUrl(final String imageUrl) {
        inputTextBox.setText(imageUrl);
        inputTextBox.selectAll();
    }

    public final String getUrl() { return inputTextBox.getText(); }
    public final HasClickHandlers getShowButton() { return showButton; }
    public final Panel getBusyPanel() { return busyPanel; }
    public final TextBox getInputTextBox() { return inputTextBox; }
    public final void setStatus(final String status) { statusLabel.setText(status); }

    /**
     * Displays or removes the icon for when the application is busy.
     */
    public final void makeBusy(final boolean isBusy) {
        busyPanel.clear();
        if (isBusy) {
          busyPanel.add(busyImage);
        }
    }
}
