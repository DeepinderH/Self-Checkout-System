package selfcheckout.software.controllers.subcontrollers;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.devices.ReceiptPrinter;
import selfcheckout.software.controllers.exceptions.InvalidPrinterRefillException;

public class PrinterRefillSubcontrollerTest {
	
	PrinterRefillSubcontroller printerRefillSubcontroller;
	ReceiptPrinter receiptPrinter;
	
	@Before
	public void setUp() {
		this.receiptPrinter = new ReceiptPrinter();
		this.printerRefillSubcontroller = new PrinterRefillSubcontroller(this.receiptPrinter);
	}
	
	@Test (expected = InvalidPrinterRefillException.class)
	public void testAttendantEnteringNegativeInk() throws InvalidPrinterRefillException {
		this.printerRefillSubcontroller.attendantAddsInk(-500);
	}
	
	@Test (expected = InvalidPrinterRefillException.class)
	public void testAttendantEnteringNegativePaper() throws InvalidPrinterRefillException {
		this.printerRefillSubcontroller.attendantAddsPaper(-50);
	}
	
	@Test (expected = InvalidPrinterRefillException.class)
	public void testAttendantOverflowingInk() throws InvalidPrinterRefillException {
		this.printerRefillSubcontroller.attendantAddsInk(2000000);
	}
	
	@Test (expected = InvalidPrinterRefillException.class)
	public void testAttendantOverflowingPaper() throws InvalidPrinterRefillException {
		this.printerRefillSubcontroller.attendantAddsPaper(2000000);
	}
	
	@Test
	public void testAttendantEnteringValidAmountOfInk() {
		try {
			this.printerRefillSubcontroller.attendantAddsInk(500);
		} catch (InvalidPrinterRefillException e) {
			e.printStackTrace();
			fail("You've gotten an InvalidPrinterRefillException while entering a"
					+ "valid amount of ink. This should never happen.");
		}
	}
	
	@Test
	public void testAttendantEnteringValidAmountOfPaper() {
		try {
			this.printerRefillSubcontroller.attendantAddsPaper(50);
		} catch (InvalidPrinterRefillException e) {
			e.printStackTrace();
			fail("You've gotten an InvalidPrinterRefillException while entering a"
					+ "valid amount of paper. This should never happen.");
		}
	}
}
