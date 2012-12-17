package jenkem.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Triggers a conversion to ASCII.
 */
public class DoConversionEvent extends GwtEvent<DoConversionEventHandler> {
    public static final Type<DoConversionEventHandler> TYPE = new Type<DoConversionEventHandler>();
    private boolean proxify;

    public DoConversionEvent(final boolean proxify) {
        this.proxify = proxify;
    }

    @Override
    public final com.google.gwt.event.shared.GwtEvent.Type<DoConversionEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final DoConversionEventHandler handler) {
        handler.onDoConversion(this);
    }

    /**
     * If true, the image must be proxyfied before conversion.
     * @return proxify
     */
    public final boolean proxify() {
        return proxify;
    }
}
