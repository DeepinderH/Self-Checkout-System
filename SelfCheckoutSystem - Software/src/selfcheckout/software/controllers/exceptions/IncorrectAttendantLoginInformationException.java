package selfcheckout.software.controllers.exceptions;

/**
 * Thrown to represent when incorrect login information was used to log into machine
 */
public class IncorrectAttendantLoginInformationException extends Exception  {
	
	private static final long serialVersionUID = 1L;

	public IncorrectAttendantLoginInformationException(String message) {
		super(message);
	}
}
