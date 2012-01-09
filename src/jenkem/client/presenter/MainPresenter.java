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
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.kiouri.sliderbar.client.event.BarValueChangedEvent;
import com.kiouri.sliderbar.client.event.BarValueChangedHandler;
import com.kiouri.sliderbar.client.solution.simplehorizontal.SliderBarSimpleHorizontal;

public class MainPresenter implements Presenter {
	private final Engine engine = new Engine();
	private final HtmlUtil htmlUtil = new HtmlUtil();
	private final Image busyImage = new Image("/images/busy.gif");
	private ImageElement currentImage;
	
	public interface Display {
		HasValue<String> getInputLink();
		TextBox getInputTextBox();
		HasClickHandlers getShowButton();
		Panel getBusyPanel();
		Surface getSurface();
		Frame getPreviewFrame();
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
		Widget asWidget();
	}

	private final JenkemServiceAsync jenkemService;
	@SuppressWarnings("unused")
	private final HandlerManager eventBus;
	private final Display display;

	public MainPresenter(final JenkemServiceAsync jenkemService,
			final HandlerManager eventBus, final Display view) {
		this.jenkemService = jenkemService;
		this.eventBus = eventBus;
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
	}
	
	/**
	 * calls the local image servlet to proxify the provided image
	 * in order to circumvent the restrictions put by the same origin policy.
	 * @return url to image servlet
	 */
	private String proxify() {
		final String urlString = display.getInputTextBox().getText();
		if (!"".equals(urlString)) {
			return "http://" + Window.Location.getHost() + "/jenkem/image?url=" + urlString;
		} else {
			return "";
		}
	}

	public void go(final HasWidgets container) {
		bind();
		container.clear();
		container.add(display.asWidget());
		doReset();
		display.getInputTextBox().setFocus(true);
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
		} else if (methodName.equals(ConversionMethod.Hybrid.toString()) ||
				methodName.equals(ConversionMethod.Plain.toString())) {
			height = (84 * currentImage.getHeight()) / currentImage.getWidth();	
		} else { //Super-Hybrid and Pwntari
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
		final String name = Long.valueOf(now.getTime()).toString();
		String[] htmlAndCss = null;
		if (methodName.equals(ConversionMethod.Plain.toString())) {
			htmlAndCss = htmlUtil.generateHtml(ircOutput, name, true);
		} else { //boolean says whether method is plain or not.
			htmlAndCss = htmlUtil.generateHtml(ircOutput, name, false);
		}
		
		final JenkemImage jenkemImage = new JenkemImage();
		jenkemImage.setName(name);
		jenkemImage.setCreateDate(now);
		jenkemImage.setIrc(irc);
		jenkemImage.setHtml(htmlAndCss[0]);
		jenkemImage.setCss(htmlAndCss[1]);

		jenkemService.saveJenkemImage(jenkemImage,
			new AsyncCallback<String>() {
				@Override
				public void onFailure(Throwable caught) {
					//TODO display fail message
					removeBusyIcon();
				}

				@Override
				public void onSuccess(final String result) {
					display.getPreviewFrame().setUrl(
						"http://" + Window.Location.getHost() + "/jenkem/output?name=" + result + "&type=html"
					);
					removeBusyIcon();
				}
				
		});
	
		display.getBusyPanel().clear();
	}
	
	private void displayBusyIcon() {
		display.getBusyPanel().clear();
		display.getBusyPanel().add(busyImage);
	}
	
	private void removeBusyIcon() {
		display.getBusyPanel().clear();
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
