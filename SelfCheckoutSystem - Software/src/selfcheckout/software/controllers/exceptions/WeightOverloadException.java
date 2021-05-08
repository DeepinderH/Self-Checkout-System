package selfcheckout.software.controllers.exceptions;

public class WeightOverloadException extends ItemBaggingException {

	private static final long serialVersionUID = 1L;

	public WeightOverloadException(String message) {
		super(message);
	}
}
