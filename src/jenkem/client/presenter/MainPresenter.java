package jenkem.client.presenter;

import java.util.ArrayList;
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

import com.google.appengine.api.datastore.Text;
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

public class MainPresenter extends AbstractTabPresenter implements Presenter {
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

	private final JenkemImageInfo jenkemImageInfo = new JenkemImageInfo();
	private final JenkemImageHtml jenkemImageHtml = new JenkemImageHtml();
	private final JenkemImageCss jenkemImageCss = new JenkemImageCss();
	private final JenkemImageIrc jenkemImageIrc = new JenkemImageIrc();
	
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

	public MainPresenter(final JenkemServiceAsync jenkemService,
			final HandlerManager eventBus, final TabPanel tabPanel,
			final Display view) {
		super(eventBus, tabPanel);
		this.jenkemService = jenkemService;
		this.display = view;
		
	}

	public void bind() {
		this.display.getInputTextBox().addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getCharCode() == KeyCodes.KEY_ENTER) {
					replaceUrl();
			    }			
			}
		});
		
		this.display.getShowButton().addClickHandler(new ClickHandler() {
			public void onClick(final ClickEvent event) {
				replaceUrl();
			}
		});
		
		this.display.getMethodListBox().addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
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
			public void onChange(ChangeEvent event) {
				replaceUrl();				
			}
		});
		
		this.display.getSchemeListBox().addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				doConversion();
			}
		});
		
		this.display.getPresetListBox().addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				doConversion();
			}
		});
		
		this.display.getResetButton().addClickHandler(new ClickHandler() {
			public void onClick(final ClickEvent event) {
				doReset();
				doConversion();
			}
		});
		
		this.display.getContrastSlider().addMouseOverHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				readyForSlider = true;
			}
		});
				
		this.display.getContrastSlider().addBarValueChangedHandler(new BarValueChangedHandler() {
			@Override
			public void onBarValueChanged(BarValueChangedEvent event) {
				if (readyForSlider) {
					updateContrast(event.getValue());
					doConversion();
					readyForSlider = false;
				}
			}
		});

		this.display.getBrightnessSlider().addMouseOverHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				readyForSlider = true;
			}
		});
		
		this.display.getBrightnessSlider().addBarValueChangedHandler(new BarValueChangedHandler() {
			@Override
			public void onBarValueChanged(BarValueChangedEvent event) {
				if (readyForSlider) {
					updateBrightness(event.getValue());
					doConversion();
					readyForSlider = false;
				}
			}
		});
		
		for (Kick kick : Kick.values()) {
			this.display.getKickButton(kick).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
				@Override
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					if (event.getValue()) {
						doConversion();
					}
				}
			});
		}
		
		this.display.getSubmitButton().addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				synchronized(this) {
					jenkemService.saveJenkemImage(jenkemImageInfo, jenkemImageHtml, jenkemImageCss, jenkemImageIrc,
						new AsyncCallback<String>() {
							@Override
							public void onFailure(Throwable caught) {
								//display.getStatusLabel().setText("Fail submitting conversion.");
							}
							@Override
							public void onSuccess(final String result) {
								display.getStatusLabel().setText("Conversion submitted successfully.");
							}
						}
					);
				}
			}
		});
	}
	
	private void replaceUrl() {
		final String currentToken = History.getToken();
		final String currentUrl = display.getInputTextBox().getValue();
		if (!currentToken.endsWith(currentUrl)) {
			History.newItem("main/" + display.getInputTextBox().getValue());			
		} else {
			proxifyAndConvert();
		}
	}
	
	public void proxifyAndConvert() {
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
	 * calls the local image servlet to proxify the provided image in order to
	 * circumvent the restrictions put by the same origin policy.
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

	private void updateImageName(String urlString) {
		String[] split = urlString.split("/");
		currentImageName = split[split.length - 1];
	}

	public void go(final HasWidgets container) {
		bind();
		container.clear();
		container.add(super.getTabPanel());
		display.getInputTextBox().setFocus(true);
		doReset();
	}

	private void doShow(final String url) {
		if (!"".equals(url)) {
			displayBusyIcon();
		}

		image = new Image();
		image.addErrorHandler(new ErrorHandler() {
			@Override
			public void onError(ErrorEvent event) {
				display.getStatusLabel().setText("Proxifying this image failed.");
				removeBusyIcon();
			}
		});
		image.addLoadHandler(new LoadHandler() {
			@Override
			public void onLoad(LoadEvent event) {
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

	private ConversionMethod getCurrentConversionMethod() {
		final String methodName = display.getMethodListBox().getItemText(display.getMethodListBox().getSelectedIndex());
		return ConversionMethod.getValueByName(methodName);
	}
	
	private int getCurrentLineWidth() {
		final String widthString = display.getWidthListBox().getItemText(display.getWidthListBox().getSelectedIndex());
		int result = Integer.parseInt(widthString);
		if (currentImage.getWidth() < result) {
			result = currentImage.getWidth();
		}
		return result;
	}
	
	private void doDeferredConversion() {
		engine = new Engine(this);
		
		currentImage = ImageElement.as(image.getElement());

		method = getCurrentConversionMethod();
		final int WIDTH = getCurrentLineWidth(); 
		int height = 0;
		if (method.equals(ConversionMethod.FullHd)) {
			height = ((WIDTH / 2) * currentImage.getHeight()) / currentImage.getWidth();
		} else { // Super-Hybrid, Hybrid, Plain and Pwntari
			height = (WIDTH * currentImage.getHeight()) / currentImage.getWidth();
		}

		display.getCanvas().setWidth(String.valueOf(WIDTH) + "px");
		display.getCanvas().setHeight(String.valueOf(height) + "px");
		display.getCanvas().getContext2d().fillRect(0, 0, WIDTH, height); //resets the canvas with black bg
		display.getCanvas().getContext2d().drawImage(currentImage, 0, 0, WIDTH, height);

		final ImageData id = display.getCanvas().getContext2d()
				.getImageData(0, 0, WIDTH, height);

		final String schemeName = display.getSchemeListBox().getItemText(
				display.getSchemeListBox().getSelectedIndex());
		final ColorScheme scheme = ColorScheme.valueOf(schemeName);

		final String presetName = display.getPresetListBox().getItemText(
				display.getPresetListBox().getSelectedIndex());
		final CharacterSet preset = CharacterSet.valueOf(presetName);
		
		
		final double contrast = Double.valueOf(display.getContrastLabel().getText());
		final int brightness = Integer
				.valueOf(display.getBrightnessLabel().getText());
		
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
			engine.generateHybrid(id, scheme, preset, contrast, brightness,	getSelectedKick());
		} else if (method.equals(ConversionMethod.Plain)) {
			ircOutput = new String[lastIndex];
			engine.generatePlain(id, preset, contrast, brightness, getSelectedKick());
		}
	}
	
	private void updateProgress(final int index) {
		final double percentDone = index * 100 / lastIndex;
		display.getStatusLabel().setText("Converting image: " + NumberFormat.getFormat("##0").format(percentDone) + "%");
		if (index == lastIndex) {
			isConversionRunnung = false;
		}
	}
	
	public synchronized void addIrcOutputLine(final String ircLine, final int index) {
		updateProgress(index);
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				if (ircLine != null && index < ircOutput.length) {
					ircOutput[index] = ircLine;
				}
				if (index >= lastIndex -1) {
					addIrcOutput();
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
	
	private synchronized void addIrcOutput() {
		ircOutput = engine.removeEmptyLines(ircOutput);
		
		final ArrayList<Text> irc = new ArrayList<Text>();
		for (String s : ircOutput) {
			irc.add(new Text(s));
		}

		final Date now = new Date();
		String[] htmlAndCss = null;
		if (method.equals(ConversionMethod.Plain)) {
			htmlAndCss = htmlUtil.generateHtml(ircOutput, currentImageName,
					true);
		} else { // boolean says whether method is plain or not.
			htmlAndCss = htmlUtil.generateHtml(ircOutput, currentImageName,
					false);
		}

		// save image info
		jenkemImageInfo.setName(currentImageName);
		jenkemImageInfo.setLines(irc.size());
		jenkemImageInfo.setLineWidth(getCurrentLineWidth());
		jenkemImageInfo.setCreateDate(now);

		// save HTML
		jenkemImageHtml.setName(currentImageName);
		jenkemImageHtml.setHtml(htmlAndCss[0]);

		// save CSS
		jenkemImageCss.setName(currentImageName);
		jenkemImageCss.setCss(htmlAndCss[1]);

		// save IRC
		jenkemImageIrc.setName(currentImageName);
		jenkemImageIrc.setIrc(irc);

		// get HTML and CSS for inline element
		final String inlineCss = htmlUtil.prepareCssForInline(htmlAndCss[1]);
		final String inlineHtml = htmlUtil.prepareHtmlForInline(htmlAndCss[0],
				inlineCss);
		display.getPreviewHtml().setHTML(inlineHtml);

		// prepare output for IRC
		StringBuilder binaryOutput = new StringBuilder();
		for (String line : ircOutput) {
			binaryOutput.append(line);
			binaryOutput.append("\n");
		}

		removeBusyIcon();
	
		display.getIrcTextArea().setText(binaryOutput.toString());
		display.getIrcTextArea().selectAll();
	}
	
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

	private Kick getSelectedKick() {
		for (Kick kick : Kick.values()) {
			if (this.display.getKickButton(kick).getValue()) {
				return kick;
			}
		}
		return Kick.Off;
	}

	private void doReset() {
		display.getSchemeListBox().setSelectedIndex(0);
		display.getPresetListBox().setSelectedIndex(0);
		resetContrastAndBrightness();
		display.getKickButton(Kick.Off).setValue(true);
	}
	
	private void resetContrastAndBrightness() {
		method = getCurrentConversionMethod();
		if (method.equals(ConversionMethod.FullHd) 
				|| method.equals(ConversionMethod.Plain)) {
			display.getContrastSlider().setValue(89);
			updateContrast(89);
		} else {
			display.getContrastSlider().setValue(59);
			updateContrast(59);
		}		
		display.getBrightnessSlider().setValue(100);
		updateBrightness(100);
	}

	private void disableKicks() {
		for (Kick kick : Kick.values()) {
			display.getKickButton(kick).setEnabled(false);			
		}
	}

	private void enableKicks() {
		if (!method.equals(ConversionMethod.FullHd)) {
			for (Kick kick : Kick.values()) {
				display.getKickButton(kick).setEnabled(true);			
			}
		}
	}

	private void updateContrast(final int value) {
		double contrast = (Double.valueOf(value) + 1) / 100.0;
		display.getContrastLabel().setText(String.valueOf(contrast));
	}

	private void updateBrightness(final int value) {
		int brightness = value - 100;
		display.getBrightnessLabel().setText(String.valueOf(brightness));
	}
}
