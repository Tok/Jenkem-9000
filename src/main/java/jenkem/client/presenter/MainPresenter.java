package jenkem.client.presenter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jenkem.client.ClientAsciiEngine;
import jenkem.client.event.DoConversionEvent;
import jenkem.client.event.DoConversionEventHandler;
import jenkem.client.event.SendToIrcEvent;
import jenkem.client.event.SendToIrcEventHandler;
import jenkem.client.service.JenkemServiceAsync;
import jenkem.client.widget.CropPanel;
import jenkem.client.widget.IrcColorSetter;
import jenkem.client.widget.IrcConnector;
import jenkem.client.widget.UrlSetter;
import jenkem.shared.CharacterSet;
import jenkem.shared.ColorScheme;
import jenkem.shared.ConversionMethod;
import jenkem.shared.HtmlUtil;
import jenkem.shared.ImageUtil;
import jenkem.shared.Kick;
import jenkem.shared.Power;
import jenkem.shared.ProcessionSettings;
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
    private static final String INVISIBLE = "invisible";
    //private final Logger logger = Logger.getLogger(this.getClass().getName());

    private final JenkemServiceAsync jenkemService;
    private final HtmlUtil htmlUtil = new HtmlUtil();
    private final ClientAsciiEngine engine;
    private final Display display;

    private ConversionMethod method;
    private final List<String> ircOutput = new ArrayList<String>();

    private Image image;
    private Map<String, Integer[]> imageRgb;
    private int width;
    private int height; //=lastIndex
    private String currentName;
    private static JenkemImage jenkemImage;

    private boolean makeInitsForImage = true;
    private boolean isConversionRunnung = false;
    private boolean restartConversion = false;
    private boolean isReady = false;

    public interface Display {
        UrlSetter getUrlSetter();
        InlineHTML getPreviewHtml();
        IrcColorSetter getIrcColorSetter();
        TextArea getIrcTextArea();
        IrcConnector getIrcConnector();
        ListBox getMethodListBox();
        ListBox getWidthListBox();
        ListBox getPowerListBox();
        ListBox getPresetListBox();
        TextBox getPresetTextBox();
        Button getResetButton();
        Button getSubmitButton();
        SliderBarSimpleHorizontal getContrastSlider();
        SliderBarSimpleHorizontal getBrightnessSlider();
        boolean isDefaultBgBlack();
        RadioButton getBlackBgButton();
        RadioButton getWhiteBgButton();
        Label getContrastLabel();
        Label getBrightnessLabel();
        ProcessionSettings getProcessionSettings();
        int getInitialContrast();
        int getInitialBrightness();
        RadioButton getKickButton(Kick kick);
        Canvas getCanvas();
        Widget asWidget();
        void resetProcession();
        void enableProcession(boolean enable);
        void makeWidgetsReady();
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
                display.getPresetListBox().setEnabled(!method.equals(ConversionMethod.Pwntari));
                display.getPresetTextBox().setEnabled(!method.equals(ConversionMethod.Pwntari));
                display.enableProcession(method.equals(ConversionMethod.SuperHybrid)
                        || method.equals(ConversionMethod.Hybrid)
                        || method.equals(ConversionMethod.Plain));
                startOrRestartConversion();
            }});
        this.display.getWidthListBox().addChangeHandler(new ChangeHandler() {
            @Override public void onChange(final ChangeEvent event) {
                startOrRestartConversion();
            }});
        this.display.getPresetListBox().addChangeHandler(new ChangeHandler() {
            @Override public void onChange(final ChangeEvent event) {
                final String presetName = display.getPresetListBox().getItemText(display.getPresetListBox().getSelectedIndex());
                final CharacterSet preset = CharacterSet.valueOf(presetName);
                display.getPresetTextBox().setText(preset.getCharacters());
                startOrRestartConversion();
            }});
        this.display.getPresetTextBox().addChangeHandler(new ChangeHandler() {
            @Override public void onChange(final ChangeEvent event) {
                final String charset = display.getPresetTextBox().getText();
                final String fixed = charset.replaceAll("[,0-9]", ""); //remove numeric and comma.
                if (!fixed.equals(charset)) {
                    display.getPresetTextBox().setText(fixed);
                }
                startOrRestartConversion();
            }});
        this.display.getPowerListBox().addChangeHandler(new ChangeHandler() {
            @Override public void onChange(final ChangeEvent event) {
                startOrRestartConversion();
            }});
        this.display.getBlackBgButton().addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override public void onValueChange(final ValueChangeEvent<Boolean> event) {
                makeInitsForImage = true;
                startOrRestartConversion();
            }});
        this.display.getWhiteBgButton().addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override public void onValueChange(final ValueChangeEvent<Boolean> event) {
                makeInitsForImage = true;
                startOrRestartConversion();
            }});
        this.display.getResetButton().addClickHandler(new ClickHandler() {
            @Override public void onClick(final ClickEvent event) {
                doReset();
                startOrRestartConversion();
            }});
        this.display.getContrastSlider().addBarValueChangedHandler(new BarValueChangedHandler() {
            @Override public void onBarValueChanged(final BarValueChangedEvent event) {
                updateContrast();
                if (isReady) {
                    startOrRestartConversion();
                }
            }});
        this.display.getBrightnessSlider().addBarValueChangedHandler(new BarValueChangedHandler() {
            @Override public void onBarValueChanged(final BarValueChangedEvent event) {
                updateBrightness();
                if (isReady) {
                    startOrRestartConversion();
                }
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

    /**
     * Shows submission message and enables submission button.
     * @param result
     */
    private synchronized void handleSubmissionResult(final String result) {
        display.getUrlSetter().setStatus(result);
        display.getSubmitButton().setEnabled(true);
    }

    /**
     * Proxifies the image and calls the show method.
     */
    public final synchronized void proxifyAndConvert() {
        final String urlString = display.getUrlSetter().getUrl();
        final String proxifiedUrl = proxify(urlString);
        doShow(proxifiedUrl);
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
        makeInitsForImage = true;
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

        // Image must be added to dom in order for load event to fire.
        RootPanel.get(INVISIBLE).clear();
        RootPanel.get(INVISIBLE).setVisible(false);
        RootPanel.get(INVISIBLE).add(image);

        image.addErrorHandler(new ErrorHandler() {
            @Override public void onError(final ErrorEvent event) {
                display.getUrlSetter().setStatus("Proxifying this image failed.");
                display.getUrlSetter().makeBusy(false);
            }});
        image.addLoadHandler(new LoadHandler() {
            @Override public void onLoad(final LoadEvent event) {
                final int width = Integer.parseInt(display.getWidthListBox().getItemText(display.getWidthListBox().getSelectedIndex()));
                display.getUrlSetter().addImage(image, width);
                display.getUrlSetter().setStatus("Image loaded.");
                doConversion();
            }});
    }

    /**
     * Defers the conversion.
     */
    private synchronized void doConversion() {
        isReady = false;
        if (image == null || !image.isAttached()) { return; }
        if (!isConversionRunnung || restartConversion) {
            isConversionRunnung = true;
            restartConversion = false;
            makeBusy(true);
            doDeferredConversion();
        }
    }

    /**
     * Returns the selected conversion method.
     * @return conversionMethod
     */
    private synchronized ConversionMethod getCurrentConversionMethod() {
        final String methodName = display.getMethodListBox().getItemText(
                display.getMethodListBox().getSelectedIndex());
        return ConversionMethod.getValueByName(methodName);
    }

    /**
     * Returns the selected line width as int.
     * @return lineWidth
     */
    private synchronized int getCurrentLineWidth(final int imgWidth) {
      final String widthString = display.getWidthListBox().getItemText(
              display.getWidthListBox().getSelectedIndex());
      return Math.min(Integer.parseInt(widthString), imgWidth);
    }

    /**
     * Processes the conversion.
     */
    private synchronized void doDeferredConversion() {
        final int left = display.getUrlSetter().getCrop(CropPanel.Type.Left);
        final int top = display.getUrlSetter().getCrop(CropPanel.Type.Top);
        final int right = display.getUrlSetter().getCrop(CropPanel.Type.Right);
        final int bottom = display.getUrlSetter().getCrop(CropPanel.Type.Bottom);

        final int w = getCurrentLineWidth(image.getWidth());
        final int divisor = method.hasKick() ? 1 : 2;
        final int h = ((w / divisor) * image.getHeight()) / image.getWidth();

        final int actualWidth = w * TOTAL_PERCENT / ((TOTAL_PERCENT - right) - left);
        final int actualHeight = h * TOTAL_PERCENT / ((TOTAL_PERCENT - top) - bottom);
        image.setPixelSize(actualWidth, actualHeight);
        image.setVisible(true);

        final ImageElement currentImage = ImageElement.as(image.getElement());

        final int widthQ = getCurrentLineWidth(currentImage.getWidth());
        final int heightQ = Double.valueOf(((widthQ / divisor) * currentImage.getHeight()) / Double.valueOf(currentImage.getWidth())).intValue();

        display.getCanvas().setWidth(String.valueOf(actualWidth) + "px");
        display.getCanvas().setHeight(String.valueOf(actualHeight) + "px");
        display.getCanvas().getContext2d().setFillStyle((display.isDefaultBgBlack() ? "#000000" : "#FFFFFF"));
        display.getCanvas().getContext2d().fillRect(0, 0, actualWidth, actualHeight);
        display.getCanvas().getContext2d().drawImage(currentImage, 0, 0, actualWidth, actualHeight);
        final int xOff = actualWidth * left / TOTAL_PERCENT;
        final int yOff = actualHeight * top / TOTAL_PERCENT;
        final String charset = display.getPresetTextBox().getText().replaceAll("[,0-9]", "");

        final Kick kick = getSelectedKick();
        final ImageData id = display.getCanvas().getContext2d().getImageData(
                xOff + kick.getXOffset(), yOff + kick.getYOffset(),
                widthQ - (2 * kick.getXOffset()), heightQ - (2 * kick.getYOffset()));
        height = id.getHeight();
        width = id.getWidth();
        final double hashMapLoadFactor = 0.75D;
        final int initialCapacity = Double.valueOf((height * width) / hashMapLoadFactor).intValue();
        imageRgb = new HashMap<String, Integer[]>(initialCapacity);
        for (int row = 0; row < height; row++) { //TODO move this elsewhere.
            for (int col = 0; col < width; col++) {
                final Integer[] rgb = {
                    id.getRedAt(col, row),
                    id.getGreenAt(col, row),
                    id.getBlueAt(col, row)
                };
                imageRgb.put(row + ":" + col, rgb);
            }
        }

        if (makeInitsForImage) {
            final boolean restart = determineDefaultsForImage(imageRgb, width, height);
            if (restart) { doDeferredConversion(); return; }
            makeInitsForImage = false;
        }

        final int contrast = Integer.valueOf(display.getContrastLabel().getText());
        final int brightness = Integer.valueOf(display.getBrightnessLabel().getText());
        final ProcessionSettings settings = display.getProcessionSettings();
        engine.setParams(imageRgb, width, charset, contrast, brightness, settings);
        if (!getCurrentConversionMethod().equals(ConversionMethod.Plain)) {
            engine.prepareEngine(display.getIrcColorSetter().getColorMap(), getSelectedPower());
        }

        ircOutput.clear();
        engine.generate(method);

        isReady = true;
        display.makeWidgetsReady(); //XXX
    }

    /**
     * Determines default values for the current image and returns true
     * if the deferred Conversions should be restarted. When this method is called
     * from elsewhere, the return value can be ignored.
     * @param imageRgb
     * @param width
     * @param height
     * @return restartConversion
     */
    private synchronized boolean determineDefaultsForImage(final Map<String, Integer[]> imageRgb,
            final int width, final int height) {
        boolean restartConversion = false;
        // select default method
        final ConversionMethod defaultMethod = ImageUtil.getDefaultMethod(imageRgb, width, height);
        if (defaultMethod != method) {
            for (int i = 0; i < display.getMethodListBox().getItemCount(); i++) {
                if (display.getMethodListBox().getItemText(i).equals(defaultMethod.getName())) {
                    display.getMethodListBox().setSelectedIndex(i);
                    method = getCurrentConversionMethod();
                    restartConversion = true;
                    break;
                }
            }
        }
        if (!method.equals(ConversionMethod.Plain)) {
            // select default brightness
            final int defaultBrightness = ImageUtil.getDefaultBrightness(imageRgb, width, height);
            display.getBrightnessSlider().setValue(defaultBrightness);
            // select default contrast
            final int defaultContrast = ImageUtil.getDefaultContrast(imageRgb, width, height);
            display.getContrastSlider().setValue(defaultContrast);
        }
        // select default color scheme
        final ColorScheme defaultScheme = ImageUtil.getDefaultColorScheme(imageRgb, width, height);
        display.getIrcColorSetter().setSelectedScheme(defaultScheme);
        return restartConversion;
    }

    /**
     * Refreshes the progress display.
     * @param index
     */
    private synchronized void updateProgress(final int index) {
        final double percentDone = index * TOTAL_PERCENT / Integer.valueOf(height).doubleValue();
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
            return;
        } else if (isConversionRunnung) {
            updateProgress(index);
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override public void execute() {
                    if (ircLine == null) {
                        addOutput();
                    } else {
                        ircOutput.add(ircLine + "\n");
                        updatePreview(ircOutput);
                        final int nextIndex = index + method.getStep();
                        if (nextIndex < height) {
                            engine.generateLine(method, nextIndex);
                        } else { // end conversion
                            addOutput();
                        }
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
    private synchronized void addOutput() {
        final String[] htmlAndCss = updatePreview(ircOutput);

        //create and wrap image parts
        final Date now = new Date();
        final DateTimeFormat format = DateTimeFormat.getFormat("yyyy.MM.dd HH:mm:ss");
        final ImageInfo jenkemImageInfo = new ImageInfo(currentName, ircOutput.size(), getCurrentLineWidth(width), format.format(now));
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
    private synchronized String[] updatePreview(final List<String> ircOutput) {
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
        display.getPresetTextBox().setEnabled(!method.equals(ConversionMethod.Pwntari));
        if (!isBusy) {
            display.getUrlSetter().setStatus("Enter URL to an image: ");
        }
    }

    /**
     * Returns the selected Kick.
     * @return kick
     */
    private synchronized Kick getSelectedKick() {
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
    private synchronized Power getSelectedPower() {
        final String powerName = display.getPowerListBox().getItemText(
                display.getPowerListBox().getSelectedIndex());
        return Power.valueOf(powerName);
    }

    /**
     * Resets the view.
     */
    private synchronized void doReset() {
        display.getIrcColorSetter().reset();
        display.getPresetListBox().setSelectedIndex(0); //hard
        display.getPowerListBox().setSelectedIndex(1); //quadratic
        resetContrastAndBrightness();
        display.getKickButton(Kick.Off).setValue(true);
        display.resetProcession();
        determineDefaultsForImage(imageRgb, width, height);
    }

    /**
     * Resets the constrast and the brightness.
     */
    private synchronized void resetContrastAndBrightness() {
        method = getCurrentConversionMethod();
        display.getContrastSlider().setValue(display.getInitialContrast());
        updateContrast();
        display.getBrightnessSlider().setValue(display.getInitialBrightness());
        updateBrightness();
    }

    /**
     * Enables or disables the Kick RadionButtons.
     */
    private synchronized void setKicksEnabled(final boolean enabled) {
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
    private synchronized void updateContrast() {
        final int value = display.getContrastSlider().getValue() - display.getInitialContrast();
        display.getContrastLabel().setText(String.valueOf(value));
    }

    /**
     * Updates the brightness with the provided value.
     * @param value
     */
    private synchronized void updateBrightness() {
        final int value = display.getBrightnessSlider().getValue() - display.getInitialBrightness();
        display.getBrightnessLabel().setText(String.valueOf(value));
    }
}
