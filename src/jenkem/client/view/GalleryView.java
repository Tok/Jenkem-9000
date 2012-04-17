package jenkem.client.view;

import jenkem.client.presenter.GalleryPresenter;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Widget;

/**
 * View for the gallery of stored conversions.
 */
public class GalleryView extends Composite implements GalleryPresenter.Display {
    private final FlexTable contentTable;
    private final FlexTable historyList = new FlexTable();

    /**
     * Default constructor.
     */
    public GalleryView() {
        final DecoratorPanel contentTableDecorator = new DecoratorPanel();
        contentTableDecorator.setWidth("1010px");
        initWidget(contentTableDecorator);
        contentTable = new FlexTable();
        contentTable.setHeight("1010px");
        historyList.setWidth("987px");
        contentTable.setWidget(0, 0, historyList);
        contentTable.getFlexCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
        contentTableDecorator.add(contentTable);
    }

    /**
     * Returns this view as gwt Widget.
     */
    @Override
    public final Widget asWidget() {
        return this;
    }

    /**
     * Returns the FlexTable of this gallery.
     */
    @Override
    public final FlexTable getHistoryList() {
        return historyList;
    }
}
