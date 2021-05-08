package selfcheckout.software.controllers.exceptions;

/**
 * Exception thrown if we already have sufficient payment
 */
public class PaymentNotRequiredException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public PaymentNotRequiredException(String message) {
		super(message);
	}
}
