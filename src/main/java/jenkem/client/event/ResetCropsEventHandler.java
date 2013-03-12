package jenkem.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * Interface to handle crop resets.
 */
public interface ResetCropsEventHandler extends EventHandler {
    void onReset(final ResetCropsEvent event);
}
