package jenkem.client.view;

import jenkem.client.presenter.HistoryPresenter;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;

public class HistoryView extends Composite implements HistoryPresenter.Display {	
	private final FlexTable contentTable;
	private final FlexTable historyList = new FlexTable();
	
	public HistoryView() {
		final DecoratorPanel contentTableDecorator = new DecoratorPanel();
		contentTableDecorator.setWidth("100%");
		contentTableDecorator.setWidth("1010px");
		initWidget(contentTableDecorator);

		contentTable = new FlexTable();
		contentTable.setWidth("100%");

		historyList.setWidth("987px");
		contentTable.setWidget(0, 0, historyList);

		contentTableDecorator.add(contentTable);
	}
	
	@Override
	public Widget asWidget() {
		return this;
	}

	@Override
	public FlexTable getHistoryList() {
		return historyList;
	}
}
