package jenkem.client.view;

import gwt.g2d.client.graphics.Surface;
import jenkem.client.presenter.MainPresenter;
import jenkem.client.widget.ExtendedTextBox;
import jenkem.shared.ConversionMethod;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class MainView extends Composite implements MainPresenter.Display {
	private final ExtendedTextBox inputTextBox;
	private final Button showButton;
	private final Panel busyPanel = new HorizontalPanel();
	private final Surface surface = new Surface();
//	private final Canvas canvas = Canvas.createIfSupported();
	private final Frame previewFrame = new Frame();
	private final ListBox methodListBox = new ListBox();
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
		
		hPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hPanel.setSpacing(5);
		if (link != null && !link.equals("")) {
			inputTextBox.setText(link);
		}
		hPanel.add(inputTextBox);		
		showButton = new Button("Convert Image");
		hPanel.add(showButton);
		hPanel.add(busyPanel);

		int row = 0;
		contentTable.setText(row++, 0, "Enter URL to an image:");		
		contentTable.setWidget(row++, 0, hPanel);
		
		final HorizontalPanel content = new HorizontalPanel();
		content.add(surface);

		previewFrame.setWidth("620px");
		previewFrame.setHeight("750px");
		content.add(previewFrame);

		final FlexTable settingsTable = new FlexTable();

		for (ConversionMethod method : ConversionMethod.values()) {
			methodListBox.addItem(method.toString());
		}
		settingsTable.setText(0, 0, "Conversion Method");
		settingsTable.setWidget(0, 1, methodListBox);
		
		content.add(settingsTable);
		
		contentTable.setWidget(row++, 0, content);
		contentTableDecorator.add(contentTable);
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
	public Panel getBusyPanel() {
		return busyPanel;
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
	public ListBox getMethodListBox() {
		return methodListBox;
	}
	
	@Override
	public Frame getPreviewFrame() {
		return previewFrame;
	}

}
