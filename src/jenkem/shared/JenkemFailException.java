package jenkem.shared;

/**
 * Thrown when jenkem fails.
 */
public class JenkemFailException extends Exception {
	private static final long serialVersionUID = 1L;
	public JenkemFailException(String string) {
		super(string);
	}
}
