package jenkem.client.presenter;

import java.util.List;
import jenkem.client.service.JenkemServiceAsync;
import jenkem.shared.HtmlUtil;
import jenkem.shared.data.JenkemImageInfo;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Presenter for the GalleryView.
 */
public class GalleryPresenter extends AbstractTabPresenter implements Presenter {
    private final Display display;
    private final JenkemServiceAsync jenkemService;

    public interface Display {
        FlexTable getHistoryList();
        Widget asWidget();
    }

    /**
     * Default constructor.
     * @param jenkemService
     * @param eventBus
     * @param tabPanel
     * @param view
     */
    public GalleryPresenter(final JenkemServiceAsync jenkemService,
            final HandlerManager eventBus, final TabPanel tabPanel,
            final Display view) {
        super(eventBus, tabPanel);
        this.jenkemService = jenkemService;
        this.display = view;
    }

    public void bind() {
    }

    @Override
    public final void go(final HasWidgets container) {
        bind();
        prepareTable();
        container.clear();
        container.add(super.getTabPanel());
    }

    /**
     * Prepares the table to display with gallery entries.
     */
    private void prepareTable() {
        display.getHistoryList().removeAllRows();
        display.getHistoryList().setText(0, 0, "Loading...");
        jenkemService.getAllImageInfo(new AsyncCallback<List<JenkemImageInfo>>() {
                    @Override
                    public void onSuccess(final List<JenkemImageInfo> result) {
                        if (result != null) {
                            prepareResultTable(result);
                        } else {
                            display.getHistoryList().setText(0, 0,
                                    "No images saved.");
                        }
                    }
                    @Override
                    public void onFailure(final Throwable caught) {
                        display.getHistoryList().setText(0, 0,
                                "Fail loading images.");
                    }
                });
    }

    /**
     * Prepares the table to display the converted images.
     * @param result
     */
    private void prepareResultTable(final List<JenkemImageInfo> result) {
        display.getHistoryList().setText(0, 0, "Name");
        display.getHistoryList().setText(0, 1, "Number of Lines");
        display.getHistoryList().setText(0, 2, "Line Width");
        display.getHistoryList().setText(0, 3, "Creation Date");
        int row = 1;
        for (final JenkemImageInfo imageInfo : result) {
            final String urlString = HtmlUtil.getHtmlUrl(imageInfo.getName());
            display.getHistoryList().setWidget(row, 0,
                    new Anchor(imageInfo.getName(), urlString));
            display.getHistoryList().setText(row, 1,
                    imageInfo.getLines().toString());
            display.getHistoryList().setText(row, 2,
                    imageInfo.getLineWidth().toString());
            // display.getHistoryList().getFlexCellFormatter().setHorizontalAlignment(row,
            // 1, HasHorizontalAlignment.ALIGN_RIGHT);
            display.getHistoryList().setText(row, 3, imageInfo.getCreation());
            row++;
        }
    }

}
