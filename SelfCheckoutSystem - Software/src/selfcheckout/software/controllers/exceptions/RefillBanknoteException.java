package selfcheckout.software.controllers.exceptions;
/*
 * This exception is thrown if there is an error loading banknotes into a dispenser
 */
public class RefillBanknoteException extends Exception{
	
	private static final long serialVersionUID = 1L;

	public RefillBanknoteException(String message) {
		super(message);
	}
}
