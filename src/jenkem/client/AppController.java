package jenkem.client;

import jenkem.client.event.CancelledEvent;
import jenkem.client.event.CancelledEventHandler;
import jenkem.client.presenter.MainPresenter;
import jenkem.client.presenter.Presenter;
import jenkem.client.service.JenkemServiceAsync;
import jenkem.client.view.MainView;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.HasWidgets;

public class AppController implements Presenter, ValueChangeHandler<String> {
	private final HandlerManager eventBus;
	private final JenkemServiceAsync rpcService;
	private HasWidgets container;

	public AppController(final JenkemServiceAsync rpcService,
			final HandlerManager eventBus) {
		this.eventBus = eventBus;
		this.rpcService = rpcService;
		bind();
	}

	private void bind() {
		History.addValueChangeHandler(this);
		
		eventBus.addHandler(CancelledEvent.TYPE,
				new CancelledEventHandler() {
					public void onCancelled(CancelledEvent event) {
						doEditTermCancelled();
					}
				});
	}

	private void doEditTermCancelled() {
		History.newItem("main");
	}

	public void go(final HasWidgets container) {
		this.container = container;
		if ("".equals(History.getToken())) {
			History.newItem("main");
		} else {
			History.fireCurrentHistoryState();
		}
	}

	public void onValueChange(final ValueChangeEvent<String> event) {
		final String token = event.getValue();
		if (token != null) {
			Presenter presenter = null;
			if (token.equals("main")) {
				presenter = new MainPresenter(rpcService, eventBus, new MainView());
			}
			presenter.go(container);
		}
	}
}
