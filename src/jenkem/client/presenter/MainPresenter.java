package jenkem.client.presenter;

import gwt.g2d.client.graphics.ImageLoader;
import gwt.g2d.client.graphics.Surface;
import gwt.g2d.client.graphics.canvas.ImageDataAdapter;

import java.util.ArrayList;
import java.util.Date;

import jenkem.client.service.JenkemServiceAsync;
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
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class MainPresenter implements Presenter {
	private final Engine engine = new Engine();
	
	public interface Display {
		HasValue<String> getInputLink();
		TextBox getInputTextBox();
		HasClickHandlers getShowButton();
		
//		Canvas getCanvas();
		Surface getSurface();
		Frame getPreviewFrame();
		
		ListBox getMethodListBox();
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
		this.display.getShowButton().addClickHandler(new ClickHandler() {
			public void onClick(final ClickEvent event) {
				doShow(proxify());
			}
		});
		
		this.display.getMethodListBox().addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				doShow(proxify());
			}
		});
	}
	
	/**
	 * calls the local image servlet to proxify the provided image
	 * in order to circumvent the restrictions put by the same origin policy.
	 * @return url to image servlet
	 */
	private String proxify() {
		return "http://" + Window.Location.getHost()
				+ "/jenkem/image?url="
				+ display.getInputTextBox().getText();

	}

	public void go(final HasWidgets container) {
		bind();
		container.clear();
		container.add(display.asWidget());
		display.getInputTextBox().setFocus(true);
	}

	private void doShow(String url) {
		String[] urls = new String[] { url };
		ImageLoader.loadImages(urls, new ImageLoader.CallBack() {
			@Override
			public void onImagesLoaded(ImageElement[] imageElements) {
				final ImageElement image = imageElements[0];

				final String methodName = display.getMethodListBox().getItemText(display.getMethodListBox().getSelectedIndex());
				final int WIDTH = 72;
				int height = 0;
				if (methodName.equals(ConversionMethod.FullHd.toString())) {
					height = (36 * image.getHeight()) / image.getWidth();
				} else if (methodName.equals(ConversionMethod.Hybrid.toString())) {
					height = (84 * image.getHeight()) / image.getWidth();	
				} else { //Super-Hybrid and Pwntari
					height = (72 * image.getHeight()) / image.getWidth();				
				}
				
				display.getSurface().clear();
				display.getSurface().setWidth(WIDTH + 5);
				display.getSurface().setHeight(height);
				display.getSurface().drawImage(image, 0, 0, WIDTH, height);

				final ImageDataAdapter ida = display.getSurface().getImageData(0, 0, WIDTH, height);
				
				String[] ircOutput = null;
				if (methodName.equals(ConversionMethod.FullHd.toString())) {
					ircOutput = engine.generateHighDef(ida);
				} else if (methodName.equals(ConversionMethod.SuperHybrid.toString())) {
					ircOutput = engine.generateSuperHybrid(ida);
				} else if (methodName.equals(ConversionMethod.Hybrid.toString())) {
					ircOutput = engine.generateHybrid(ida);
				} else if (methodName.equals(ConversionMethod.Pwntari.toString())) {
					ircOutput = engine.generatePwntari(ida);
				}

				ArrayList<Text> irc = new ArrayList<Text>();
				for (String s : ircOutput) {
					irc.add(new Text(s));
				}

				HtmlUtil htmlUtil = new HtmlUtil();
				Date now = new Date();
				String name = Long.valueOf(now.getTime()).toString();
				String[] htmlAndCss = htmlUtil.generateHtml(ircOutput, name,
						HtmlUtil.MODE_FULL_HD);

				JenkemImage jenkemImage = new JenkemImage();
				jenkemImage.setName(name);
				jenkemImage.setCreateDate(now);
				jenkemImage.setIrc(irc);
				jenkemImage.setHtml(htmlAndCss[0]);
				jenkemImage.setCss(htmlAndCss[1]);

				jenkemService.saveJenkemImage(jenkemImage,
						new AsyncCallback<String>() {
							@Override
							public void onFailure(Throwable caught) {
								// TODO Auto-generated method stub
							}

							@Override
							public void onSuccess(String result) {
								display.getPreviewFrame().setUrl(
										"http://" + Window.Location.getHost()
												+ "/jenkem/output?name="
												+ result + "&type=html");
							}
						});
			}
		});
	}


	
}
