package jenkem.client.view;

import java.util.HashMap;
import java.util.Map;
import jenkem.client.presenter.MainPresenter;
import jenkem.client.widget.IrcColorSetter;
import jenkem.client.widget.IrcConnector;
import jenkem.client.widget.ProcessionSettingsPanel;
import jenkem.client.widget.UrlSetter;
import jenkem.shared.CharacterSet;
import jenkem.shared.ConversionMethod;
import jenkem.shared.Kick;
import jenkem.shared.LineWidth;
import jenkem.shared.Power;
import jenkem.shared.ProcessionSettings;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.i18n.client.HasDirection.Direction;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.kiouri.sliderbar.client.solution.simplehorizontal.SliderBarSimpleHorizontal;

/**
 * View of the main page.
 */
public class MainView extends Composite implements MainPresenter.Display {
    private static final int IRC_TEXT_CHARACTER_WIDTH = 77;
    private static final int SLIDER_WIDTH = 200;
    private static final int INITIAL_CONTRAST_MAX = SLIDER_WIDTH - 1;
    private static final int INITIAL_CONTRAST_DEFAULT = (SLIDER_WIDTH / 2);
    private static final int INITIAL_BRIGHTNESS_MAX = SLIDER_WIDTH - 1;
    private static final int INITIAL_BRIGHTNESS_DEFAULT = (SLIDER_WIDTH / 2);

    private final Map<Kick, RadioButton> kickButtons = new HashMap<Kick, RadioButton>();
    private final ListBox methodListBox = new ListBox();
    private final ListBox widthListBox = new ListBox();
    private final ListBox powerListBox = new ListBox();
    private final ListBox presetListBox = new ListBox();
    private final TextBox presetTextBox = new TextBox();
    private final HorizontalPanel kickPanel = new HorizontalPanel();
    private final HorizontalPanel backgroundPanel = new HorizontalPanel();
    private final Label contrastLabel = new Label();
    private final Label brightnessLabel = new Label();
    private final Button submitButton = new Button("Submit to Gallery");
    private final Button resetButton = new Button("Reset");
    private final SliderBarSimpleHorizontal contrastSlider = new SliderBarSimpleHorizontal(100, "100px", false);
    private final SliderBarSimpleHorizontal brightnessSlider = new SliderBarSimpleHorizontal(100, "100px", false);
    private final RadioButton blackBgButton = new RadioButton("bg", "Black");
    private final RadioButton whiteBgButton = new RadioButton("bg", "White");
    private final Panel previewPanel = new VerticalPanel();
    private final Canvas canvas = Canvas.createIfSupported();
    private final InlineHTML inline = new InlineHTML();
    private final TextArea ircText = new TextArea();
    private final ProcessionSettingsPanel processingPanel;
    private final UrlSetter urlSetter;
    private final IrcColorSetter ircColorSetter;
    private final IrcConnector ircConnector;
    private final FlexTable contentTable = new FlexTable();

    /**
     * Default constructor.
     * TODO Megamoth
     */
    public MainView(final HandlerManager eventBus) {
        urlSetter = new UrlSetter(eventBus);
        processingPanel = new ProcessionSettingsPanel(eventBus);
        ircColorSetter = new IrcColorSetter(eventBus);
        ircConnector = new IrcConnector(eventBus);

        final FlexTable flex = new FlexTable();
        final FlexTable settingsTable = new FlexTable();
        final DecoratorPanel contentTableDecorator = new DecoratorPanel();

        contentTableDecorator.setWidth("1010px");
        previewPanel.setWidth("545px");
        previewPanel.setHeight("1010px");
        settingsTable.getFlexCellFormatter().setWidth(0, 0, "170px");
        settingsTable.getFlexCellFormatter().setWidth(1, 0, "200px");
        settingsTable.getFlexCellFormatter().setWidth(2, 0, "50px");
        methodListBox.setWidth("200px");
        widthListBox.setWidth("200px");
        resetButton.setWidth("200px");
        presetListBox.setWidth("200px");
        presetTextBox.setWidth("190px");
        powerListBox.setWidth("200px");
        submitButton.setWidth("200px");
        contrastLabel.setWidth("25px");
        brightnessLabel.setWidth("25px");
        ircText.setWidth("393px");

        int row = 0;
        contentTable.setWidget(row++, 0, urlSetter);

        previewPanel.add(inline);
        flex.setWidget(0, 0, previewPanel);
        flex.getFlexCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);

