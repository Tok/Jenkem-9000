package jenkem.client.widget;

import jenkem.client.event.DoConversionEvent;
import jenkem.shared.ProcessionSettings;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.kiouri.sliderbar.client.event.BarValueChangedEvent;
import com.kiouri.sliderbar.client.event.BarValueChangedHandler;
import com.kiouri.sliderbar.client.solution.simplehorizontal.SliderBarSimpleHorizontal;

/**
 * Widget to set URLs.
 */
public class ProcessionSettingsPanel extends Composite {
    private static final int MAX_OFFSET = 128;
    private static final int DEFAULT_OFFSET = MAX_OFFSET / 2;
    private static final int SPACING = 5;
    private final HandlerManager eventBus;
    private final VerticalPanel mainPan = new VerticalPanel();
    private final HorizontalPanel pan = new HorizontalPanel();
    private final HorizontalPanel sliderPan = new HorizontalPanel();
    private final SliderBarSimpleHorizontal offsetSlider = new SliderBarSimpleHorizontal(MAX_OFFSET, MAX_OFFSET + "px", false);
    private final Label offsetLabel = new Label();
    private final CheckBox doVlineBox = new CheckBox("|");
    private final CheckBox doHlineBox = new CheckBox("--");
    private final CheckBox doEdgeBox = new CheckBox("Edge");
    private final CheckBox doDiagonalBox = new CheckBox("/ \\");
    private boolean isReady = false;

    public ProcessionSettingsPanel(final HandlerManager eventBus) {
        this.eventBus = eventBus;

        offsetLabel.setWidth("25px");

        sliderPan.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        sliderPan.setSpacing(SPACING);
        sliderPan.add(offsetSlider);
        sliderPan.add(offsetLabel);
        pan.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        pan.setSpacing(SPACING);
        pan.add(doVlineBox);
        pan.add(doHlineBox);
        pan.add(doEdgeBox);
        pan.add(doDiagonalBox);
        mainPan.add(sliderPan);
        mainPan.add(pan);

        reset();
        bind();
        initWidget(mainPan);
    }

    private void bind() {
        doVlineBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override public void onValueChange(final ValueChangeEvent<Boolean> event) {
                if (isReady) { eventBus.fireEvent(new DoConversionEvent(false)); }
            }});
        doHlineBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override public void onValueChange(final ValueChangeEvent<Boolean> event) {
                if (isReady) { eventBus.fireEvent(new DoConversionEvent(false)); }
            }});
        doEdgeBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override public void onValueChange(final ValueChangeEvent<Boolean> event) {
                if (isReady) { eventBus.fireEvent(new DoConversionEvent(false)); }
            }});
        doDiagonalBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override public void onValueChange(final ValueChangeEvent<Boolean> event) {
                if (isReady) { eventBus.fireEvent(new DoConversionEvent(false)); }
            }});
        offsetSlider.addBarValueChangedHandler(new BarValueChangedHandler() {
            @Override public void onBarValueChanged(final BarValueChangedEvent event) {
                offsetLabel.setText(String.valueOf(offsetSlider.getValue() / 2));
                if (isReady) { eventBus.fireEvent(new DoConversionEvent(false)); }
            }});
    }

    public final void reset() {
        offsetSlider.setValue(DEFAULT_OFFSET);
        doVlineBox.setValue(true);
        doHlineBox.setValue(false);
        doEdgeBox.setValue(true);
        doDiagonalBox.setValue(true);
    }

    public final void setEnabled(final boolean enabled) {
        doVlineBox.setEnabled(enabled);
        doHlineBox.setEnabled(enabled);
        doEdgeBox.setEnabled(enabled);
        doDiagonalBox.setEnabled(enabled);
    }

    public final ProcessionSettings getSettings() {
        return new ProcessionSettings(DEFAULT_OFFSET - Integer.valueOf(offsetLabel.getText()),
                doVlineBox.getValue(), doHlineBox.getValue(),
                doEdgeBox.getValue(), doDiagonalBox.getValue());
    }

    public final void setReady() { isReady = true; }
}
