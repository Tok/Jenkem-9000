package jenkem.shared;

/**
 * Thrown when scheme is not known.
 */
public class SchemeUnknownException extends JenkemFailException {
	private static final long serialVersionUID = 4519760584560378726L;
	public SchemeUnknownException(final String string) {
		super(string + " is not a jenkem scheme.");
	}
}