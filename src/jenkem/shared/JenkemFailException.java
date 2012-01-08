package jenkem.shared;

/**
 * Thrown when jenkem fails.
 */
public class JenkemFailException extends Exception {
	private static final long serialVersionUID = 4123765295629994055L;
	public JenkemFailException(final String string) {
		super(string);
	}
}
