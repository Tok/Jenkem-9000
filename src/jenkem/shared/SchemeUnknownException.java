package jenkem.shared;

/**
 * Thrown when scheme is not known.
 */
public class SchemeUnknownException extends JenkemFailException {
	private static final long serialVersionUID = 1L;
	public SchemeUnknownException(String string) {
		super(string + " is not a jenkem scheme.");
	}
}