        int settingsRow = 0;
        for (final ConversionMethod method : ConversionMethod.values()) { methodListBox.addItem(method.toString()); }
        settingsTable.setText(settingsRow, 0, "Conversion Method:");
        settingsTable.setWidget(settingsRow, 1, methodListBox);
        settingsTable.getFlexCellFormatter().setColSpan(settingsRow, 1, 2);
        settingsRow++;

        for (final LineWidth lw : LineWidth.values()) { widthListBox.addItem(lw.getValueString()); }
        settingsTable.setText(settingsRow, 0, "Max Line Width:");
        settingsTable.setWidget(settingsRow, 1, widthListBox);
        settingsTable.getFlexCellFormatter().setColSpan(settingsRow, 1, 2);
        settingsRow++;

        settingsTable.setWidget(settingsRow, 0, new HTML("&nbsp;"));
        settingsTable.getFlexCellFormatter().setColSpan(settingsRow, 1, 3);
        settingsRow++;

        settingsTable.setText(settingsRow, 0, "Reset values:");
        settingsTable.setWidget(settingsRow, 1, resetButton);
        settingsTable.getFlexCellFormatter().setColSpan(settingsRow, 1, 2);
        settingsRow++;

        for (final CharacterSet preset : CharacterSet.values()) { presetListBox.addItem(preset.name()); }
        settingsTable.setText(settingsRow, 0, "Character Set:");
        settingsTable.setWidget(settingsRow, 1, presetListBox);
        settingsTable.getFlexCellFormatter().setColSpan(settingsRow, 1, 2);
        settingsRow++;

        presetTextBox.setText(CharacterSet.Hard.getCharacters()); //default
        presetTextBox.setTitle("For best results, the characters must be arranged from bright to dark. Numbers and commas are not allowed.");
        settingsTable.setWidget(settingsRow, 1, presetTextBox);
        settingsTable.getFlexCellFormatter().setColSpan(settingsRow, 1, 2);
        settingsRow++;

        settingsTable.setText(settingsRow, 0, "Processing:");
        settingsTable.setWidget(settingsRow, 1, processingPanel);
        settingsTable.getFlexCellFormatter().setColSpan(settingsRow, 1, 3);
        settingsRow++;

        settingsTable.setText(settingsRow, 0, "Kick:");
        initKicks(kickPanel);
        settingsTable.setWidget(settingsRow, 1, kickPanel);
        settingsTable.getFlexCellFormatter().setColSpan(settingsRow, 1, 2);
        settingsRow++;

        settingsTable.setText(settingsRow, 0, "Power:");
        for (final Power power : Power.values()) { powerListBox.addItem(power.toString()); }
        powerListBox.setSelectedIndex(1); //quadratic
        settingsTable.setWidget(settingsRow, 1, powerListBox);
        settingsTable.getFlexCellFormatter().setColSpan(settingsRow, 1, 2);
        settingsRow++;

        settingsTable.setText(settingsRow, 0, "Default Background:");
        blackBgButton.setValue(true);
        backgroundPanel.add(blackBgButton);
        backgroundPanel.add(whiteBgButton);
        settingsTable.setWidget(settingsRow, 1, backgroundPanel);
        settingsTable.getFlexCellFormatter().setColSpan(settingsRow, 1, 2);
        settingsRow++;

        settingsTable.setText(settingsRow, 0, "Contrast:");
        contrastSlider.setValue(INITIAL_CONTRAST_DEFAULT);
        contrastSlider.setMaxValue(INITIAL_CONTRAST_MAX);
        contrastSlider.setWidth(SLIDER_WIDTH + "px");
        settingsTable.setWidget(settingsRow, 1, contrastSlider);
        settingsTable.setWidget(settingsRow, 2, contrastLabel);
        settingsRow++;

        settingsTable.setText(settingsRow, 0, "Brightness:");
        brightnessSlider.setValue(INITIAL_BRIGHTNESS_DEFAULT);
        brightnessSlider.setMaxValue(INITIAL_BRIGHTNESS_MAX);
        brightnessSlider.setWidth(SLIDER_WIDTH + "px");
        settingsTable.setWidget(settingsRow, 1, brightnessSlider);
        settingsTable.setWidget(settingsRow, 2, brightnessLabel);
        settingsRow++;

