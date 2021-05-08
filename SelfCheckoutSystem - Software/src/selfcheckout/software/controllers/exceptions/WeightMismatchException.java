package selfcheckout.software.controllers.exceptions;

/**
 * Exception thrown if the weight is physically valid, but does not match the weight expected by the scale
 */
public class WeightMismatchException extends ItemBaggingException {
	
	private static final long serialVersionUID = 1L;

	public WeightMismatchException(String message) {
		super(message);
	}
}
