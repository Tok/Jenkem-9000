package jenkem.client;

import jenkem.client.event.CancelledEvent;
import jenkem.client.event.CancelledEventHandler;
import jenkem.client.presenter.GalleryPresenter;
import jenkem.client.presenter.MainPresenter;
import jenkem.client.presenter.Presenter;
import jenkem.client.service.JenkemService;
import jenkem.client.service.JenkemServiceAsync;
import jenkem.client.view.GalleryView;
import jenkem.client.view.MainView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.TabPanel;

public class AppController implements Presenter, ValueChangeHandler<String> {
	private final JenkemServiceAsync jenkemService = GWT.create(JenkemService.class);
	private final HandlerManager eventBus = new HandlerManager(null);
	private HasWidgets container;
	
	private final TabPanel tabPanel = new TabPanel();
	private final MainView mainView = new MainView();
	private final GalleryView galleryView = new GalleryView();
	
	private Presenter mainPresenter;
	private Presenter galleryPresenter;
	
	public AppController() {
		prepareTabs();
		bind();
	}
	
	private void prepareTabs() {		
		tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				int selection = event.getSelectedItem().intValue();
				if(selection == 0) {
					if(!History.getToken().startsWith("main")) {
						History.newItem("main/");
					}
				}
				if(selection == 1) {
					if(!History.getToken().startsWith("gallery")) {	
						History.newItem("gallery/");
					}
				}
			}
		});

//		tabPanel.setAnimationEnabled(true); //XXX
		tabPanel.add(mainView.asWidget(), "Main");
		tabPanel.add(galleryView.asWidget(), "Gallery");
	}
	
	private void bind() {
		History.addValueChangeHandler(this);
		eventBus.addHandler(CancelledEvent.TYPE,
			new CancelledEventHandler() {
				public void onCancelled(CancelledEvent event) {
					doEditTermCancelled();
				}
			}
		);
	}

	private void doEditTermCancelled() {
		History.newItem("main/");
	}

	public void go(final HasWidgets container) {
		this.container = container;
		if ("".equals(History.getToken())) {
			History.newItem("main/");
		} else {
			History.fireCurrentHistoryState();
		}
	}
	
	public void onValueChange(final ValueChangeEvent<String> event) {
		final String token = event.getValue();
		if (token.startsWith("main/")) {
			tabPanel.selectTab(0);
			mainPresenter = new MainPresenter(jenkemService, eventBus, tabPanel, mainView);
			mainPresenter.go(container);
		} else if (token.startsWith("gallery/")) {
			tabPanel.selectTab(1);
			galleryPresenter = new GalleryPresenter(jenkemService, eventBus, tabPanel, galleryView);
			galleryPresenter.go(container);
		} else {
			tabPanel.selectTab(0);
			mainPresenter = new MainPresenter(jenkemService, eventBus, tabPanel, mainView);
			mainPresenter.go(container);			
		}
	}
	
}
