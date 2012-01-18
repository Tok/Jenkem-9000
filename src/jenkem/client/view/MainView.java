package jenkem.client.view;

import java.util.HashMap;
import java.util.Map;

import jenkem.client.presenter.MainPresenter;
import jenkem.client.widget.ExtendedTextBox;
import jenkem.shared.CharacterSet;
import jenkem.shared.ColorScheme;
import jenkem.shared.ConversionMethod;
import jenkem.shared.Kick;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.i18n.client.HasDirection.Direction;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.kiouri.sliderbar.client.solution.simplehorizontal.SliderBarSimpleHorizontal;

public class MainView extends Composite implements MainPresenter.Display {
	private final ExtendedTextBox inputTextBox = new ExtendedTextBox();
	private final Button showButton = new Button("Convert Image");
	private final Panel busyPanel = new HorizontalPanel();
	private final Label statusLabel = new Label("Enter URL to an image:");
	private final Canvas canvas = Canvas.createIfSupported(); 
	private final Panel previewPanel = new VerticalPanel();
	private final InlineHTML inline = new InlineHTML();
	private final TextArea ircText = new TextArea();
	private final ListBox methodListBox = new ListBox();
	private final ListBox widthListBox = new ListBox();
	private final ListBox schemeListBox = new ListBox();
	private final ListBox presetListBox = new ListBox();
	private final Button resetButton = new Button("Reset");
	private final SliderBarSimpleHorizontal contrastSlider = new SliderBarSimpleHorizontal(100, "100px", false);
	private final Label contrastLabel = new Label();
	private final SliderBarSimpleHorizontal brightnessSlider = new SliderBarSimpleHorizontal(100, "100px", false);
	private final Label brightnessLabel = new Label();
	private final Map<Kick, RadioButton> kickButtons = new HashMap<Kick, RadioButton>();
	private final Button submitButton = new Button("Submit");
	private final FlexTable contentTable;
	
