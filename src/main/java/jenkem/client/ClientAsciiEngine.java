package jenkem.client;

import java.util.Map;
import jenkem.client.presenter.MainPresenter;
import jenkem.shared.ConversionMethod;
import jenkem.shared.Engine;
import jenkem.shared.Power;
import jenkem.shared.ProcessionSettings;
import jenkem.shared.color.IrcColor;

/**
 * Converts images to colored ASCII in client.
 */
public class ClientAsciiEngine {
    private final Engine engine = new Engine();
    private final MainPresenter presenter;

    /**
     * @param presenter with a method to add converted lines.
     */
    public ClientAsciiEngine(final MainPresenter presenter) {
        this.presenter = presenter;
    }

    /**
     * Initializes colorMap and starts conversion for the provided mode.
     * The conversion is triggered by starting the conversion of the 1st line.
     */
    public final void generate(final ConversionMethod method) {
        //Window.alert("generate"); //TODO remove 2x
        generateLine(method, 0);
    }

    public final void generateLine(final ConversionMethod method, final int index) {
        final String nextLine = engine.generateLine(method, index);
        presenter.addIrcOutputLine(nextLine, index);
    }

    public final void setParams(final Map<String, Integer[]> imageRgb, final int width,
            final String charset, final int contrast, final int brightness,
            final ProcessionSettings settings) {
        engine.setParams(imageRgb, width, charset, contrast, brightness, settings);
    }

    public final void prepareEngine(final Map<IrcColor, Integer> colorMap, final Power power) {
        engine.prepareEngine(colorMap, power);
    }
}
