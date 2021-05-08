package selfcheckout.software.controllers.exceptions;

/**
 * Exception thrown if attendant account already exists in database
 */
public class AttendantAccountAlreadyExistsException extends Exception {

	private static final long serialVersionUID = 1L;

	public AttendantAccountAlreadyExistsException(String message) {
		super(message);
	}
}
