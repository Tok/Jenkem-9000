package jenkem.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * Interface to handle the SentToIrcEvent
 */
public interface SendToIrcEventHandler extends EventHandler {
    void onSend(final SendToIrcEvent event);
}
