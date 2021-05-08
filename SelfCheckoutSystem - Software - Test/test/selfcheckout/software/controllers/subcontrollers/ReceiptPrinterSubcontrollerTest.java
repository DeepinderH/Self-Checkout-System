package selfcheckout.software.controllers.subcontrollers;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.PLUCodedItem;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.devices.ReceiptPrinter;

import selfcheckout.software.controllers.ProductDatabasesWrapper;
import selfcheckout.software.controllers.Purchase;
import selfcheckout.software.controllers.PurchaseManager;
import selfcheckout.software.controllers.exceptions.*;

public class ReceiptPrinterSubcontrollerTest {
	private ReceiptPrinter receiptPrinter;
	private ReceiptPrinterSubcontroller receiptPrinterSubcontroller;
	private PurchaseManager purchaseManager;
	private Purchase purchase;
	private BigDecimal totalPayment;
	
	@Before
	public void setUp() {
		ProductDatabasesWrapper.initializeDatabases();
		this.receiptPrinter = new ReceiptPrinter();
		ProductDatabasesWrapper productDatabasesWrapper = new ProductDatabasesWrapper();
		this.purchase = new Purchase();
		this.purchaseManager = new PurchaseManager(productDatabasesWrapper);
		this.receiptPrinterSubcontroller = new ReceiptPrinterSubcontroller(this.receiptPrinter,
				this.purchaseManager, productDatabasesWrapper);
	}
	
	@After
	public void tearDown() {
		ProductDatabasesWrapper.resetDatabases();
	}
	
	@Test (expected = OutOfInkException.class)
	public void testPrinterOutOfInk() throws OutOfInkException, OutOfPaperException {
		
		this.receiptPrinter.addInk(1);
		this.receiptPrinter.addPaper(50);
		
		double breadWeight = 1.00; // Not actual weight. This is just for testing.
		BarcodedItem breadTMItem = new BarcodedItem(new Barcode("23578"), breadWeight);
		
		double appleWeight = 1.00; // Not actual weight. This is just for testing.
		PLUCodedItem appleItem = new PLUCodedItem(new PriceLookupCode("4131"), appleWeight);
		
		this.totalPayment = new BigDecimal("20.00");
		
		// Simulates customer having purchased the following items. (Used by printReceipt() in ReceiptPrinterSubcontroller.)
		this.purchase.addItem(breadTMItem);
		this.purchase.addItem(appleItem);
		try {
			this.purchaseManager.addItem(breadTMItem);
			this.purchaseManager.addItem(appleItem);
		} catch (NoSuchItemException e) {
			e.printStackTrace();
			fail("Item doesn't exist!");
		}
		this.purchaseManager.saveCurrentPurchase();
		
		this.receiptPrinterSubcontroller.printReceipt(this.totalPayment);
	}
	
	@Test (expected = OutOfPaperException.class)
	public void testPrinterOutOfPaper() throws OutOfInkException, OutOfPaperException {
		
		this.receiptPrinter.addInk(500);
		this.receiptPrinter.addPaper(1);
		
		double breadWeight = 1.00; // Not actual weight. This is just for testing.
		BarcodedItem breadTMItem = new BarcodedItem(new Barcode("23578"), breadWeight);
		
		double appleWeight = 1.00; // Not actual weight. This is just for testing.
		PLUCodedItem appleItem = new PLUCodedItem(new PriceLookupCode("4131"), appleWeight);
		
		this.totalPayment = new BigDecimal("20.00");
		
		// Simulates customer having purchased the following items. (Used by printReceipt() in ReceiptPrinterSubcontroller.)
		this.purchase.addItem(breadTMItem);
		this.purchase.addItem(appleItem);
		try {
			this.purchaseManager.addItem(breadTMItem);
			this.purchaseManager.addItem(appleItem);
		} catch (NoSuchItemException e) {
			e.printStackTrace();
			fail("Item doesn't exist!");
		}
		this.purchaseManager.saveCurrentPurchase();
		
		this.receiptPrinterSubcontroller.printReceipt(this.totalPayment);
	}
	
