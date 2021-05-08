package selfcheckout.software.controllers.exceptions;

/**
 * Thrown to represent any error in scanning items
 */
public class ItemBaggingException extends Exception {

	private static final long serialVersionUID = 1L;

	public ItemBaggingException(String message) {
		super(message);
	}
}
