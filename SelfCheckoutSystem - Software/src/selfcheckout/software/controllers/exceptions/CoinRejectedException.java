package selfcheckout.software.controllers.exceptions;

public class CoinRejectedException extends Exception {

	private static final long serialVersionUID = 1L;

	public CoinRejectedException(String message) {
		super(message);
	}
}