	@Test
	public void testCharacterLineTooLong() throws OutOfInkException, OutOfPaperException {

		this.receiptPrinter.addInk(500);
		this.receiptPrinter.addPaper(50);

		StringBuilder sb = new StringBuilder();
		for(int i = 0 ; i < 65 ; i++) {
			sb.append('=');
		}
		String toPrint = sb.toString();
		this.receiptPrinterSubcontroller.stringPrinter(toPrint);
		this.receiptPrinter.cutPaper();
		String actual = this.receiptPrinter.removeReceipt();
		
		// 60 is the maximum character length per line in the receipt.
		sb = new StringBuilder();
		for(int i = 0 ; i < 60 ; i++) {
			sb.append('=');
		}
		sb.append('\n');
		for(int i = 0 ; i < 5 ; i++) {
			sb.append('=');
		}
		String expected = sb.toString();
		
		assertEquals(expected, actual);
	}
	
	// Note: This tests for printing a receipt with a BarcodedItem and a PLUCodedItem as well.
	@Test
	public void testPrinterFilledWithInkAndFilledWithPaper() throws OutOfInkException, OutOfPaperException {
		
		receiptPrinter.addInk(500);
		receiptPrinter.addPaper(50);
		
		double breadWeight = 1.00; // Not actual weight. This is just for testing.
		BarcodedItem breadTMItem = new BarcodedItem(new Barcode("23578"), breadWeight);
		
		double appleWeight = 1.00; // Not actual weight. This is just for testing.
		PLUCodedItem appleItem = new PLUCodedItem(new PriceLookupCode("4131"), appleWeight);
		
		// Simulates customer having purchased the following items. (Used by printReceipt() in ReceiptPrinterSubcontroller.)
		this.purchase.addItem(breadTMItem);
		this.purchase.addItem(appleItem);
		try {
			this.purchaseManager.addItem(breadTMItem);
			this.purchaseManager.addItem(appleItem);
		} catch (NoSuchItemException e) {
			e.printStackTrace();
			fail("Item doesn't exist!");
		}
		this.purchaseManager.saveCurrentPurchase();
		
		this.totalPayment = new BigDecimal("20.00");
		
		String expectedReceipt =	"============================\n"
					+	"                          Receipt\n"
					+	"============================\n"
					+	"Purchased Items:\n"
					+	"\n"
					+	"Bread (TM): $10.00\n"
					+	"Fuji Apple: $3.00\n"
					+	"\n"
					+	"Total Price: $13.00\n"
					+	"Total Payment: $20.00\n"
					+	"Total Change: $7.00\n"
					+	"============================\n";
		
		String actualReceipt = this.receiptPrinterSubcontroller.printReceipt(totalPayment);
		assertEquals(expectedReceipt, actualReceipt);
	}

	@Test(expected = ControlSoftwareException.class)
	public void testNoBarcodedProductMatching() throws OutOfInkException, OutOfPaperException {

		receiptPrinter.addInk(500);
		receiptPrinter.addPaper(50);

		double breadWeight = 1.00; // Not actual weight. This is just for testing.
		BarcodedItem breadTMItem = new BarcodedItem(new Barcode("23578"), breadWeight);

		// Simulates customer having purchased the following items. (Used by printReceipt() in ReceiptPrinterSubcontroller.)
		this.purchase.addItem(breadTMItem);
		try {
			this.purchaseManager.addItem(breadTMItem);
		} catch (NoSuchItemException e) {
			e.printStackTrace();
			fail("Item doesn't exist!");
		}
		this.purchaseManager.saveCurrentPurchase();
		ProductDatabasesWrapper.resetDatabases();
		this.receiptPrinterSubcontroller.printReceipt(totalPayment);
	}


