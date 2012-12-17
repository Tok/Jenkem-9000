package jenkem.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * Interface to handle the Conversion
 */
public interface DoConversionEventHandler extends EventHandler {
    void onDoConversion(final DoConversionEvent event);
}
