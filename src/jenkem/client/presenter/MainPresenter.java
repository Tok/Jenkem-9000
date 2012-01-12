package jenkem.client.presenter;

import gwt.g2d.client.graphics.ImageLoader;
import gwt.g2d.client.graphics.Surface;
import gwt.g2d.client.graphics.canvas.ImageDataAdapter;

import java.util.ArrayList;
import java.util.Date;

import jenkem.client.service.JenkemServiceAsync;
import jenkem.shared.ColorScheme;
import jenkem.shared.ConversionMethod;
import jenkem.shared.Engine;
import jenkem.shared.HtmlUtil;
import jenkem.shared.data.JenkemImage;
import jenkem.shared.data.JenkemImageInfo;

import com.google.appengine.api.datastore.Text;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.kiouri.sliderbar.client.event.BarValueChangedEvent;
import com.kiouri.sliderbar.client.event.BarValueChangedHandler;
import com.kiouri.sliderbar.client.solution.simplehorizontal.SliderBarSimpleHorizontal;

public class MainPresenter extends AbstractTabPresenter implements Presenter {
	private final Engine engine = new Engine();
	private final HtmlUtil htmlUtil = new HtmlUtil();
	
	private final Display display;
	
	private final Image busyImage = new Image("/images/busy.gif");
	
	private ImageElement currentImage;
	private String currentImageName;
	private final JenkemImageInfo jenkemImageInfo = new JenkemImageInfo();
	private final JenkemImage jenkemImage = new JenkemImage();
	
	public interface Display {
		HasValue<String> getInputLink();
		TextBox getInputTextBox();
		Label getStatusLabel();
		HasClickHandlers getShowButton();
		Panel getBusyPanel();
		Surface getSurface();
		InlineHTML getPreviewHtml();
		TextArea getIrcTextArea();
		ListBox getMethodListBox();
		ListBox getSchemeListBox();
		HasClickHandlers getResetButton();
		SliderBarSimpleHorizontal getContrastSlider();
		Label getContrastLabel();
		SliderBarSimpleHorizontal getBrightnessSlider();
		Label getBrightnessLabel();
		RadioButton getNoKickButton();
		RadioButton getXKickButton();
		RadioButton getYKickButton();
		RadioButton getXyKickButton();
		HasClickHandlers getSubmitButton();
		Widget asWidget();
	}

	public MainPresenter(final JenkemServiceAsync jenkemService,
			final HandlerManager eventBus, final TabPanel tabPanel, final Display view) {
		super(jenkemService, eventBus, tabPanel);
		this.display = view;
	}

