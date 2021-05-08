package selfcheckout.software.controllers.exceptions;

public class InvalidPLUCodeException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidPLUCodeException(String message) {
		super(message);
	}
}
