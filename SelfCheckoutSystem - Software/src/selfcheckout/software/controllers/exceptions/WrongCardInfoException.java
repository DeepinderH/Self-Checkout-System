package selfcheckout.software.controllers.exceptions;

/**
 * Exception thrown if card info was found to be wrong
 */
public class WrongCardInfoException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public WrongCardInfoException(String message) {
		super(message);
	}
}
