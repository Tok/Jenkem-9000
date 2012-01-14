package jenkem.client.view;

import jenkem.client.presenter.InfoPresenter;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;

public class InfoView extends Composite implements InfoPresenter.Display {	
	private final FlexTable contentTable;
	private final FlexTable infoList = new FlexTable();
	
	public InfoView() {
		final DecoratorPanel contentTableDecorator = new DecoratorPanel();
		contentTableDecorator.setWidth("1010px");
		initWidget(contentTableDecorator);

		contentTable = new FlexTable();
		infoList.setWidth("987px");
		contentTable.setWidget(0, 0, infoList);

		contentTableDecorator.add(contentTable);
	}
	
	@Override
	public Widget asWidget() {
		return this;
	}

	@Override
	public FlexTable getInfoList() {
		return infoList;
	}
}
