package jenkem.client.widget;

import java.util.HashMap;
import java.util.Map;
import jenkem.client.event.DoConversionEvent;
import jenkem.shared.ColorScheme;
import jenkem.shared.color.IrcColor;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.kiouri.sliderbar.client.event.BarValueChangedEvent;
import com.kiouri.sliderbar.client.event.BarValueChangedHandler;
import com.kiouri.sliderbar.client.solution.simplevertical.SliderBarSimpleVertical;

/**
 * Widget to send messages to IRC.
 */
public class IrcColorSetter extends Composite {
    private static final int MAX_PERCENT = 100;
    private final HandlerManager eventBus;
    private final FlexTable flex = new FlexTable();

    private final ListBox schemeListBox = new ListBox();
    private final Map<IrcColor, SliderBarSimpleVertical> sliders = new HashMap<IrcColor, SliderBarSimpleVertical>();
    private final Map<IrcColor, Label> labels = new HashMap<IrcColor, Label>();
    private boolean isReady = false;

    public IrcColorSetter(final HandlerManager eventBus) {
        this.eventBus = eventBus;
        flex.setWidth("400px");

        for (final ColorScheme scheme : ColorScheme.values()) {
            schemeListBox.addItem(scheme.toString());
        }
        schemeListBox.setWidth("200px");
        final int positon = IrcColor.values().length / 2;
        flex.setWidget(0, positon, schemeListBox);
        flex.getFlexCellFormatter().setColSpan(0, positon, positon);

        for (IrcColor ic : IrcColor.values()) {
            final SliderBarSimpleVertical colorSlider = new SliderBarSimpleVertical(MAX_PERCENT, MAX_PERCENT + "px", false);
            sliders.put(ic, colorSlider);
            flex.setWidget(1, ic.getValue(), colorSlider);
            final Label colorLabel = new Label();
            colorLabel.setWidth("22px");
            colorLabel.setStyleName(ic.name() + " slider");
            labels.put(ic, colorLabel);
            flex.setWidget(2, ic.getValue(), colorLabel);
            bind(ic, colorSlider, colorLabel);
        }

        reset();
        schemeListBox.addChangeHandler(new ChangeHandler() {
            @Override public void onChange(final ChangeEvent event) {
                final String schemeName = schemeListBox.getValue(schemeListBox.getSelectedIndex());
                setScheme(ColorScheme.valueOf(schemeName));
                eventBus.fireEvent(new DoConversionEvent(false));
            }});

        initWidget(flex);
    }

    private void bind(final IrcColor ic, final SliderBarSimpleVertical colorSlider, final Label colorLabel) {
        colorSlider.addBarValueChangedHandler(new BarValueChangedHandler() {
            @Override public void onBarValueChanged(final BarValueChangedEvent event) {
                colorLabel.setText(String.valueOf(MAX_PERCENT - colorSlider.getValue()));
                if (isReady) {
                    eventBus.fireEvent(new DoConversionEvent(false));
                }
            }});
    }

    private void setValue(final IrcColor ic, final int value) {
        if (value >= 0 && value <= MAX_PERCENT) {
            SliderBarSimpleVertical slider = sliders.get(ic);
            slider.setValue(MAX_PERCENT - value);
            labels.get(ic).setText(String.valueOf(MAX_PERCENT - slider.getValue()));
        } else {
            throw new IllegalArgumentException("Color out of range: " + value);
        }
    }

    private int getValue(final IrcColor ic) {
        return Integer.valueOf(labels.get(ic).getText());
    }

    public final void setEnabled(final boolean enabled) {
        schemeListBox.setEnabled(enabled);
    }

    public final void setScheme(final ColorScheme scheme) {
        for (final IrcColor ic : IrcColor.values()) {
            setValue(ic, ic.getOrder(scheme));
        }
    }

    public final Map<IrcColor, Integer> getColorMap() {
        final Map<IrcColor, Integer> colorMap = new HashMap<IrcColor, Integer>();
        for (final IrcColor ic : IrcColor.values()) {
            colorMap.put(ic, getValue(ic));
        }
        return colorMap;
    }

    public final void reset() {
        schemeListBox.setSelectedIndex(1); //default
        final String schemeName = schemeListBox.getValue(schemeListBox.getSelectedIndex());
        setScheme(ColorScheme.valueOf(schemeName));
    }

    public final void setSelectedScheme(final ColorScheme defaultScheme) {
        for (int i = 0; i < schemeListBox.getItemCount(); i++) {
            if (schemeListBox.getItemText(i).equals(defaultScheme.toString())) {
                schemeListBox.setSelectedIndex(i);
                setScheme(defaultScheme);
                return;
            }
        }
    }

    public final void setReady() { isReady = true; }
}
