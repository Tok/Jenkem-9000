package jenkem.client.presenter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import jenkem.client.ClientAsciiEngine;
import jenkem.client.event.DoConversionEvent;
import jenkem.client.event.DoConversionEventHandler;
import jenkem.client.event.SendToIrcEvent;
import jenkem.client.event.SendToIrcEventHandler;
import jenkem.client.service.JenkemServiceAsync;
import jenkem.client.widget.IrcColorSetter;
import jenkem.client.widget.IrcConnector;
import jenkem.client.widget.UrlSetter;
import jenkem.shared.CharacterSet;
import jenkem.shared.ConversionMethod;
import jenkem.shared.HtmlUtil;
import jenkem.shared.Kick;
import jenkem.shared.Power;
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
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
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
    private final ClientAsciiEngine engine;
    private final Display display;

    private ConversionMethod method;
    private final List<String> ircOutput = new ArrayList<String>();
    private int lastIndex;

    private Image image;
    private ImageElement currentImage;
    private String currentName;
    private static JenkemImage jenkemImage;

    private boolean isConversionRunnung = false;
    private boolean restartConversion = false;

    public interface Display {
        UrlSetter getUrlSetter();
        InlineHTML getPreviewHtml();
        IrcColorSetter getIrcColorSetter();
        TextArea getIrcTextArea();
        IrcConnector getIrcConnector();
        ListBox getMethodListBox();
        ListBox getWidthListBox();
        ListBox getPresetListBox();
        ListBox getPowerListBox();
        Button getResetButton();
        Button getSubmitButton();
        SliderBarSimpleHorizontal getContrastSlider();
        SliderBarSimpleHorizontal getBrightnessSlider();
        Label getContrastLabel();
        Label getBrightnessLabel();
        int getInitialContrast();
        int getInitialBrightness();
        RadioButton getKickButton(Kick kick);
        Canvas getCanvas();
        Widget asWidget();
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
        this.engine = new ClientAsciiEngine(this);
    }

    @Override public final void go(final HasWidgets container) {
        bind();
        container.clear();
        container.add(super.getTabPanel());
        doReset();
    }

    /**
     * Binds the elements from the view.
     */
    public final void bind() {
        getEventBus().addHandler(SendToIrcEvent.TYPE, new SendToIrcEventHandler() {
            @Override public void onSend(final SendToIrcEvent event) {
                display.getIrcConnector().sendMessage(ircOutput); }
        });
        getEventBus().addHandler(DoConversionEvent.TYPE, new DoConversionEventHandler() {
            @Override public void onDoConversion(final DoConversionEvent event) {
                if (event.proxify()) { proxifyAndConvert(); } else { startOrRestartConversion(); }
            }});
        this.display.getMethodListBox().addChangeHandler(new ChangeHandler() {
            @Override public void onChange(final ChangeEvent event) {
                method = getCurrentConversionMethod();
                setKicksEnabled(!method.equals(ConversionMethod.FullHd));
                display.getIrcColorSetter().setEnabled(!method.equals(ConversionMethod.Plain));
                if (!method.equals(ConversionMethod.Pwntari)) {
                    display.getPresetListBox().setEnabled(true);
                }
                resetContrastAndBrightness();
                startOrRestartConversion();
            }});
        this.display.getWidthListBox().addChangeHandler(new ChangeHandler() {
            @Override public void onChange(final ChangeEvent event) {
                startOrRestartConversion();
            }});
        this.display.getPresetListBox().addChangeHandler(makeConversionChangeHandler());
        this.display.getPowerListBox().addChangeHandler(makeConversionChangeHandler());
        this.display.getResetButton().addClickHandler(new ClickHandler() {
            @Override public void onClick(final ClickEvent event) {
                doReset();
                startOrRestartConversion();
            }});
        this.display.getContrastSlider().addBarValueChangedHandler(new BarValueChangedHandler() {
            @Override public void onBarValueChanged(final BarValueChangedEvent event) {
                updateContrast();
                startOrRestartConversion();
            }});
        this.display.getBrightnessSlider().addBarValueChangedHandler(new BarValueChangedHandler() {
            @Override public void onBarValueChanged(final BarValueChangedEvent event) {
                updateBrightness();
                startOrRestartConversion();
            }});
        for (final Kick kick : Kick.values()) {
            this.display.getKickButton(kick).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                @Override public void onValueChange(final ValueChangeEvent<Boolean> event) {
                    if (event.getValue()) { startOrRestartConversion(); }
                }});
        }
        this.display.getSubmitButton().addClickHandler(new ClickHandler() {
            @Override public void onClick(final ClickEvent event) {
                synchronized (this) {
                    display.getSubmitButton().setEnabled(false); //prevent double clicks
                    jenkemService.saveJenkemImage(jenkemImage, new AsyncCallback<Void>() {
                        @Override public void onFailure(final Throwable caught) {
                            handleSubmissionResult("Fail submitting conversion.");
                        }
                        @Override public void onSuccess(final Void result) {
                            handleSubmissionResult("Conversion submitted successfully.");
                        }
                    });
                }
            }});
    }

    private ChangeHandler makeConversionChangeHandler() {
        return new ChangeHandler() { @Override public void onChange(final ChangeEvent event) { startOrRestartConversion(); }};
    }

    /**
     * Shows submission message and enables submission button.
     * @param result
     */
    private void handleSubmissionResult(final String result) {
        display.getUrlSetter().setStatus(result);
        display.getSubmitButton().setEnabled(true);
    }

    /**
     * Proxifies the image and calls the show method.
     */
    public final synchronized void proxifyAndConvert() {
        final String urlString = display.getUrlSetter().getUrl();
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
    private synchronized String proxify(final String urlString) {
        display.getUrlSetter().setStatus("Proxifying image.");
        final String[] split = urlString.split("/");
        currentName = split[split.length - 1];
        return ("".equals(urlString)) ? "" : "http://" + Window.Location.getHost() + "/jenkem/image?url=" + urlString;
    }

    /**
     * Shows the proxified image.
     * @param url to the image
     */
    private synchronized void doShow(final String proxifiedUrl) {
        Image.prefetch(proxifiedUrl);
        if (!"".equals(proxifiedUrl)) { makeBusy(true); }
        image = new Image(proxifiedUrl);
        image.setVisible(false);
        image.addErrorHandler(new ErrorHandler() {
            @Override public void onError(final ErrorEvent event) {
                display.getUrlSetter().setStatus("Proxifying this image failed.");
                display.getUrlSetter().makeBusy(false);
            }});
        image.addLoadHandler(new LoadHandler() {
            @Override public void onLoad(final LoadEvent event) {
                final int width = Integer.parseInt(display.getWidthListBox().getItemText(display.getWidthListBox().getSelectedIndex()));
                final int height = image.getHeight() * width / image.getWidth();
                image.setPixelSize(width, height);
                display.getUrlSetter().addImage(image, width);
                display.getUrlSetter().setStatus("Image loaded.");
                //startOrRestartConversion();
                doConversion();
            }});
        // Image must be added to dom in order for load event to fire.
        RootPanel.get("invisible").clear();
        RootPanel.get("invisible").setVisible(false);
        RootPanel.get("invisible").add(image);
    }

    /**
     * Defers the conversion.
     */
    private synchronized void doConversion() {
        if (image == null) { return; }
        if (!isConversionRunnung || restartConversion) {
            isConversionRunnung = true;
            restartConversion = false;
            makeBusy(true);
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override public void execute() {
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
      return Math.min(Integer.parseInt(widthString), currentImage.getWidth());
    }

    /**
     * Processes the conversion.
     */
    private synchronized void doDeferredConversion() {
        currentImage = ImageElement.as(image.getElement());
        method = getCurrentConversionMethod();
        final int width = getCurrentLineWidth();
        final int divisor = method.hasKick() ? 1 : 2;
        final int height = ((width / divisor) * currentImage.getHeight()) / currentImage.getWidth();
        display.getCanvas().setWidth(String.valueOf(width) + "px");
        display.getCanvas().setHeight(String.valueOf(height) + "px");
        display.getCanvas().getContext2d().fillRect(0, 0, width, height); //resets the canvas with black bg
        display.getCanvas().getContext2d().drawImage(currentImage, 0, 0, width, height);

        final ImageData id = display.getCanvas().getContext2d().getImageData(0, 0, width, height);

        final String presetName = display.getPresetListBox().getItemText(display.getPresetListBox().getSelectedIndex());
        final CharacterSet preset = CharacterSet.valueOf(presetName);

        final int contrast = Integer.valueOf(display.getContrastLabel().getText());
        final int brightness = Integer.valueOf(display.getBrightnessLabel().getText());

        lastIndex = id.getHeight();
        if (!method.equals(ConversionMethod.Plain)) { //FIXME ugly workaround
            engine.setParams(id, preset, getSelectedKick(), contrast, brightness);
            engine.prepareEngine(display.getIrcColorSetter().getColorMap(), getSelectedPower());
        } else {
            engine.setParams(id, preset, getSelectedKick(), contrast + 1, brightness);
        }
        ircOutput.clear();
        engine.generate(method);
    }

    /**
     * Refreshes the progress display.
     * @param index
     */
    private void updateProgress(final int index) {
        final double percentDone = index * TOTAL_PERCENT / Integer.valueOf(lastIndex).doubleValue();
        display.getUrlSetter().setStatus("Converting image: " + NumberFormat.getFormat("##0").format(percentDone) + "%");
    }

    /**
     * Adds a line to the irc output TextBox.
     * @param ircLine
     * @param index
     */
    public final synchronized void addIrcOutputLine(final String ircLine, final int index) {
        if (restartConversion) {
            restartConversion = false;
            isConversionRunnung = false;
            doConversion();
        } else if (isConversionRunnung) {
            updateProgress(index);
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override public void execute() {
                    if (ircLine != null && !ircLine.isEmpty() && index < lastIndex) {
                        ircOutput.add(ircLine + "\n");
                    }
                    if (index >= lastIndex - 1) {
                        addOutput();
                    } else {
                        engine.generateLine(method, method.hasKick() ? index + 2 : index + 1);
                        updatePreview(ircOutput);
                    }
                }
            });
        }
    }

    public final synchronized void startOrRestartConversion() {
        if (isConversionRunnung) { restartConversion = true; } else { doConversion(); }
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

        makeBusy(false);
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

    private void makeBusy(final boolean isBusy) {
        display.getUrlSetter().makeBusy(isBusy);
        display.getIrcColorSetter().setEnabled(!method.equals(ConversionMethod.Plain));
        display.getPresetListBox().setEnabled(!method.equals(ConversionMethod.Pwntari));
        if (!isBusy) {
            display.getUrlSetter().setStatus("Enter URL to an image: ");
        }
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
     * Returns the selected Power.
     * @return power
     */
    private Power getSelectedPower() {
        final String powerName = display.getPowerListBox().getItemText(
                display.getPowerListBox().getSelectedIndex());
        return Power.valueOf(powerName);
    }

    /**
     * Resets the view.
     */
    private void doReset() {
        display.getIrcColorSetter().reset();
        display.getPresetListBox().setSelectedIndex(0); //hard
        display.getPowerListBox().setSelectedIndex(2); //cubic
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
     * Enables or disables the Kick RadionButtons.
     */
    private void setKicksEnabled(final boolean enabled) {
        if (!enabled || !method.equals(ConversionMethod.FullHd)) {
            for (final Kick kick : Kick.values()) {
                display.getKickButton(kick).setEnabled(enabled);
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
