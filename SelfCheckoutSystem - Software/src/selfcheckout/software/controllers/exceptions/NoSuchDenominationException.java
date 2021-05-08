package selfcheckout.software.controllers.exceptions;

/**
 * Thrown when the change algorithm thinks a non-existent denomination of currency should be returned as change
 */
public class NoSuchDenominationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NoSuchDenominationException(String message) {
		super(message);
	}
}
