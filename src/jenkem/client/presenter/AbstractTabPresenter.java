package jenkem.client.presenter;

import jenkem.client.service.JenkemServiceAsync;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;

public class AbstractTabPresenter {
	private final JenkemServiceAsync jenkemService;
	private final TabPanel tabPanel;	
	private final HandlerManager eventBus;
	public AbstractTabPresenter(final JenkemServiceAsync jenkemService, final HandlerManager eventBus, final TabPanel tabPanel) {
		this.jenkemService = jenkemService;
		this.tabPanel = tabPanel;
		this.eventBus = eventBus;
	}
	
	public JenkemServiceAsync getJenkemService() {
		return jenkemService;
	}
	
	public Widget getTabPanel() {
		return tabPanel;
	}
	
	public HandlerManager getEventBus() {
		return eventBus;
	}
}