	public MainView() {
		String link = com.google.gwt.user.client.Window.Location.getParameter("link");
		
		final DecoratorPanel contentTableDecorator = new DecoratorPanel();
		contentTableDecorator.setWidth("1010px");
		initWidget(contentTableDecorator);

		contentTable = new FlexTable();
				
		final HorizontalPanel hPanel = new HorizontalPanel();
		inputTextBox.setWidth("800px");
		hPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hPanel.setSpacing(5);
		if (link != null && !link.equals("")) {
			inputTextBox.setText(link);
		}
		hPanel.add(inputTextBox);
		showButton.setWidth("137px");
		hPanel.add(showButton);
		hPanel.add(busyPanel);

		int row = 0;
		contentTable.setWidget(row++, 0, statusLabel);		
		contentTable.setWidget(row++, 0, hPanel);

		previewPanel.setHeight("1010px");
		previewPanel.add(inline);

		final FlexTable flex = new FlexTable();
		flex.setWidget(0, 0, previewPanel);
		flex.getFlexCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
		flex.getFlexCellFormatter().setWidth(0, 0, "600px");

		int settingsRow = 0;
		final FlexTable settingsTable = new FlexTable();
		for (ConversionMethod method : ConversionMethod.values()) {
			methodListBox.addItem(method.toString());
		}
		settingsTable.setText(settingsRow, 0, "Conversion Method:");
		methodListBox.setWidth("200px");
		settingsTable.setWidget(settingsRow, 1, methodListBox);
		settingsTable.getFlexCellFormatter().setColSpan(settingsRow, 1, 2);
		settingsRow++;
		
		widthListBox.addItem("72");
		widthListBox.addItem("64");
		widthListBox.addItem("56");
		widthListBox.addItem("48");
		widthListBox.addItem("40");
		widthListBox.addItem("32");
		settingsTable.setText(settingsRow, 0, "Line Width:");
		widthListBox.setWidth("200px");
		settingsTable.setWidget(settingsRow, 1, widthListBox);
		settingsTable.getFlexCellFormatter().setColSpan(settingsRow, 1, 2);
		settingsRow++;
				
		settingsTable.setWidget(settingsRow, 0, new HTML("&nbsp;"));
		settingsTable.getFlexCellFormatter().setColSpan(settingsRow, 1, 3);
		settingsRow++;
		
		settingsTable.setText(settingsRow, 0, "Reset values:");
		resetButton.setWidth("200px");
		settingsTable.setWidget(settingsRow, 1, resetButton);
		settingsTable.getFlexCellFormatter().setColSpan(settingsRow, 1, 2);
		settingsRow++;

		for (ColorScheme scheme : ColorScheme.values()) {
			schemeListBox.addItem(scheme.toString());
		}
		settingsTable.setText(settingsRow, 0, "Color Scheme:");
		schemeListBox.setWidth("200px");
		settingsTable.setWidget(settingsRow, 1, schemeListBox);
		settingsTable.getFlexCellFormatter().setColSpan(settingsRow, 1, 2);
		settingsRow++;
		
		for (CharacterSet preset : CharacterSet.values()) {
			presetListBox.addItem(preset.name());
		}
		settingsTable.setText(settingsRow, 0, "Character Set:");
		presetListBox.setWidth("200px");
		settingsTable.setWidget(settingsRow, 1, presetListBox);
		settingsTable.getFlexCellFormatter().setColSpan(settingsRow, 1, 2);
		settingsRow++;
		
		settingsTable.setText(settingsRow, 0, "Contrast:");
		contrastSlider.setMaxValue(199);
		contrastSlider.setWidth("200px");
		settingsTable.setWidget(settingsRow, 1, contrastSlider);
		contrastLabel.setWidth("25px");
		settingsTable.setWidget(settingsRow, 2, contrastLabel);
		settingsRow++;
		
		settingsTable.setText(settingsRow, 0, "Brightness:");
		brightnessSlider.setMaxValue(200);
		brightnessSlider.setWidth("200px");
		settingsTable.setWidget(settingsRow, 1, brightnessSlider);
		brightnessLabel.setWidth("25px");
		settingsTable.setWidget(settingsRow, 2, brightnessLabel);
		settingsRow++;
		
		settingsTable.setText(settingsRow, 0, "Kick:");
		final HorizontalPanel kickPanel = new HorizontalPanel();
		initKicks(kickPanel);
		settingsTable.setWidget(settingsRow, 1, kickPanel);
		settingsTable.getFlexCellFormatter().setColSpan(settingsRow, 1, 2);
		settingsRow++;
		
		settingsTable.setWidget(settingsRow, 0, new HTML("&nbsp;"));
		settingsTable.getFlexCellFormatter().setColSpan(settingsRow, 1, 3);
		settingsRow++;
		
		settingsTable.setText(settingsRow, 0, "Submit Conversion:");
		submitButton.setWidth("200px");
		settingsTable.setWidget(settingsRow, 1, submitButton);
		settingsRow++;
		
		settingsTable.setText(settingsRow, 0, "Binary Output for IRC:");
		settingsRow++;

		ircText.setCharacterWidth(77);
		ircText.setVisibleLines(5);
		ircText.setReadOnly(true);
		ircText.setDirection(Direction.LTR);
		ircText.getElement().setAttribute("wrap", "off");
		ircText.setWidth("393px");
		settingsTable.setWidget(settingsRow, 0, ircText);
		settingsTable.getFlexCellFormatter().setColSpan(settingsRow, 0, 3);
		
		settingsTable.getFlexCellFormatter().setWidth(0, 0, "170px");
		settingsTable.getFlexCellFormatter().setWidth(1, 0, "400px");
		settingsTable.getFlexCellFormatter().setWidth(2, 0, "50px");
		
		flex.setWidget(0, 1, settingsTable);
		flex.getFlexCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_TOP);
		flex.getFlexCellFormatter().setWidth(0, 1, "450px");

		contentTable.setWidget(row++, 0, flex);

		contentTableDecorator.add(contentTable);
	}

	private void initKicks(HorizontalPanel kickPanel) {
		for (Kick kick : Kick.values()) {
			final RadioButton kickRadioButton = new RadioButton("kick", kick.name());
			kickButtons.put(kick, kickRadioButton);
			kickPanel.add(kickRadioButton);			
		}
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
	public Label getStatusLabel() {
		return statusLabel;
	}

	@Override
	public Canvas getCanvas() {
		return canvas;
	}

	@Override
	public ListBox getMethodListBox() {
		return methodListBox;
	}
	
	@Override
	public ListBox getWidthListBox() {
		return widthListBox;
	}
	
	@Override
	public ListBox getSchemeListBox() {
		return schemeListBox;
	}
	
	@Override
	public ListBox getPresetListBox() {
		return presetListBox;
	}
	
	@Override
	public Button getResetButton() {
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
	public RadioButton getKickButton(Kick kick) {
		return kickButtons.get(kick);
	}
	
	@Override
	public InlineHTML getPreviewHtml() {
		return inline;
	}
	
	@Override
	public TextArea getIrcTextArea() {
		return ircText;
	}
	
	@Override
	public Button getSubmitButton() {
		return submitButton;
	}

	public void setUrl(String imageUrl) {
		inputTextBox.setText(imageUrl);
		inputTextBox.selectAll();
	}

	public Object getUrl() {
		return inputTextBox.getText();
	}
}
