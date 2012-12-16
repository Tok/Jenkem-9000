package jenkem.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * This event is triggered to send converted images to IRC.
 */
public class SendToIrcEvent extends GwtEvent<SendToIrcEventHandler> {
    public static final Type<SendToIrcEventHandler> TYPE = new Type<SendToIrcEventHandler>();

    @Override
    public final com.google.gwt.event.shared.GwtEvent.Type<SendToIrcEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final SendToIrcEventHandler handler) {
        handler.onSend(this);
    }
}
