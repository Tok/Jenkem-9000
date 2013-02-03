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
    private final Panel imagePanel = new HorizontalPanel();
    private final Panel statusPanel = new HorizontalPanel();
    private final HorizontalPanel submissionPanel = new HorizontalPanel();
    private final Panel busyPanel = new HorizontalPanel();
    private final Label statusLabel = new Label("Enter URL to an image:");
    private final Button showButton = new Button("Convert Image");
    private final ExtendedTextBox inputTextBox = new ExtendedTextBox();
    private final CropPanel cropPanel;
    private boolean isReady = false;

    public UrlSetter(final HandlerManager eventBus) {
        this.eventBus = eventBus;
        cropPanel = new CropPanel(eventBus, statusLabel);
        flex.setWidth("800px");
        imagePanel.setWidth("72px");
        imagePanel.setHeight("100px");
        inputTextBox.setWidth("724px");
        showButton.setWidth("128px");

        inputTextBox.setFocus(true);
        final String link = com.google.gwt.user.client.Window.Location.getParameter("link");
        if (link != null && !link.equals("")) { inputTextBox.setText(link); }

        statusPanel.add(statusLabel);
        submissionPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        submissionPanel.setSpacing(SPACING);
        submissionPanel.add(inputTextBox);
        submissionPanel.add(showButton);
        submissionPanel.add(busyPanel);

        bind();
        flex.getFlexCellFormatter().setRowSpan(0, 0, 3);
        flex.setWidget(0, 0, imagePanel);
        flex.setWidget(0, 2, statusPanel);
        flex.setWidget(1, 1, submissionPanel);
        flex.setWidget(2, 1, cropPanel);
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
            if (isReady) {
                eventBus.fireEvent(new DoConversionEvent(false));
            }
        }
    }

    public final void setUrl(final String imageUrl) {
        inputTextBox.setText(imageUrl);
        inputTextBox.selectAll();
    }

    /**
     * Displays or removes the icon for when the application is busy.
     */
    public final void makeBusy(final boolean isBusy) {
        busyPanel.clear();
        if (isBusy) {
          busyPanel.add(busyImage);
        }
    }

    public final String getUrl() { return inputTextBox.getText(); }
    public final HasClickHandlers getShowButton() { return showButton; }
    public final Panel getBusyPanel() { return busyPanel; }
    public final TextBox getInputTextBox() { return inputTextBox; }
    public final void setStatus(final String status) { statusLabel.setText(status); }
    public final int getCrop(final CropPanel.Type type) { return cropPanel.getCrop(type); }

    public final void addImage(final String proxifiedUrl, final int width) {
        imagePanel.clear();
        final Image visibleCopy = new Image(proxifiedUrl);
        visibleCopy.setWidth(width + "px");
        imagePanel.add(visibleCopy);
    }

    public final void setReady() { isReady = true; cropPanel.setReady(); }
}
