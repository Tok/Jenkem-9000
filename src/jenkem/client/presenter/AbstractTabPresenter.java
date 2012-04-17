package jenkem.client.presenter;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Abstract Presenter for Jenkem 9000
 */
public class AbstractTabPresenter {
    private final TabPanel tabPanel;
    private final HandlerManager eventBus;

    /**
     * Default constructor.
     * @param eventBus
     * @param tabPanel
     */
    public AbstractTabPresenter(final HandlerManager eventBus, final TabPanel tabPanel) {
        this.tabPanel = tabPanel;
        this.eventBus = eventBus;
    }

    /**
     * Returns the tab panel.
     * @return tabPanel
     */
    public final Widget getTabPanel() {
        return tabPanel;
    }

    /**
     * Returns the event bus.
     * @return eventBus
     */
    public final HandlerManager getEventBus() {
        return eventBus;
    }
}
