package selfcheckout.software.controllers.exceptions;

import java.math.BigDecimal;

/**
 * Thrown whenever the self-checkout machine cannot return change
 */
public class CannotReturnChangeException extends Exception {

	private static final long serialVersionUID = 1L;

	public BigDecimal amountOwed;

	/**
	 * Constructs an instance of CannotReturnChangeException
	 * @param amountOwed The amount the self-checkout machine still owes the user
	 * @param message A message explaining what went wrong
	 */
	public CannotReturnChangeException(BigDecimal amountOwed, String message) {
		super(message);
		this.amountOwed = amountOwed;
	}
}
