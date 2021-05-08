package selfcheckout.software.views.gui.pages;

/**
 * A class that is purely used to ensure that an expected method is called
 * during automated tests
 */
public class ExpectedThrownException extends RuntimeException {
	public ExpectedThrownException(String message) {
		super(message);
	}

	public ExpectedThrownException() {
		super();
	}
}
