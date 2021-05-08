package selfcheckout.software.controllers.exceptions;

public class InvalidPrinterRefillException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public InvalidPrinterRefillException(String message) {
		super(message);
	}

}
