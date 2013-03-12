package jenkem.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * This event is triggered reset crops.
 */
public class ResetCropsEvent extends GwtEvent<ResetCropsEventHandler> {
    public static final Type<ResetCropsEventHandler> TYPE = new Type<ResetCropsEventHandler>();

    @Override
    public final com.google.gwt.event.shared.GwtEvent.Type<ResetCropsEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final ResetCropsEventHandler handler) {
        handler.onReset(this);
    }
}
