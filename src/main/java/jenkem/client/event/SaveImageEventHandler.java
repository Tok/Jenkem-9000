package jenkem.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * Interface to handle the Image saving
 */
public interface SaveImageEventHandler extends EventHandler {
    void onSave(final SaveImageEvent event);
}
