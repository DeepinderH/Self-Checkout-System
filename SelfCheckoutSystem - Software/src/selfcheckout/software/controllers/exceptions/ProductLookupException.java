package selfcheckout.software.controllers.exceptions;

/*
 * This exception is thrown if the search parameters given by an attendant are invalid
 */
public class ProductLookupException extends Exception {
	private static final long serialVersionUID = 1L;

	public ProductLookupException(String message) {
		super(message);
	}

}
