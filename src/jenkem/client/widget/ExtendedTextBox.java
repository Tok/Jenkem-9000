package jenkem.client.widget;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Extended GWT TextBox that reacts on the paste Event.
 */
public class ExtendedTextBox extends TextBox {

    /**
     * Default constructor.
     */
    public ExtendedTextBox() {
        super();
        sinkEvents(Event.ONPASTE);
    }

    /**
     * Fires ValueChangeEvent on paste.
     */
    @Override
    public final void onBrowserEvent(final Event event) {
        super.onBrowserEvent(event);
        if (DOM.eventGetType(event) == Event.ONPASTE) {
            ValueChangeEvent.fire(ExtendedTextBox.this, getText());
        }
    }
}
