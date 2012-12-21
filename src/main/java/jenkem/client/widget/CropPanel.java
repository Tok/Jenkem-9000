package jenkem.client.widget;

import java.util.HashMap;
import java.util.Map;
import jenkem.client.event.DoConversionEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Widget to set URLs.
 */
public class CropPanel extends Composite {
    public enum Type { Left, Top, Right, Bottom }
    private static final int SPACING = 5;
    private static final int MAX_PERCENT = 100;
    private final HandlerManager eventBus;
    private final Label statusLabel;
    private final HorizontalPanel cropPanel = new HorizontalPanel();
    private final Map<Type, TextBox> boxes = new HashMap<Type, TextBox>();
    private final Button cropButton = new Button("Crop and Stretch");
    private final Button resetButton = new Button("Reset Crops");

    public CropPanel(final HandlerManager eventBus, final Label statusLabel) {
        this.eventBus = eventBus;
        this.statusLabel = statusLabel;

        cropPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        cropPanel.setSpacing(SPACING);
        for (final Type type : Type.values()) {
            final TextBox box = new TextBox();
            box.setWidth("20px");
            box.setText("0");
            box.addChangeHandler(new ChangeHandler() {
                @Override public void onChange(final ChangeEvent event) {
                    doCrop();
                }
            });
            boxes.put(type, box);
            cropPanel.add(new Label("Crop " + type.name() + " (%): "));
            cropPanel.add(box);
        }
        cropButton.setWidth("128px");
        resetButton.setWidth("128px");
        //TODO Add a CheckBox that allows to keep the aspect ratio by stretching the image accordingly.
        cropButton.setTitle("In order to keep the aspect ratio, make sure to crop the same % horizontally and vertically.");
        cropPanel.add(cropButton);
        cropPanel.add(resetButton);

        bind();
        initWidget(cropPanel);
    }

    private void bind() {
        cropButton.addClickHandler(new ClickHandler() {
            @Override public void onClick(final ClickEvent event) {
                doCrop();
            }
        });
        resetButton.addClickHandler(new ClickHandler() {
            @Override public void onClick(final ClickEvent event) {
                for (final Type type : Type.values()) {
                    boxes.get(type).setText("0");
                }
                doCrop();
            }
        });
    }

    private void doCrop() {
        for (final Type type : Type.values()) {
            final TextBox box = boxes.get(type);
            try {
                final int value = getCrop(type);
                if (value < 0 || value > MAX_PERCENT) {
                    statusLabel.setText(type + " crop must be between 0 and 100.");
                    box.setFocus(true);
                    return;
                }
            } catch (NumberFormatException nfe) {
                statusLabel.setText(type + " crop must be numeric.");
                box.setFocus(true);
                return;
            }
        }
        if (getCrop(Type.Left) + getCrop(Type.Right) >= MAX_PERCENT) {
            statusLabel.setText("Left crop plus Right crop must be smaller than 100.");
            boxes.get(Type.Left).setFocus(true);
            return;
        }
        if (getCrop(Type.Top) + getCrop(Type.Bottom) >= MAX_PERCENT) {
            statusLabel.setText("Top crop plus Bottom crop must be smaller than 100.");
            boxes.get(Type.Top).setFocus(true);
            return;
        }
        eventBus.fireEvent(new DoConversionEvent(true));
    }

    public final int getCrop(final Type type) {
        return Integer.valueOf(boxes.get(type).getValue());
    }
}
