package selfcheckout.software.controllers.exceptions;

/**
 * Thrown if someone tries to insert a membership card not linked to a preexisting account
 * 
 */
public class NotAMemberException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public NotAMemberException(String message) {
		super(message);
	}

}
