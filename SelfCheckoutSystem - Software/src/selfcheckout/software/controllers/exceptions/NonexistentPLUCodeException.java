package selfcheckout.software.controllers.exceptions;

public class NonexistentPLUCodeException extends NoSuchItemException {

	private static final long serialVersionUID = 1L;

	public NonexistentPLUCodeException(String message) {
		super(message);
	}
}
