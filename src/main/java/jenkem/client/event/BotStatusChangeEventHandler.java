package jenkem.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * Interface to inform the interfaces when the bot changes it's status.
 */
public interface BotStatusChangeEventHandler extends EventHandler {
    void onBotStatusChanged(final BotStatusChangeEvent event);
}
