package jenkem.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * Interface to handle the CancelledEvent
 */
public interface CancelledEventHandler extends EventHandler {
    void onCancelled(final CancelledEvent event);
}
