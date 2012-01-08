package jenkem.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class CancelledEvent extends GwtEvent<CancelledEventHandler>{
  public static Type<CancelledEventHandler> TYPE = new Type<CancelledEventHandler>();
  
  @Override
  public Type<CancelledEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(final CancelledEventHandler handler) {
    handler.onCancelled(this);
  }
}
