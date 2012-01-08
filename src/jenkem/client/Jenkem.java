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
	public void onModuleLoad() {
		final JenkemServiceAsync rpcService = GWT.create(JenkemService.class);
		final HandlerManager eventBus = new HandlerManager(null);
		final AppController appViewer = new AppController(rpcService, eventBus);

		Image titleImage = new Image("/images/Jenkem-Title.png");
		titleImage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				eventBus.fireEvent(new CancelledEvent());
			}
		});
		RootPanel.get("title").add(titleImage);
		
		appViewer.go(RootPanel.get("content"));
	}
}
