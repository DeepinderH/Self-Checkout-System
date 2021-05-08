package selfcheckout.software.controllers.exceptions;

/** 
 * Exception to create alert that a barcode is not valid (e.g. is too long, does not contain only numbers etc.)
 */
public class InvalidBarcodeException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidBarcodeException(String message) {
		super(message);
	}
}
