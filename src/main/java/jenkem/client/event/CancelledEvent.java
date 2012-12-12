package jenkem.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * This event is bound to the main tab in AppController.
 */
public class CancelledEvent extends GwtEvent<CancelledEventHandler> {
    public static final Type<CancelledEventHandler> TYPE = new Type<CancelledEventHandler>();

    @Override
    public final com.google.gwt.event.shared.GwtEvent.Type<CancelledEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final CancelledEventHandler handler) {
        handler.onCancelled(this);
    }
}
