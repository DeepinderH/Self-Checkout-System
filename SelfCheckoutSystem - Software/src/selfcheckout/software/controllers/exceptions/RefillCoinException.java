package selfcheckout.software.controllers.exceptions;

/**
 * Exception thrown if there is an issue with refilling the coins in a dispenser
 */

public class RefillCoinException extends Exception {
	private static final long serialVersionUID = 1L;

	public RefillCoinException(String message) {
		super(message);
	}

}
