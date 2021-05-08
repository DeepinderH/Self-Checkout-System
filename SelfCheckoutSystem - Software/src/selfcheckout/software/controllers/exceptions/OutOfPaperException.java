package selfcheckout.software.controllers.exceptions;

public class OutOfPaperException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public OutOfPaperException(String message) {
		super(message);
	}

}
