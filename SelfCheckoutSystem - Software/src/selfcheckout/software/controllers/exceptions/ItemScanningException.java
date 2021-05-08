package selfcheckout.software.controllers.exceptions;

/**
 * Thrown to represent any error in scanning items
 */
public class ItemScanningException extends Exception {

	private static final long serialVersionUID = 1L;

	public ItemScanningException(String message) {
		super(message);
	}
}
