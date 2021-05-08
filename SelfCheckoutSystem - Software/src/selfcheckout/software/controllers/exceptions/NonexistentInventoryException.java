package selfcheckout.software.controllers.exceptions;


/** 
 * Exception to create alert that there is no specified inventory for a product
 */

public class NonexistentInventoryException extends NoSuchItemException {

	private static final long serialVersionUID = 1L;

	public NonexistentInventoryException(String message) {
		super(message);
	}

}
