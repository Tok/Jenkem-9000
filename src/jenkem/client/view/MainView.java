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
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.kiouri.sliderbar.client.solution.simplehorizontal.SliderBarSimpleHorizontal;

public class MainView extends Composite implements MainPresenter.Display {
	private final ExtendedTextBox inputTextBox;
	private final Button showButton;
	private final Panel busyPanel = new HorizontalPanel();
	private final Surface surface = new Surface();
	private final Frame previewFrame = new Frame();
	private final ListBox methodListBox = new ListBox();
	private final Button resetButton = new Button("Reset");
	private final SliderBarSimpleHorizontal contrastSlider = new SliderBarSimpleHorizontal(100, "100px", false);
	private final Label contrastLabel = new Label();
	private final SliderBarSimpleHorizontal brightnessSlider = new SliderBarSimpleHorizontal(100, "100px", false);
	private final Label brightnessLabel = new Label();
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

		int settingsRow = 0;
		final FlexTable settingsTable = new FlexTable();
		for (ConversionMethod method : ConversionMethod.values()) {
			methodListBox.addItem(method.toString());
		}
		settingsTable.setText(settingsRow, 0, "Conversion Method");
		settingsTable.setWidget(settingsRow, 1, methodListBox);
		settingsTable.getFlexCellFormatter().setColSpan(settingsRow, 1, 2);
		settingsRow++;
		
		settingsTable.setWidget(settingsRow, 0, new HTML("&nbsp;"));
		settingsTable.getFlexCellFormatter().setColSpan(settingsRow, 1, 3);
		settingsRow++;
		
		settingsTable.setText(settingsRow, 0, "Reset values");
		settingsTable.setWidget(settingsRow, 1, resetButton);
		settingsTable.getFlexCellFormatter().setColSpan(settingsRow, 1, 2);
		settingsRow++;

		settingsTable.setText(settingsRow, 0, "Contrast");
		contrastSlider.setMaxValue(199);
		settingsTable.setWidget(settingsRow, 1, contrastSlider);
		settingsTable.setWidget(settingsRow, 2, contrastLabel);
		settingsRow++;
		
		settingsTable.setText(settingsRow, 0, "Brightness");
		brightnessSlider.setMaxValue(200);
		settingsTable.setWidget(settingsRow, 1, brightnessSlider);
		settingsTable.setWidget(settingsRow, 2, brightnessLabel);
		settingsRow++;
		
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

	@Override
	public ListBox getMethodListBox() {
		return methodListBox;
	}
	
	@Override
	public HasClickHandlers getResetButton() {
		return resetButton;
	}
	
	@Override
	public SliderBarSimpleHorizontal getContrastSlider() {
		return contrastSlider;
	}
	
	@Override
	public Label getContrastLabel() {
		return contrastLabel;
	}
	
	@Override
	public SliderBarSimpleHorizontal getBrightnessSlider() {
		return brightnessSlider;
	}
	
	@Override
	public Label getBrightnessLabel() {
		return brightnessLabel;
	}
	
	@Override
	public Frame getPreviewFrame() {
		return previewFrame;
	}

}
