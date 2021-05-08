package selfcheckout.software.controllers.exceptions;

/**
 * Exception thrown if we cannot read the card
 */
public class CannotReadCardException extends Exception {

	private static final long serialVersionUID = 1L;

	public CannotReadCardException(String message) {
		super(message);
	}
}
