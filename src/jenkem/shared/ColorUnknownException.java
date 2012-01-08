package jenkem.shared;

/**
 * Thrown when color isn't known.
 */
public class ColorUnknownException extends JenkemFailException {
	private static final long serialVersionUID = 1L;
	public ColorUnknownException(String string) {
		super(string + " is not a jenkem color.");
	}
}