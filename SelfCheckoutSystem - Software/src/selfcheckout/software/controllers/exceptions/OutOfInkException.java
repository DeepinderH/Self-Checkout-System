package selfcheckout.software.controllers.exceptions;

public class OutOfInkException extends Exception {

	private static final long serialVersionUID = 1L;

	public OutOfInkException(String message) {
		super(message);
	}

}
