package jenkem.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Triggers a crop changes.
 */
public class CropChangeEvent extends GwtEvent<CropChangeEventHandler> {
    public static final Type<CropChangeEventHandler> TYPE = new Type<CropChangeEventHandler>();
    private int xStart;
    private int xEnd;
    private int yStart;
    private int yEnd;

    public CropChangeEvent(final int xStart, final int xEnd,
            final int yStart, final int yEnd) {
        this.xStart = xStart;
        this.xEnd = xEnd;
        this.yStart = yStart;
        this.yEnd = yEnd;
    }

    @Override
    public final com.google.gwt.event.shared.GwtEvent.Type<CropChangeEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final CropChangeEventHandler handler) {
        handler.onChangeCrop(this);
    }

    public final int getXStart() { return xStart; }
    public final int getXEnd() { return xEnd; }
    public final int getYStart() { return yStart; }
    public final int getYEnd() { return yEnd; }
}
