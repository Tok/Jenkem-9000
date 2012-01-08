package jenkem.client.widget;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.TextBox;

public class ExtendedTextBox extends TextBox {
	public ExtendedTextBox() {
		super();
		sinkEvents(Event.ONPASTE);
	}

	@Override
	public void onBrowserEvent(final Event event) {
		super.onBrowserEvent(event);
		if (DOM.eventGetType(event) == Event.ONPASTE) {
			ValueChangeEvent.fire(ExtendedTextBox.this, getText());
		}
	}
}
