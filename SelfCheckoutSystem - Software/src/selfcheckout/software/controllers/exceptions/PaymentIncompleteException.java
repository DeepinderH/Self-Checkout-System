package selfcheckout.software.controllers.exceptions;


/**
 * Exception thrown if payment could not be completed
 */
public class PaymentIncompleteException extends Exception {

	private static final long serialVersionUID = 1L;

	public PaymentIncompleteException(String message) {
		super(message);
	}
}
