package jenkem.client;

import jenkem.client.event.CancelledEvent;
import jenkem.client.event.CancelledEventHandler;
import jenkem.client.presenter.GalleryPresenter;
import jenkem.client.presenter.InfoPresenter;
import jenkem.client.presenter.MainPresenter;
import jenkem.client.presenter.Presenter;
import jenkem.client.service.JenkemService;
import jenkem.client.service.JenkemServiceAsync;
import jenkem.client.view.GalleryView;
import jenkem.client.view.InfoView;
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
    private final InfoView infoView = new InfoView();

    private MainPresenter mainPresenter;
    private Presenter galleryPresenter;
    private Presenter infoPresenter;

    private boolean doConvert = true;

    public AppController() {
        prepareTabs();
        bind();
    }

    private void prepareTabs() {
        tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {
            @Override
            public void onSelection(final SelectionEvent<Integer> event) {
                final int selection = event.getSelectedItem().intValue();
                if (selection == 0) {
                    if (!History.getToken().startsWith("main")) {
                        doConvert = false;
                        History.newItem("main/"
                                + mainView.getInputTextBox().getValue());
                    }
                }
                if (selection == 1) {
                    if (!History.getToken().startsWith("gallery")) {
                        History.newItem("gallery/");
                    }
                }
                if (selection == 2) {
                    if (!History.getToken().startsWith("info")) {
                        History.newItem("info/");
                    }
                }
            }
        });

        // tabPanel.setAnimationEnabled(true); //XXX
        tabPanel.add(mainView.asWidget(), "Main");
        tabPanel.add(galleryView.asWidget(), "Gallery");
        tabPanel.add(infoView.asWidget(), "Info");
    }

    private void bind() {
        History.addValueChangeHandler(this);
        eventBus.addHandler(CancelledEvent.type, new CancelledEventHandler() {
            @Override
            public void onCancelled(final CancelledEvent event) {
                doEditTermCancelled();
            }
        });
    }

    private void doEditTermCancelled() {
        History.newItem("main/" + mainView.getInputTextBox().getValue());
    }

    @Override
    public final void go(final HasWidgets container) {
        this.container = container;
        if ("".equals(History.getToken())) {
            History.newItem("main/");
        } else {
            History.fireCurrentHistoryState();
        }
    }

    @Override
    public final void onValueChange(final ValueChangeEvent<String> event) {
        final String token = event.getValue();
        if (token.startsWith("main/")) {
            prepareMainTab(token);
        } else if (token.startsWith("gallery/")) {
            tabPanel.selectTab(1);
            galleryPresenter = new GalleryPresenter(jenkemService, eventBus,
                    tabPanel, galleryView);
            galleryPresenter.go(container);
        } else if (token.startsWith("info/")) {
            tabPanel.selectTab(2);
            infoPresenter = new InfoPresenter(eventBus, tabPanel, infoView);
            infoPresenter.go(container);
        } else {
            prepareMainTab("main/");
        }
    }

    private void prepareMainTab(final String token) {
        mainPresenter = new MainPresenter(jenkemService, eventBus, tabPanel,
                mainView);
        tabPanel.selectTab(0);
        String imageUrl = "";
        if (token.split("/", 2).length > 1) {
            imageUrl = token.split("/", 2)[1];
            if (!"".equals(imageUrl)) {
                if (doConvert) {
                    mainView.setUrl(imageUrl);
                    mainPresenter.proxifyAndConvert();
                }
            }
        }
        // doConvert = true;
        mainPresenter.go(container);
    }
}