	// Note: This tests for printing a receipt with a BarcodedItem and a PLUCodedItem as well.
	@Test
	public void testNoPLUCodedProductMatching() throws OutOfInkException, OutOfPaperException {

		receiptPrinter.addInk(500);
		receiptPrinter.addPaper(50);

		double appleWeight = 1.00; // Not actual weight. This is just for testing.
		PLUCodedItem appleItem = new PLUCodedItem(new PriceLookupCode("4131"), appleWeight);

		// Simulates customer having purchased the following items. (Used by printReceipt() in ReceiptPrinterSubcontroller.)
		this.purchase.addItem(appleItem);
		try {
			this.purchaseManager.addItem(appleItem);
		} catch (NoSuchItemException e) {
			e.printStackTrace();
			fail("Item doesn't exist!");
		}
		this.purchaseManager.saveCurrentPurchase();
		this.totalPayment = new BigDecimal("20.00");
		this.receiptPrinterSubcontroller.printReceipt(totalPayment);
	}

	@Test
	public void testPrintingAReceiptWithAllBarcodedItems() throws OutOfInkException, OutOfPaperException {
		
		receiptPrinter.addInk(500);
		receiptPrinter.addPaper(50);
		
		double milkWeight = 1.00; // Not actual weight. This is just for testing.
		BarcodedItem milkItem = new BarcodedItem(new Barcode("29861259"), milkWeight);
		
		double spamWeight = 1.00; // Not actual weight. This is just for testing.
		BarcodedItem spamItem = new BarcodedItem(new Barcode("791666190"), spamWeight);
		
		// Simulates customer having purchased the following items. (Used by printReceipt() in ReceiptPrinterSubcontroller.)
		this.purchase.addItem(milkItem);
		this.purchase.addItem(spamItem);
		try {
			this.purchaseManager.addItem(milkItem);
			this.purchaseManager.addItem(spamItem);
		} catch (NoSuchItemException e) {
			e.printStackTrace();
			fail("Item doesn't exist!");
		}
		this.purchaseManager.saveCurrentPurchase();
		
		this.totalPayment = new BigDecimal("40.00");
		
		String expectedReceipt =	"============================\n"
					+	"                          Receipt\n"
					+	"============================\n"
					+	"Purchased Items:\n"
					+	"\n"
					+	"Milk: The Drink: $15.00\n"
					+	"Spam!: $20.00\n"
					+	"\n"
					+	"Total Price: $35.00\n"
					+	"Total Payment: $40.00\n"
					+	"Total Change: $5.00\n"
					+	"============================\n";
		
		String actualReceipt = this.receiptPrinterSubcontroller.printReceipt(totalPayment);
		assertEquals(expectedReceipt, actualReceipt);
	}

	@Test
	public void testPrintingAReceiptWithAllPLUCodedItems() throws OutOfInkException, OutOfPaperException {
		
		receiptPrinter.addInk(500);
		receiptPrinter.addPaper(50);
		
		double bananaWeight = 1.00; // Not actual weight. This is just for testing.
		PLUCodedItem bananaItem = new PLUCodedItem(new PriceLookupCode("4011"), bananaWeight);
		
		double orangeWeight = 1.00; // Not actual weight. This is just for testing.
		PLUCodedItem orangeItem = new PLUCodedItem(new PriceLookupCode("3107"), orangeWeight);
		
		// Simulates customer having purchased the following items. (Used by printReceipt() in ReceiptPrinterSubcontroller.)
		this.purchase.addItem(bananaItem);
		this.purchase.addItem(orangeItem);
		try {
			this.purchaseManager.addItem(bananaItem);
			this.purchaseManager.addItem(orangeItem);
		} catch (NoSuchItemException e) {
			e.printStackTrace();
			fail("Item doesn't exist!");
		}
		this.purchaseManager.saveCurrentPurchase();
		
		this.totalPayment = new BigDecimal("10.00");
		
		String expectedReceipt =	"============================\n"
					+	"                          Receipt\n"
					+	"============================\n"
					+	"Purchased Items:\n"
					+	"\n"
					+	"Cavendish Bananas: $1.00\n"
					+	"Navel Oranges: $2.00\n"
					+	"\n"
					+	"Total Price: $3.00\n"
					+	"Total Payment: $10.00\n"
					+	"Total Change: $7.00\n"
					+	"============================\n";
		
		String actualReceipt = this.receiptPrinterSubcontroller.printReceipt(totalPayment);
		assertEquals(expectedReceipt, actualReceipt);
	}
}