        settingsTable.setWidget(settingsRow, 0, new HTML("&nbsp;"));
        settingsTable.getFlexCellFormatter().setColSpan(settingsRow, 1, 3);
        settingsRow++;

        settingsTable.setWidget(settingsRow, 0, ircColorSetter);
        settingsTable.getFlexCellFormatter().setColSpan(settingsRow, 0, 3);
        settingsRow++;

        settingsTable.setWidget(settingsRow, 0, new HTML("&nbsp;"));
        settingsTable.getFlexCellFormatter().setColSpan(settingsRow, 1, 3);
        settingsRow++;

        settingsTable.setText(settingsRow, 0, "Submit Conversion:");
        settingsTable.setWidget(settingsRow, 1, submitButton);
        settingsRow++;

        settingsTable.setText(settingsRow, 0, "Binary Output for IRC:");
        settingsRow++;

        ircText.setCharacterWidth(IRC_TEXT_CHARACTER_WIDTH);
        ircText.setVisibleLines(5);
        ircText.setReadOnly(true);
        ircText.setDirection(Direction.LTR);
        ircText.getElement().setAttribute("wrap", "off");
        settingsTable.setWidget(settingsRow, 0, ircText);
        settingsTable.getFlexCellFormatter().setColSpan(settingsRow, 0, 3);
        settingsRow++;

        settingsTable.setWidget(settingsRow, 0, new HTML("&nbsp;"));
        settingsRow++;
        settingsTable.setWidget(settingsRow, 0, ircConnector);
        settingsTable.getFlexCellFormatter().setColSpan(settingsRow, 0, 3);

        flex.setWidget(0, 1, settingsTable);
        flex.getFlexCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_TOP);

        contentTable.setWidget(row, 0, flex);
        contentTableDecorator.add(contentTable);
        initWidget(contentTableDecorator);
    }

    /**
     * Initializes the Kick RadioButtons.
     * @param kickPanel
     */
    private void initKicks(final HorizontalPanel kickPanel) {
        for (final Kick kick : Kick.values()) {
            final RadioButton kickRadioButton = new RadioButton("kick", kick.name());
            kickButtons.put(kick, kickRadioButton);
            kickPanel.add(kickRadioButton);
        }
    }

    @Override public final Widget asWidget() { return this; }
    @Override public final Canvas getCanvas() { return canvas; }
    @Override public final UrlSetter getUrlSetter() { return urlSetter; }
    @Override public final ListBox getMethodListBox() { return methodListBox; }
    @Override public final ListBox getWidthListBox() { return widthListBox; }
    @Override public final ListBox getPresetListBox() { return presetListBox; }
    @Override public final TextBox getPresetTextBox() { return presetTextBox; }
    @Override public final ListBox getPowerListBox() { return powerListBox; }
    @Override public final Button getResetButton() { return resetButton; }
    @Override public final SliderBarSimpleHorizontal getContrastSlider() { return contrastSlider; }
    @Override public final Label getContrastLabel() { return contrastLabel; }
    @Override public final SliderBarSimpleHorizontal getBrightnessSlider() { return brightnessSlider; }
    @Override public final Label getBrightnessLabel() { return brightnessLabel; }
    @Override public final RadioButton getKickButton(final Kick kick) { return kickButtons.get(kick); }
    @Override public final InlineHTML getPreviewHtml() { return inline; }
    @Override public final TextArea getIrcTextArea() { return ircText; }
    @Override public final IrcColorSetter getIrcColorSetter() { return ircColorSetter; }
    @Override public final IrcConnector getIrcConnector() { return ircConnector; }
    @Override public final Button getSubmitButton() { return submitButton; }
    @Override public final int getInitialContrast() { return INITIAL_CONTRAST_DEFAULT; }
    @Override public final int getInitialBrightness() { return INITIAL_BRIGHTNESS_DEFAULT; }
    @Override public final ProcessionSettings getProcessionSettings() { return processingPanel.getSettings(); }
    @Override public final void resetProcession() { processingPanel.reset(); }
    @Override public final void enableProcession(final boolean enable) { processingPanel.setEnabled(enable); }
    @Override public final boolean isDefaultBgBlack() { return blackBgButton.getValue().booleanValue(); }
    @Override public final RadioButton getBlackBgButton() { return blackBgButton; }
    @Override public final RadioButton getWhiteBgButton() { return whiteBgButton; }
}
