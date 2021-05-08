package selfcheckout.software.controllers.exceptions;

public class StorageUnitFullException extends Exception {

	private static final long serialVersionUID = 1L;

	public StorageUnitFullException(String message) {
		super(message);
	}
}
