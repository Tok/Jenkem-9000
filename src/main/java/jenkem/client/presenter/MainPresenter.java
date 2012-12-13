package jenkem.client.presenter;

import java.util.Date;
import jenkem.client.service.JenkemServiceAsync;
import jenkem.shared.CharacterSet;
import jenkem.shared.ColorScheme;
import jenkem.shared.ConversionMethod;
import jenkem.shared.Engine;
import jenkem.shared.HtmlUtil;
import jenkem.shared.Kick;
import jenkem.shared.data.JenkemImageCss;
import jenkem.shared.data.JenkemImageHtml;
import jenkem.shared.data.JenkemImageInfo;
import jenkem.shared.data.JenkemImageIrc;
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
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
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
    private static final double CONTRAST_DIVISOR = 100.0;
    private static final int TOTAL_PERCENT = 100;
    private static final int INITIAL_BRIGHNESS = 100;
    private static final int INITIAL_CONTRAST_FOR_PLAIN = 89;
    private static final int INITIAL_CONTRAST = 59;

    private final JenkemServiceAsync jenkemService;

    private Engine engine;
    private ConversionMethod method;
    private String[] ircOutput;
    private int lastIndex;

    private final HtmlUtil htmlUtil = new HtmlUtil();

    private final Display display;

    private final Image busyImage = new Image("/images/busy.gif");

    private Image image;
    private ImageElement currentImage;
    private String currentImageName;

    private static final JenkemImageInfo jenkemImageInfo = new JenkemImageInfo();
    private static final JenkemImageHtml jenkemImageHtml = new JenkemImageHtml();
    private static final JenkemImageCss jenkemImageCss = new JenkemImageCss();
    private static final JenkemImageIrc jenkemImageIrc = new JenkemImageIrc();

    private boolean readyForSlider = false;
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
    }

    /**
     * Binds the elements from the view.
     */
    public final void bind() {
        this.display.getInputTextBox().addKeyPressHandler(
                new KeyPressHandler() {
                    @Override
                    public void onKeyPress(final KeyPressEvent event) {
                        if (event.getCharCode() == KeyCodes.KEY_ENTER) {
                            replaceUrl();
                        }
                    }
                });
        this.display.getShowButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                replaceUrl();
            }
        });
        this.display.getMethodListBox().addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(final ChangeEvent event) {
                method = getCurrentConversionMethod();
                if (method.equals(ConversionMethod.FullHd)) {
                    disableKicks();
                } else {
                    enableKicks();
                }
                if (method.equals(ConversionMethod.Plain)) {
                    display.getSchemeListBox().setEnabled(false);
                } else {
                    display.getSchemeListBox().setEnabled(true);
                }
                if (!method.equals(ConversionMethod.Pwntari)) {
                    display.getPresetListBox().setEnabled(true);
                } else {
                    display.getPresetListBox().setEnabled(true);
                }
                resetContrastAndBrightness();
                replaceUrl();
            }
        });
        this.display.getWidthListBox().addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(final ChangeEvent event) {
                replaceUrl();
            }
        });
        this.display.getSchemeListBox().addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(final ChangeEvent event) {
                doConversion();
            }
        });
        this.display.getPresetListBox().addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(final ChangeEvent event) {
                doConversion();
            }
        });
        this.display.getResetButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                doReset();
                doConversion();
            }
        });
        this.display.getContrastSlider().addMouseOverHandler(
                new MouseOverHandler() {
                    @Override
                    public void onMouseOver(final MouseOverEvent event) {
                        readyForSlider = true;
                    }
                });
        this.display.getContrastSlider().addBarValueChangedHandler(
                new BarValueChangedHandler() {
                    @Override
                    public void onBarValueChanged(
                            final BarValueChangedEvent event) {
                        if (readyForSlider) {
                            updateContrast(event.getValue());
                            doConversion();
                            readyForSlider = false;
                        }
                    }
                });
        this.display.getBrightnessSlider().addMouseOverHandler(
                new MouseOverHandler() {
                    @Override
                    public void onMouseOver(final MouseOverEvent event) {
                        readyForSlider = true;
                    }
                });
        this.display.getBrightnessSlider().addBarValueChangedHandler(
                new BarValueChangedHandler() {
                    @Override
                    public void onBarValueChanged(
                            final BarValueChangedEvent event) {
                        if (readyForSlider) {
                            updateBrightness(event.getValue());
                            doConversion();
                            readyForSlider = false;
                        }
                    }
                });
        for (final Kick kick : Kick.values()) {
            this.display.getKickButton(kick).addValueChangeHandler(
                    new ValueChangeHandler<Boolean>() {
                        @Override
                        public void onValueChange(
                                final ValueChangeEvent<Boolean> event) {
                            if (event.getValue()) {
                                doConversion();
                            }
                        }
                    });
        }
        this.display.getSubmitButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                synchronized (this) {
                    jenkemService.saveJenkemImage(jenkemImageInfo,
                            jenkemImageHtml, jenkemImageCss, jenkemImageIrc,
                            new AsyncCallback<Void>() {
                                @Override
                                public void onFailure(final Throwable caught) {
                                    display.getStatusLabel().setText("Fail submitting conversion.");
                                }
                                @Override
                                public void onSuccess(final Void result) {
                                    display.getStatusLabel().setText("Conversion submitted successfully.");
                                }
                            });
                }
            }
        });
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
            @Override
            public void execute() {
                doShow(proxifiedUrl);
            }
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
        if (!"".equals(urlString)) {
            return "http://" + Window.Location.getHost() + "/jenkem/image?url=" + urlString;
        } else {
            return "";
        }
    }

    /**
     * Updates the name of the image from the provided url.
     * @param urlString
     */
    private void updateImageName(final String urlString) {
        final String[] split = urlString.split("/");
        currentImageName = split[split.length - 1];
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
        if (!"".equals(url)) {
            displayBusyIcon();
        }
        image = new Image();
        image.addErrorHandler(new ErrorHandler() {
            @Override
            public void onError(final ErrorEvent event) {
                display.getStatusLabel().setText(
                        "Proxifying this image failed.");
                removeBusyIcon();
            }
        });
        image.addLoadHandler(new LoadHandler() {
            @Override
            public void onLoad(final LoadEvent event) {
                display.getStatusLabel().setText("Image loaded.");
                doConversion();
            }
        });
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
        if (image == null) {
            return;
        }
        if (!isConversionRunnung) {
            isConversionRunnung = true;
            displayBusyIcon();
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                public void execute() {
                    doDeferredConversion();
                }
            });
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
        engine = new Engine(this);
        currentImage = ImageElement.as(image.getElement());
        method = getCurrentConversionMethod();
        final int width = getCurrentLineWidth();
        int height = 0;
        if (method.equals(ConversionMethod.FullHd)) {
            height = ((width / 2) * currentImage.getHeight()) / currentImage.getWidth();
        } else { // Super-Hybrid, Hybrid, Plain and Pwntari
            height = (width * currentImage.getHeight()) / currentImage.getWidth();
        }
        display.getCanvas().setWidth(String.valueOf(width) + "px");
        display.getCanvas().setHeight(String.valueOf(height) + "px");
        display.getCanvas().getContext2d().fillRect(0, 0, width, height); // resets the canvas with black bg
        display.getCanvas().getContext2d().drawImage(currentImage, 0, 0, width, height);

        final ImageData id = display.getCanvas().getContext2d().getImageData(0, 0, width, height);
        final String schemeName = display.getSchemeListBox().getItemText(display.getSchemeListBox().getSelectedIndex());
        final ColorScheme scheme = ColorScheme.valueOf(schemeName);
        final String presetName = display.getPresetListBox().getItemText(display.getPresetListBox().getSelectedIndex());
        final CharacterSet preset = CharacterSet.valueOf(presetName);
        final double contrast = Double.valueOf(display.getContrastLabel().getText());
        final int brightness = Integer.valueOf(display.getBrightnessLabel().getText());

        lastIndex = id.getHeight();
        if (method.equals(ConversionMethod.FullHd)) {
            ircOutput = new String[lastIndex];
            engine.generateHighDef(id, scheme, preset, contrast, brightness);
        } else if (method.equals(ConversionMethod.SuperHybrid)) {
            ircOutput = new String[lastIndex];
            engine.generateSuperHybrid(id, scheme, preset, contrast, brightness, getSelectedKick());
        } else if (method.equals(ConversionMethod.Pwntari)) {
            ircOutput = new String[lastIndex];
            engine.generatePwntari(id, scheme, preset, contrast, brightness, getSelectedKick());
        } else if (method.equals(ConversionMethod.Hybrid)) {
            ircOutput = new String[lastIndex];
            engine.generateHybrid(id, scheme, preset, contrast, brightness, getSelectedKick());
        } else if (method.equals(ConversionMethod.Plain)) {
            ircOutput = new String[lastIndex];
            engine.generatePlain(id, preset, contrast, brightness, getSelectedKick());
        }
    }

    /**
     * Refreshes the progress display.
     * @param index
     */
    private void updateProgress(final int index) {
        final double percentDone = index * TOTAL_PERCENT / lastIndex;
        display.getStatusLabel().setText("Converting image: " + NumberFormat.getFormat("##0").format(percentDone) + "%");
    }

    /**
     * Adds a line to the irc output TextBox.
     * @param ircLine
     * @param index
     */
    public final synchronized void addIrcOutputLine(final String ircLine,
            final int index) {
        updateProgress(index);
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                if (ircLine != null && index < ircOutput.length) {
                    ircOutput[index] = ircLine;
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
                }
            }
        });

    }

    /**
     * Adds the finished output to the view.
     */
    private synchronized void addOutput() {
        ircOutput = engine.removeEmptyLines(ircOutput);

        final StringBuilder irc = new StringBuilder();
        for (final String s : ircOutput) {
            irc.append(s);
            irc.append("\n");
        }
        
        final Date now = new Date();
        String[] htmlAndCss = null;

        if (method.equals(ConversionMethod.Plain)) { //TODO pass enum instead of boolean
            htmlAndCss = htmlUtil.generateHtml(ircOutput, currentImageName, true);
        } else { // boolean says whether method is plain or not.
            htmlAndCss = htmlUtil.generateHtml(ircOutput, currentImageName, false);
        }

        // save image info
        jenkemImageInfo.setName(currentImageName);
        jenkemImageInfo.setLines(ircOutput.length);
        jenkemImageInfo.setLineWidth(getCurrentLineWidth());
        final DateTimeFormat format = DateTimeFormat.getFormat("yyyy.MM.dd HH:mm:ss"); // TODO use date Util
        jenkemImageInfo.setCreation(format.format(now));

        // save HTML
        jenkemImageHtml.setName(currentImageName);
        jenkemImageHtml.setHtml(htmlAndCss[0]);

        // save CSS
        jenkemImageCss.setName(currentImageName);
        jenkemImageCss.setCss(htmlAndCss[1]);

        // save IRC
        jenkemImageIrc.setName(currentImageName);
        jenkemImageIrc.setIrc(irc.toString());

        // get HTML and CSS for inline element
        final String inlineCss = htmlUtil.prepareCssForInline(htmlAndCss[1]);
        final String inlineHtml = htmlUtil.prepareHtmlForInline(htmlAndCss[0], inlineCss);
        display.getPreviewHtml().setHTML(inlineHtml);

        // prepare output for IRC
        final StringBuilder binaryOutput = new StringBuilder();
        for (final String line : ircOutput) {
            binaryOutput.append(line);
            binaryOutput.append("\n");
        }

        removeBusyIcon();

        display.getIrcTextArea().setText(binaryOutput.toString());
        display.getIrcTextArea().selectAll();

        isConversionRunnung = false;
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
        if (method.equals(ConversionMethod.FullHd)
                || method.equals(ConversionMethod.Plain)) {
            display.getContrastSlider().setValue(INITIAL_CONTRAST_FOR_PLAIN);
            updateContrast(INITIAL_CONTRAST_FOR_PLAIN);
        } else {
            display.getContrastSlider().setValue(INITIAL_CONTRAST);
            updateContrast(INITIAL_CONTRAST);
        }
        display.getBrightnessSlider().setValue(INITIAL_BRIGHNESS);
        updateBrightness(INITIAL_BRIGHNESS);
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
    private void updateContrast(final int value) {
        final double contrast = (Double.valueOf(value) + 1) / CONTRAST_DIVISOR;
        display.getContrastLabel().setText(String.valueOf(contrast));
    }

    /**
     * Updates the brightness with the provided value.
     * @param value
     */
    private void updateBrightness(final int value) {
        final int brightness = value - INITIAL_BRIGHNESS;
        display.getBrightnessLabel().setText(String.valueOf(brightness));
    }
}