	public void bind() {
		this.display.getInputTextBox().addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getCharCode() == KeyCodes.KEY_ENTER) {
					doShow(proxify());
			    }			
			}
		});
		
		this.display.getShowButton().addClickHandler(new ClickHandler() {
			public void onClick(final ClickEvent event) {
				doShow(proxify());
			}
		});
		
		this.display.getMethodListBox().addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				final String methodName = display.getMethodListBox().getItemText(display.getMethodListBox().getSelectedIndex());
				if (methodName.equals(ConversionMethod.FullHd.toString())) {
					disableKicks();
				} else {
					enableKicks();
				}
				doShow(proxify());
			}
		});
		
		this.display.getSchemeListBox().addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				doShow(proxify());
			}
		});
		
		this.display.getResetButton().addClickHandler(new ClickHandler() {
			public void onClick(final ClickEvent event) {
				doReset();
				doConversion();
			}
		});
				
		this.display.getContrastSlider().addBarValueChangedHandler(new BarValueChangedHandler() {
			@Override
			public void onBarValueChanged(BarValueChangedEvent event) {
				updateContrast(event.getValue());
				doConversion();
			}
		});
				
		this.display.getBrightnessSlider().addBarValueChangedHandler(new BarValueChangedHandler() {
			@Override
			public void onBarValueChanged(BarValueChangedEvent event) {
				updateBrightness(event.getValue());
				doConversion();
			}
		});
		
		this.display.getNoKickButton().addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if (event.getValue()) {
					doConversion();
				}
			}
		});

		this.display.getXKickButton().addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if (event.getValue()) {
					doConversion();
				}
			}
		});
		
		this.display.getYKickButton().addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if (event.getValue()) {
					doConversion();
				}
			}
		});
		
		this.display.getXyKickButton().addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if (event.getValue()) {
					doConversion();
				}
			}
		});
		
		this.display.getSubmitButton().addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				getJenkemService().saveJenkemImage(jenkemImageInfo, jenkemImage,
					new AsyncCallback<String>() {
						@Override
						public void onFailure(Throwable caught) {
							display.getStatusLabel().setText("Fail submitting conversion.");
						}
						@Override
						public void onSuccess(final String result) {
							display.getStatusLabel().setText("Conversion submitted successfully.");
						}
					}
				);
			}
		});
	}
	
	/**
	 * calls the local image servlet to proxify the provided image
	 * in order to circumvent the restrictions put by the same origin policy.
	 * @return url to image servlet
	 */
	private String proxify() {
		display.getStatusLabel().setText("Proxifying image...");
		final String urlString = display.getInputTextBox().getText();
		updateImageName(urlString);
		if (!"".equals(urlString)) {
			return "http://" + Window.Location.getHost() + "/jenkem/image?url=" + urlString;
		} else {
			return "";
		}
	}

	private void updateImageName(String urlString) {
		String[] split = urlString.split("/");
		currentImageName = split[split.length -1];		
	}
	
	public void go(final HasWidgets container) {
		bind();
		display.getInputTextBox().setFocus(true);
		container.clear();
		container.add(super.getTabPanel());
		doReset();
	}

	private void doShow(final String url) {
		if (!"".equals(url)) {
			displayBusyIcon();
		}
		
		final String[] urls = new String[] { url };
		ImageLoader.loadImages(urls, new ImageLoader.CallBack() {
			@Override
			public void onImagesLoaded(ImageElement[] imageElements) {
				currentImage = imageElements[0];
				doConversion();
			}
		});
	}
	
	private void doConversion() {
		if (currentImage == null) {
			return;
		}
		
		displayBusyIcon();
		final String methodName = display.getMethodListBox().getItemText(display.getMethodListBox().getSelectedIndex());
		final int WIDTH = 72;
		int height = 0;
		if (methodName.equals(ConversionMethod.FullHd.toString())) {
			height = (36 * currentImage.getHeight()) / currentImage.getWidth();
		} else { //Super-Hybrid, Hybrid, Plain and Pwntari
			height = (72 * currentImage.getHeight()) / currentImage.getWidth();				
		}
		
		display.getSurface().clear();
		display.getSurface().setWidth(WIDTH + 5);
		display.getSurface().setHeight(height);
		display.getSurface().drawImage(currentImage, 0, 0, WIDTH, height);

		final ImageDataAdapter ida = display.getSurface().getImageData(0, 0, WIDTH, height);
		final String schemeName = display.getSchemeListBox().getItemText(display.getSchemeListBox().getSelectedIndex());
		final ColorScheme scheme = ColorScheme.valueOf(schemeName);
		
		double contrast = Double.valueOf(display.getContrastLabel().getText());
		int brightness = Integer.valueOf(display.getBrightnessLabel().getText());
		String kick = getKick();
		String[] ircOutput = null;
		if (methodName.equals(ConversionMethod.FullHd.toString())) {
			ircOutput = engine.generateHighDef(ida, scheme, contrast, brightness);
		} else if (methodName.equals(ConversionMethod.SuperHybrid.toString())) {
			ircOutput = engine.generateSuperHybrid(ida, scheme, contrast, brightness, kick);
		} else if (methodName.equals(ConversionMethod.Pwntari.toString())) {
			ircOutput = engine.generatePwntari(ida, scheme, contrast, brightness, kick);
		} else if (methodName.equals(ConversionMethod.Hybrid.toString())) {
			ircOutput = engine.generateHybrid(ida, scheme, contrast, brightness, kick);
		} else if (methodName.equals(ConversionMethod.Plain.toString())) {
			ircOutput = engine.generatePlain(ida, contrast, brightness, kick);
		}

		final ArrayList<Text> irc = new ArrayList<Text>();
		for (String s : ircOutput) {
			irc.add(new Text(s));
		}

		final Date now = new Date();
		String[] htmlAndCss = null;
		if (methodName.equals(ConversionMethod.Plain.toString())) {
			htmlAndCss = htmlUtil.generateHtml(ircOutput, currentImageName, true);
		} else { //boolean says whether method is plain or not.
			htmlAndCss = htmlUtil.generateHtml(ircOutput, currentImageName, false);
		}
		
		//save image info
		jenkemImageInfo.setName(currentImageName);
		jenkemImageInfo.setCreateDate(now);
		jenkemImageInfo.setCreateStamp(now.getTime());
		
		//save conversion in jenkem image
		jenkemImage.setName(currentImageName);
		jenkemImage.setIrc(irc);
		jenkemImage.setHtml(htmlAndCss[0]);
		jenkemImage.setCss(htmlAndCss[1]);

		//get HTML and CSS for inline element
		final String inlineCss = htmlUtil.prepareCssForInline(htmlAndCss[1]);
		final String inlineHtml = htmlUtil.prepareHtmlForInline(htmlAndCss[0], inlineCss);
		display.getPreviewHtml().setHTML(inlineHtml);
		
		//prepare output for IRC
		StringBuilder binaryOutput = new StringBuilder();
		for (String line : ircOutput) {
			binaryOutput.append(line);
			binaryOutput.append("\n");
		}
		display.getIrcTextArea().setText(binaryOutput.toString());
		display.getIrcTextArea().selectAll();

		removeBusyIcon();
	}
	
	private void displayBusyIcon() {
		display.getBusyPanel().clear();
		display.getBusyPanel().add(busyImage);
		display.getStatusLabel().setText("Converting image...");
	}
	
	private void removeBusyIcon() {
		display.getBusyPanel().clear();
		display.getStatusLabel().setText("Please enter URL to an image:");
	}
	
	private String getKick() {
		if (this.display.getNoKickButton().getValue()) {
			return "0";
		} else if (this.display.getXKickButton().getValue()) {
			return "X";
		} else if (this.display.getYKickButton().getValue()) {
			return "Y";
		} else {
			return "XY";
		}
	}

	private void doReset() {
		display.getSchemeListBox().setSelectedIndex(0);
		display.getContrastSlider().setValue(94);
		updateContrast(94);
		display.getBrightnessSlider().setValue(100);
		updateBrightness(100);
		display.getNoKickButton().setValue(true);
	}
	
	private void disableKicks() {
		display.getNoKickButton().setEnabled(false);
		display.getXKickButton().setEnabled(false);
		display.getYKickButton().setEnabled(false);
		display.getXyKickButton().setEnabled(false);
	}

	private void enableKicks() {
		display.getNoKickButton().setEnabled(true);
		display.getXKickButton().setEnabled(true);
		display.getYKickButton().setEnabled(true);
		display.getXyKickButton().setEnabled(true);
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
