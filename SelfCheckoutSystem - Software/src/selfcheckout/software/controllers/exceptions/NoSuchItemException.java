package selfcheckout.software.controllers.exceptions;

/**
 * Exception to create alert that an Item that we tried to add an
 * order does not exist
 */
public class NoSuchItemException extends Exception {

	private static final long serialVersionUID = 1L;

	public NoSuchItemException(String message) {
		super(message);
	}

}
