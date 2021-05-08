package selfcheckout.software.controllers.exceptions;

/**
 * Alert the user (scale) that they have entered an impossible weight
 *
 */
public class InvalidWeightException extends ItemBaggingException {

	private static final long serialVersionUID = 1L;

	public InvalidWeightException(String message) {
		super(message);
	}
}
