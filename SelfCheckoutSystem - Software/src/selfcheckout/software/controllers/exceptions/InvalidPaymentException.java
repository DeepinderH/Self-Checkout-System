package selfcheckout.software.controllers.exceptions;

/**
 * Thrown to represent any error in scanning items
 */
public class InvalidPaymentException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidPaymentException(String message) {
		super(message);
	}
}
