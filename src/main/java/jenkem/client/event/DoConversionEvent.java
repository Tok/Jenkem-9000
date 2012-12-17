package jenkem.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Triggers a conversion to ASCII.
 */
public class DoConversionEvent extends GwtEvent<DoConversionEventHandler> {
    public static final Type<DoConversionEventHandler> TYPE = new Type<DoConversionEventHandler>();

    @Override
    public final com.google.gwt.event.shared.GwtEvent.Type<DoConversionEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final DoConversionEventHandler handler) {
        handler.onDoConversion(this);
    }
}
