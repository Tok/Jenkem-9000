package jenkem.client.presenter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import jenkem.client.event.SendToIrcEvent;
import jenkem.client.event.SendToIrcEventHandler;
import jenkem.client.service.JenkemServiceAsync;
import jenkem.client.widget.IrcConnector;
import jenkem.shared.CharacterSet;
import jenkem.shared.ColorScheme;
import jenkem.shared.ConversionMethod;
import jenkem.shared.Engine;
import jenkem.shared.HtmlUtil;
import jenkem.shared.Kick;
import jenkem.shared.color.Sample;
import jenkem.shared.data.ImageCss;
import jenkem.shared.data.ImageHtml;
import jenkem.shared.data.ImageInfo;
import jenkem.shared.data.ImageIrc;
import jenkem.shared.data.JenkemImage;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.kiouri.sliderbar.client.event.BarValueChangedEvent;
import com.kiouri.sliderbar.client.event.BarValueChangedHandler;
import com.kiouri.sliderbar.client.solution.simplehorizontal.SliderBarSimpleHorizontal;

/**
 * Presenter for the main view.
 */
public class MainPresenter extends AbstractTabPresenter implements Presenter {
    private static final int TOTAL_PERCENT = 100;

    private final JenkemServiceAsync jenkemService;
    private final HtmlUtil htmlUtil = new HtmlUtil();
    private final Engine engine;
    private final Display display;

    private ConversionMethod method;
    //private String[] ircOutput;
    private final List<String> ircOutput = new ArrayList<String>();
    private int lastIndex;

    private final Image busyImage = new Image("/images/busy.gif");
    private Image image;
    private ImageElement currentImage;
    private String currentName;
    private static JenkemImage jenkemImage;

    private boolean isConversionRunnung = false;

    public interface Display {
        HasValue<String> getInputLink();
        TextBox getInputTextBox();
        Label getStatusLabel();
        HasClickHandlers getShowButton();
        Panel getBusyPanel();
        Canvas getCanvas();
        InlineHTML getPreviewHtml();
        TextArea getIrcTextArea();
        IrcConnector getIrcConnector();
        ListBox getMethodListBox();
        ListBox getWidthListBox();
        ListBox getSchemeListBox();
        ListBox getPresetListBox();
        Button getResetButton();
        SliderBarSimpleHorizontal getContrastSlider();
        Label getContrastLabel();
        SliderBarSimpleHorizontal getBrightnessSlider();
        Label getBrightnessLabel();
        RadioButton getKickButton(Kick kick);
        Button getSubmitButton();
        Widget asWidget();
        int getInitialContrast();
        int getInitialBrightness();
    }

    /**
     * Default constructor.
     * @param jenkemService
     * @param eventBus
     * @param tabPanel
     * @param view
     */
    public MainPresenter(final JenkemServiceAsync jenkemService,
            final HandlerManager eventBus, final TabPanel tabPanel,
            final Display view) {
        super(eventBus, tabPanel);
        this.jenkemService = jenkemService;
        this.display = view;
        this.engine = new Engine(this);
    }

