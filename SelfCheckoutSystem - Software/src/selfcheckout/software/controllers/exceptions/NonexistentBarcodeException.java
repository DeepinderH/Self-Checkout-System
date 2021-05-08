package selfcheckout.software.controllers.exceptions;

/** 
 * Exception to create alert that a barcode is not linked to any available product.
 */
public class NonexistentBarcodeException extends NoSuchItemException {

	private static final long serialVersionUID = 1L;

	public NonexistentBarcodeException(String message) {
		super(message);
	}

}
