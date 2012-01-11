package jenkem.client;

import jenkem.client.event.CancelledEvent;
import jenkem.client.event.CancelledEventHandler;
import jenkem.client.presenter.HistoryPresenter;
import jenkem.client.presenter.MainPresenter;
import jenkem.client.presenter.Presenter;
import jenkem.client.service.JenkemServiceAsync;
import jenkem.client.view.HistoryView;
import jenkem.client.view.MainView;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.TabPanel;

public class AppController implements Presenter, ValueChangeHandler<String> {
	private final HandlerManager eventBus;
	private HasWidgets container;
	
	private final TabPanel tabPanel = new TabPanel();
	private final MainView mainView = new MainView();
	private final HistoryView historyView = new HistoryView();
	
	private Presenter mainPresenter;
	private Presenter historyPresenter;
	
	public AppController(final JenkemServiceAsync jenkemService,
			final HandlerManager eventBus) {
		this.eventBus = eventBus;
		
		mainPresenter = new MainPresenter(jenkemService, eventBus, tabPanel, mainView);
		historyPresenter = new HistoryPresenter(jenkemService, eventBus, tabPanel, historyView);
		
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
					if(!History.getToken().startsWith("history")) {	
						History.newItem("history/");
					}
				}
			}
		});

		tabPanel.setAnimationEnabled(true);
		tabPanel.add(mainView.asWidget(), "Main");
		tabPanel.add(historyView.asWidget(), "History");
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
			mainPresenter.go(container);
		} else if (token.startsWith("history/")) {
			tabPanel.selectTab(1);				
			historyPresenter.go(container);
		} else {
			tabPanel.selectTab(0);
			mainPresenter.go(container);			
		}
	}
	
}
