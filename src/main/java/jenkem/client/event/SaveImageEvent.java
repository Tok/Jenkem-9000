package jenkem.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Saves the Jenkem Image
 */
public class SaveImageEvent extends GwtEvent<SaveImageEventHandler> {
    public static final Type<SaveImageEventHandler> TYPE = new Type<SaveImageEventHandler>();

    public SaveImageEvent() { }

    @Override
    public final com.google.gwt.event.shared.GwtEvent.Type<SaveImageEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final SaveImageEventHandler handler) {
        handler.onSave(this);
    }
}
