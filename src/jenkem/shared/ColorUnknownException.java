package jenkem.shared;

/**
 * Thrown when color isn't known.
 */
public class ColorUnknownException extends JenkemFailException {
	private static final long serialVersionUID = -7408964116430497926L;
	public ColorUnknownException(final String string) {
		super(string + " is not a jenkem color.");
	}
}