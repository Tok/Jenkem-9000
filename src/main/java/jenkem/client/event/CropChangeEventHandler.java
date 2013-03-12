package jenkem.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * Interface to handle the crop changes.
 */
public interface CropChangeEventHandler extends EventHandler {
    void onChangeCrop(final CropChangeEvent event);
}
