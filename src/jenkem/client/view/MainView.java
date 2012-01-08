package jenkem.client.view;

import gwt.g2d.client.graphics.Surface;
import jenkem.client.presenter.MainPresenter;
import jenkem.client.widget.ExtendedTextBox;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class MainView extends Composite implements MainPresenter.Display {
	private final ExtendedTextBox inputTextBox;
	private final Button showButton;
	
	private Surface surface = new Surface();
//	private Canvas canvas = Canvas.createIfSupported();
	
	private final Frame previewFrame = new Frame();

	private final FlexTable contentTable;
	
	public MainView() {
		String link = com.google.gwt.user.client.Window.Location.getParameter("link");
		
		final DecoratorPanel contentTableDecorator = new DecoratorPanel();
		contentTableDecorator.setWidth("100%");
		contentTableDecorator.setWidth("1024px");
		initWidget(contentTableDecorator);

		contentTable = new FlexTable();
		contentTable.setWidth("100%");
		
		final HorizontalPanel hPanel = new HorizontalPanel();
		
		inputTextBox = new ExtendedTextBox();
		inputTextBox.setWidth("710px");
		inputTextBox.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				doChange();
			}
		});
		inputTextBox.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				doChange();
			}
		});
		if (link != null && !link.equals("")) {
			inputTextBox.setText(link);
			doChange();
		}
		hPanel.add(inputTextBox);		
		showButton = new Button("Convert Image");
		hPanel.add(showButton);

		int row = 0;
		contentTable.setText(row++, 0, "Enter URL to an image:");		
		contentTable.setWidget(row++, 0, hPanel);
		
		final HorizontalPanel content = new HorizontalPanel();
		content.add(surface);

		previewFrame.setWidth("600px");
		previewFrame.setHeight("750px");

		content.add(previewFrame);
		contentTable.setWidget(row++, 0, content);
		contentTableDecorator.add(contentTable);
	}

	private void doChange() {
		
	}
	
	@Override
	public Widget asWidget() {
		return this;
	}

	@Override
	public HasClickHandlers getShowButton() {
		return showButton;
	}

	@Override
	public HasValue<String> getInputLink() {
		return inputTextBox;
	}

	@Override
	public TextBox getInputTextBox() {
		return inputTextBox;
	}

	@Override
	public Surface getSurface() {
		return surface;
	}

//	@Override
//	public Canvas getCanvas() {
//		return canvas;
//	}
	
	@Override
	public Frame getPreviewFrame() {
		return previewFrame;
	}

}
