package jenkem.client.presenter;

import java.util.List;

import jenkem.client.service.JenkemServiceAsync;
import jenkem.shared.data.JenkemImageInfo;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;

public class GalleryPresenter extends AbstractTabPresenter implements Presenter {
	private final Display display;
	private final JenkemServiceAsync jenkemService;
	
	public interface Display {
		FlexTable getHistoryList();
		Widget asWidget();
	}
	
	public GalleryPresenter(final JenkemServiceAsync jenkemService, final HandlerManager eventBus, final TabPanel tabPanel, final Display view) {
		super(eventBus, tabPanel);
		this.jenkemService = jenkemService;
		this.display = view;
	}

	public void bind() {
	}
		
	public void go(final HasWidgets container) {
		bind();
		prepareTable();
		container.clear();
		container.add(super.getTabPanel());
	}

	private void prepareTable() {
		display.getHistoryList().removeAllRows();
		display.getHistoryList().setText(0, 0, "Loading...");
		
		jenkemService.getAllImageInfo(new AsyncCallback<List<JenkemImageInfo>>() {			
			@Override
			public void onSuccess(List<JenkemImageInfo> result) {
				if (result != null) {
					prepareResultTable(result);					
				} else {
					display.getHistoryList().setText(0, 0, "No images saved.");					
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				display.getHistoryList().setText(0, 0, "Fail loading images.");
			}
		});
	}
		
	private void prepareResultTable(List<JenkemImageInfo> result) {
		display.getHistoryList().setText(0, 0, "Name");
		display.getHistoryList().setText(0, 1, "Creation Date");
		final DateTimeFormat format = DateTimeFormat.getFormat("yyyy.MM.dd HH:mm:ss"); //TODO use date Util
		int row = 1;
		for (JenkemImageInfo imageInfo : result) {
			String urlString = "http://" + Window.Location.getHost() + "/jenkem/output?name=" + imageInfo.getName() + "&type=html";
			display.getHistoryList().setWidget(row, 0, new Anchor(imageInfo.getName(), urlString));
			display.getHistoryList().setText(row, 1, format.format(imageInfo.getCreateDate()));
			row++;
		}
	}
	
}