    /**
     * Binds the elements from the view.
     */
    public final void bind() {
        getEventBus().addHandler(SendToIrcEvent.TYPE, new SendToIrcEventHandler() {
            @Override
            public void onSend(final SendToIrcEvent event) {
                display.getIrcConnector().sendMessage(ircOutput);
            }
        });
        this.display.getInputTextBox().addKeyPressHandler(new KeyPressHandler() {
            @Override
            public void onKeyPress(final KeyPressEvent event) {
                if (event.getCharCode() == KeyCodes.KEY_ENTER) {
                    replaceUrl();
                }
            }});
        this.display.getShowButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                replaceUrl();
            }});
        this.display.getMethodListBox().addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(final ChangeEvent event) {
                method = getCurrentConversionMethod();
                if (method.equals(ConversionMethod.FullHd)) {
                    disableKicks();
                } else {
                    enableKicks();
                }
                display.getSchemeListBox().setEnabled(!method.equals(ConversionMethod.Plain));
                if (!method.equals(ConversionMethod.Pwntari)) {
                    display.getPresetListBox().setEnabled(true);
                }
                resetContrastAndBrightness();
                replaceUrl();
            }});
        this.display.getWidthListBox().addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(final ChangeEvent event) {
                replaceUrl();
            }});
        this.display.getSchemeListBox().addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(final ChangeEvent event) {
                doConversion();
            }});
        this.display.getPresetListBox().addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(final ChangeEvent event) {
                doConversion();
            }});
        this.display.getResetButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                doReset();
                doConversion();
            }});
        this.display.getContrastSlider().addBarValueChangedHandler(new BarValueChangedHandler() {
            @Override
            public void onBarValueChanged(final BarValueChangedEvent event) {
                updateContrast();
                doConversion();
            }});
        this.display.getBrightnessSlider().addBarValueChangedHandler(new BarValueChangedHandler() {
            @Override
            public void onBarValueChanged(final BarValueChangedEvent event) {
                updateBrightness();
                doConversion();
            }});
        for (final Kick kick : Kick.values()) {
            this.display.getKickButton(kick).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                @Override
                public void onValueChange(final ValueChangeEvent<Boolean> event) {
                    if (event.getValue()) {
                        doConversion();
                    }
                }});
        }
        this.display.getSubmitButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                synchronized (this) {
                    display.getSubmitButton().setEnabled(false); //prevent double clicks
                    jenkemService.saveJenkemImage(jenkemImage, new AsyncCallback<Void>() {
                        @Override
                        public void onFailure(final Throwable caught) {
                            handleSubmissionResult("Fail submitting conversion.");
                        }
                        @Override
                        public void onSuccess(final Void result) {
                            handleSubmissionResult("Conversion submitted successfully.");
                        }
                    });
                }
            }});
    }

    /**
     * Shows submission message and enables submission button.
     * @param result
     */
    private void handleSubmissionResult(final String result) {
        display.getStatusLabel().setText(result);
        display.getSubmitButton().setEnabled(true);
    }

    /**
     * Creates a new history event if required.
     */
    private void replaceUrl() {
        final String currentToken = History.getToken();
        final String currentUrl = display.getInputTextBox().getValue();
        if (!currentToken.endsWith(currentUrl)) {
            History.newItem("main/" + display.getInputTextBox().getValue());
        } else {
            proxifyAndConvert();
        }
    }

    /**
     * Proxifies the image and calls the show method.
     */
    public final void proxifyAndConvert() {
        final String urlString = display.getInputTextBox().getText();
        final String proxifiedUrl = proxify(urlString);
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override public void execute() { doShow(proxifiedUrl); }
        });
    }

    /**
     * Calls the local image servlet to proxify the provided image in order to
     * circumvent the restrictions put by the same origin policy.
     * @param urlString a String with the url to the image
     * @return url to image servlet
     */
    private String proxify(final String urlString) {
        display.getStatusLabel().setText("Proxifying image.");
        updateImageName(urlString);
        return ("".equals(urlString)) ? "" : "http://" + Window.Location.getHost() + "/jenkem/image?url=" + urlString;
    }

    /**
     * Updates the name of the image from the provided url.
     * @param urlString
     */
    private void updateImageName(final String urlString) {
        final String[] split = urlString.split("/");
        currentName = split[split.length - 1];
    }

    @Override
    public final void go(final HasWidgets container) {
        bind();
        container.clear();
        container.add(super.getTabPanel());
        display.getInputTextBox().setFocus(true);
        doReset();
    }

    /**
     * Shows the proxified image.
     * @param url to the image
     */
    private void doShow(final String url) {
        if (!"".equals(url)) { displayBusyIcon(); }
        image = new Image();
        image.addErrorHandler(new ErrorHandler() {
            @Override
            public void onError(final ErrorEvent event) {
                display.getStatusLabel().setText("Proxifying this image failed.");
                removeBusyIcon();
            }});
        image.addLoadHandler(new LoadHandler() {
            @Override
            public void onLoad(final LoadEvent event) {
                display.getStatusLabel().setText("Image loaded.");
                doConversion();
            }});
        image.setUrl(url);
        // Image must be added in order for load event to fire.
        image.setVisible(false);
        RootPanel.get("invisible").clear();
        RootPanel.get("invisible").add(image);
    }

    /**
     * Defers the conversion.
     */
    private void doConversion() {
        if (image == null) { return; }
        if (!isConversionRunnung) {
            isConversionRunnung = true;
            displayBusyIcon();
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                public void execute() {
                    doDeferredConversion();
                }});
        }
    }

    /**
     * Returns the selected conversion method.
     * @return conversionMethod
     */
    private ConversionMethod getCurrentConversionMethod() {
        final String methodName = display.getMethodListBox().getItemText(
                display.getMethodListBox().getSelectedIndex());
        return ConversionMethod.getValueByName(methodName);
    }

    /**
     * Returns the selected line width as int.
     * @return lineWidth
     */
    private int getCurrentLineWidth() {
        final String widthString = display.getWidthListBox().getItemText(
                display.getWidthListBox().getSelectedIndex());
        int lineWidth = Integer.parseInt(widthString);
        if (currentImage.getWidth() < lineWidth) {
            lineWidth = currentImage.getWidth();
        }
        return lineWidth;
    }

    /**
     * Processes the conversion.
     */
    private void doDeferredConversion() {
        currentImage = ImageElement.as(image.getElement());
        method = getCurrentConversionMethod();
        final int width = getCurrentLineWidth();
        int height = 0;
        if (method.equals(ConversionMethod.FullHd)) {
            height = ((width / 2) * currentImage.getHeight()) / currentImage.getWidth();
        } else { //Super-Hybrid, Hybrid, Plain and Pwntari
            height = (width * currentImage.getHeight()) / currentImage.getWidth();
        }
        display.getCanvas().setWidth(String.valueOf(width) + "px");
        display.getCanvas().setHeight(String.valueOf(height) + "px");
        display.getCanvas().getContext2d().fillRect(0, 0, width, height); //resets the canvas with black bg
        display.getCanvas().getContext2d().drawImage(currentImage, 0, 0, width, height);

        final ImageData id = display.getCanvas().getContext2d().getImageData(0, 0, width, height);
        final String schemeName = display.getSchemeListBox().getItemText(display.getSchemeListBox().getSelectedIndex());
        final ColorScheme scheme = ColorScheme.valueOf(schemeName);
        final String presetName = display.getPresetListBox().getItemText(display.getPresetListBox().getSelectedIndex());
        final CharacterSet preset = CharacterSet.valueOf(presetName);

        final double sensitivity = Double.valueOf(Sample.HALF_RGB) / Double.valueOf(TOTAL_PERCENT);
        final int contrast = (int) ((Integer.valueOf(display.getContrastLabel().getText()) * sensitivity) + Sample.HALF_RGB);
        final int brightness = Integer.valueOf(display.getBrightnessLabel().getText());

        lastIndex = id.getHeight();
        engine.setParams(id, preset, getSelectedKick(), contrast, brightness);
        if (!method.equals(ConversionMethod.Plain)) {
            engine.prepareScheme(scheme);
        }
        ircOutput.clear();
        if (method.equals(ConversionMethod.FullHd)) {
            engine.generateHighDef();
        } else if (method.equals(ConversionMethod.SuperHybrid)) {
            engine.generateSuperHybrid();
        } else if (method.equals(ConversionMethod.Pwntari)) {
            engine.generatePwntari();
        } else if (method.equals(ConversionMethod.Hybrid)) {
            engine.generateHybrid();
        } else if (method.equals(ConversionMethod.Plain)) {
            engine.generatePlain();
        }
    }

    /**
     * Refreshes the progress display.
     * @param index
     */
    private void updateProgress(final int index) {
        final double percentDone = index * TOTAL_PERCENT / Integer.valueOf(lastIndex).doubleValue();
        display.getStatusLabel().setText("Converting image: " + NumberFormat.getFormat("##0").format(percentDone) + "%");
    }

    /**
     * Adds a line to the irc output TextBox.
     * @param ircLine
     * @param index
     */
    public final void addIrcOutputLine(final String ircLine, final int index) {
        updateProgress(index);
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                if (ircLine != null && !ircLine.isEmpty() && index < lastIndex) {
                    ircOutput.add(ircLine + "\n");
                }
                if (index >= lastIndex - 1) {
                    addOutput();
                } else {
                    if (method.equals(ConversionMethod.FullHd)) {
                        engine.generateHighDefLine(index + 1);
                    } else if (method.equals(ConversionMethod.SuperHybrid)) {
                        engine.generateSuperHybridLine(index + 2);
                    } else if (method.equals(ConversionMethod.Pwntari)) {
                        engine.generatePwntariLine(index + 2);
                    } else if (method.equals(ConversionMethod.Hybrid)) {
                        engine.generateHybridLine(index + 2);
                    } else if (method.equals(ConversionMethod.Plain)) {
                        engine.generatePlainLine(index + 2);
                    }
                    updatePreview(ircOutput);
                }
            }
        });
    }

    /**
     * Adds the finished output to the view.
     */
    private void addOutput() {
        final String[] htmlAndCss = updatePreview(ircOutput);

        //create and wrap image parts
        final Date now = new Date();
        final DateTimeFormat format = DateTimeFormat.getFormat("yyyy.MM.dd HH:mm:ss");
        final ImageInfo jenkemImageInfo = new ImageInfo(currentName, ircOutput.size(), getCurrentLineWidth(), format.format(now));
        final ImageHtml jenkemImageHtml = new ImageHtml(currentName, htmlAndCss[0]);
        final ImageCss jenkemImageCss = new ImageCss(currentName, htmlAndCss[1]);
        final StringBuilder irc = new StringBuilder();
        for (final String line : ircOutput) { irc.append(line); }
        final ImageIrc jenkemImageIrc = new ImageIrc(currentName, irc.toString());
        jenkemImage = new JenkemImage(jenkemImageInfo, jenkemImageHtml, jenkemImageCss, jenkemImageIrc);

        display.getIrcTextArea().setText(irc.toString());
        display.getIrcTextArea().selectAll();

        removeBusyIcon();
        isConversionRunnung = false;
    }

    /**
     * Updates the HTML preview and returns the generated HTML, CSS.
     * @param html
     * @param css
     * @return
     */
    private String[] updatePreview(final List<String> ircOutput) {
        final String[] htmlAndCss = htmlUtil.generateHtml(ircOutput, currentName, method);
        final String inlineCss = htmlUtil.prepareCssForInline(htmlAndCss[1]);
        final String inlineHtml = htmlUtil.prepareHtmlForInline(htmlAndCss[0], inlineCss);
        display.getPreviewHtml().setHTML(inlineHtml);
        return htmlAndCss;
    }

    /**
     * Displays the icon for when the application is busy.
     */
    private void displayBusyIcon() {
        display.getBusyPanel().clear();
        display.getBusyPanel().add(busyImage);
        display.getMethodListBox().setEnabled(false);
        display.getWidthListBox().setEnabled(false);
        display.getResetButton().setEnabled(false);
        display.getSchemeListBox().setEnabled(false);
        display.getPresetListBox().setEnabled(false);
        display.getSubmitButton().setEnabled(false);
        disableKicks();
    }

    /**
     * Removes the busy icon.
     */
    private void removeBusyIcon() {
        display.getBusyPanel().clear();
        display.getStatusLabel().setText("Enter URL to an image: ");
        display.getMethodListBox().setEnabled(true);
        display.getWidthListBox().setEnabled(true);
        display.getResetButton().setEnabled(true);
        if (!method.equals(ConversionMethod.Plain)) {
            display.getSchemeListBox().setEnabled(true);
        }
        if (!method.equals(ConversionMethod.Pwntari)) {
            display.getPresetListBox().setEnabled(true);
        }
        display.getSubmitButton().setEnabled(true);
        enableKicks();
    }

    /**
     * Returns the selected Kick.
     * @return kick
     */
    private Kick getSelectedKick() {
        for (final Kick kick : Kick.values()) {
            if (this.display.getKickButton(kick).getValue()) {
                return kick;
            }
        }
        return Kick.Off;
    }

    /**
     * Resets the view.
     */
    private void doReset() {
        display.getSchemeListBox().setSelectedIndex(0);
        display.getPresetListBox().setSelectedIndex(0);
        resetContrastAndBrightness();
        display.getKickButton(Kick.Off).setValue(true);
    }

    /**
     * Resets the constrast and the brightness.
     */
    private void resetContrastAndBrightness() {
        method = getCurrentConversionMethod();
        display.getContrastSlider().setValue(display.getInitialContrast());
        updateContrast();
        display.getBrightnessSlider().setValue(display.getInitialBrightness());
        updateBrightness();
    }

    /**
     * Disables the Kick RadioButtons.
     */
    private void disableKicks() {
        for (final Kick kick : Kick.values()) {
            display.getKickButton(kick).setEnabled(false);
        }
    }

    /**
     * Enables the Kick RadionButtons.
     */
    private void enableKicks() {
        if (!method.equals(ConversionMethod.FullHd)) {
            for (final Kick kick : Kick.values()) {
                display.getKickButton(kick).setEnabled(true);
            }
        }
    }

    /**
     * Updates the contrast with the provided value.
     * @param value
     */
    private void updateContrast() {
        final int value = display.getContrastSlider().getValue() - display.getInitialContrast();
        display.getContrastLabel().setText(String.valueOf(value));
    }

    /**
     * Updates the brightness with the provided value.
     * @param value
     */
    private void updateBrightness() {
        final int value = display.getBrightnessSlider().getValue() - display.getInitialBrightness();
        display.getBrightnessLabel().setText(String.valueOf(value));
    }
}
