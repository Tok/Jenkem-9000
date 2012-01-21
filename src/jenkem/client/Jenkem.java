package jenkem.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Jenkem implements EntryPoint {
    private final AppController appController = new AppController();

    @Override
    public final void onModuleLoad() {
        final Image titleImage = new Image("/images/Jenkem-Title.png");
        RootPanel.get("title").add(titleImage);
        RootPanel.get().setStyleName("everything");
        appController.go(RootPanel.get("content"));
    }
}
