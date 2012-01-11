package jenkem.client;

import jenkem.client.event.CancelledEvent;
import jenkem.client.service.JenkemService;
import jenkem.client.service.JenkemServiceAsync;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Jenkem implements EntryPoint {
	private final JenkemServiceAsync jenkemService = GWT.create(JenkemService.class);
	private final HandlerManager eventBus = new HandlerManager(null);
	private final AppController appViewer = new AppController(jenkemService, eventBus);
	
	public void onModuleLoad() {		
		final Image titleImage = new Image("/images/Jenkem-Title.png");
		titleImage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				eventBus.fireEvent(new CancelledEvent());
			}
		});
		RootPanel.get("title").add(titleImage);
		
		
		RootPanel.get().setStyleName("everything");
		
		appViewer.go(RootPanel.get("content"));
	}
}
