package selfcheckout.software.controllers.exceptions;

public class BanknoteRejectedException extends Exception {

	private static final long serialVersionUID = 1L;

	public BanknoteRejectedException(String message) {
		super(message);
	}
}
