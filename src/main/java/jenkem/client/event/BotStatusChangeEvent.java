package jenkem.client.event;

import jenkem.shared.BotStatus;
import com.google.gwt.event.shared.GwtEvent;

/**
 * This event is bound to the main tab in AppController.
 */
public class BotStatusChangeEvent extends GwtEvent<BotStatusChangeEventHandler> {
    public static final Type<BotStatusChangeEventHandler> TYPE = new Type<BotStatusChangeEventHandler>();
    private BotStatus botStatus;

    public BotStatusChangeEvent(final BotStatus botStatus) {
        this.botStatus = botStatus;
    }

    @Override
    public final com.google.gwt.event.shared.GwtEvent.Type<BotStatusChangeEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final BotStatusChangeEventHandler handler) {
        handler.onBotStatusChanged(this);
    }

    public static Type<BotStatusChangeEventHandler> getType() {
        return TYPE;
    }

    public final BotStatus getBotStatus() {
        return botStatus;
    }
}
