package selfcheckout.software.controllers.exceptions;

public class OrderIncompleteException extends Exception {

	private static final long serialVersionUID = 1L;

	public OrderIncompleteException(String message) {
		super(message);
	}
	
}
