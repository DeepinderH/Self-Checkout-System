package selfcheckout.software.controllers.subcontrollers;

import org.lsmr.selfcheckout.devices.ReceiptPrinter;
import org.lsmr.selfcheckout.devices.SimulationException;

import selfcheckout.software.controllers.exceptions.InvalidPrinterRefillException;
import selfcheckout.software.controllers.listeners.ReceiptPrinterNotificationRecorder;

public class PrinterRefillSubcontroller {

	private ReceiptPrinter receiptPrinter;
	private final ReceiptPrinterNotificationRecorder receiptPrinterNotificationRecorder;
	
	public PrinterRefillSubcontroller(ReceiptPrinter receiptPrinter) {
		this.receiptPrinterNotificationRecorder = new ReceiptPrinterNotificationRecorder();
		this.receiptPrinter = receiptPrinter;
		this.receiptPrinter.register(receiptPrinterNotificationRecorder);
	}
	
	// Idea: During printing, after getting an exception from either of the two exceptions, prompt the
	//       user to call an attendant to fill either paper and/or ink.
		
	public void attendantAddsPaper(int units) throws InvalidPrinterRefillException {
		try {
			this.receiptPrinter.addPaper(units);
		} catch (SimulationException e) {
			// Attendant puts a negative number.
			if (units < 0) {
				// Ignore. Keep the paper level as is.
				throw new InvalidPrinterRefillException("The attendant has entered a negative number. This will be ignored.");
			}
			else {
				// Attendant overflows the paper.
				// Just ignore it as well. Keep the paper level as is.
				throw new InvalidPrinterRefillException("The attendant has overflowed the paper. This will be ignored.");
			}	
		}
	}
	
	public void attendantAddsInk(int quantity) throws InvalidPrinterRefillException {
		try {
			this.receiptPrinter.addInk(quantity);
		} catch (SimulationException e) {
			// Attendant puts a negative number.
			if (quantity < 0) {
				// Ignore. Keep the ink level as is.
				throw new InvalidPrinterRefillException("The attendant has entered a negative number. This will be ignored.");
			}
			else {
				// Attendant overflows the ink.
				// Just ignore it as well. Keep the ink level as is.
				throw new InvalidPrinterRefillException("The attendant has overflowed the ink. This will be ignored.");
			}	
		}
	}
}
