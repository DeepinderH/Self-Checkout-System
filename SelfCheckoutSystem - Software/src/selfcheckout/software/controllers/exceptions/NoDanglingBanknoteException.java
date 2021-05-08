package selfcheckout.software.controllers.exceptions;

/**
 * Represents an error where the user attempts to remove a dangling
 * banknote but there is no dangling banknote to remove
 *
 * This is an unchecked exception since we expect the program to prevent
 * the user from attempting to remove a dangling banknote when it
 * doesn't exist (so this exception signals that the control software
 * is improperly configured)
 */
public class NoDanglingBanknoteException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NoDanglingBanknoteException(String message) {
		super(message);
	}
}
