package jenkem.client;

import java.util.Map;
import jenkem.client.presenter.MainPresenter;
import jenkem.shared.ConversionMethod;
import jenkem.shared.Engine;
import jenkem.shared.Kick;
import jenkem.shared.Power;
import jenkem.shared.ProcessionSettings;
import jenkem.shared.color.IrcColor;
import com.google.gwt.canvas.dom.client.ImageData;

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
        int startLine = method.hasKick() ? engine.getStartY() : 0;
        generateLine(method, startLine);
    }

    public final void generateLine(final ConversionMethod method, final int index) {
        String nextLine = null;
        if (method.equals(ConversionMethod.FullHd)) {
            nextLine = engine.generateHighDefLine(index);
        } else if (method.equals(ConversionMethod.SuperHybrid)) {
            nextLine = engine.generateSuperHybridLine(index);
        } else if (method.equals(ConversionMethod.Pwntari)) {
            nextLine = engine.generatePwntariLine(index);
        } else if (method.equals(ConversionMethod.Hybrid)) {
            nextLine = engine.generateHybridLine(index);
        } else if (method.equals(ConversionMethod.Plain)) {
            nextLine = engine.generatePlainLine(index);
        } else {
            throw new IllegalArgumentException("Method unknown: " + method);
        }
        presenter.addIrcOutputLine(nextLine, index);
    }

    public final void setParams(final ImageData id, final String charset,
            final Kick selectedKick, final int contrast, final int brightness,
            final ProcessionSettings settings) {
        engine.setParams(id, charset, selectedKick, contrast, brightness, settings);
    }

    public final void prepareEngine(final Map<IrcColor, Integer> colorMap, final Power power) {
        engine.prepareEngine(colorMap, power);
    }
}
