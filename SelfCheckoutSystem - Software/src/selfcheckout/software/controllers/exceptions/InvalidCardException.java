package selfcheckout.software.controllers.exceptions;

public class InvalidCardException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public InvalidCardException(String message) {
		super(message);
	}

}
