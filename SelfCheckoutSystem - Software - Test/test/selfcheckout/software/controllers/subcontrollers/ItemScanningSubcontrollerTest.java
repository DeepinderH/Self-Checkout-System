package selfcheckout.software.controllers.subcontrollers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.devices.BarcodeScanner;
import org.lsmr.selfcheckout.devices.ElectronicScale;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.listeners.BarcodeScannerListener;

import selfcheckout.software.controllers.ControllerTestConstants;
import selfcheckout.software.controllers.ProductDatabasesWrapper;
import selfcheckout.software.controllers.PurchaseManager;
import selfcheckout.software.controllers.exceptions.*;

import static org.junit.Assert.*;

/**
 * Tests the interactions between the different scale listeners
 *
 */
public class ItemScanningSubcontrollerTest {

	private BarcodeScanner barcodeScanner;
	private ElectronicScale scanningScale;
	private PurchaseManager purchaseManager;
	private ProductDatabasesWrapper databasesWrapper;
	public ItemScanningSubcontroller scanningController;

	@Before
	public void setUp() {
		ProductDatabasesWrapper.initializeDatabases();
		// create two very weak scales, each with sensitivity of 1 and overload capacity of 20 g
		this.barcodeScanner = new BarcodeScanner();
		this.scanningScale = new ElectronicScale(10, 1);
		this.databasesWrapper = new ProductDatabasesWrapper();
		this.purchaseManager = new PurchaseManager(this.databasesWrapper);
	}

	@After
	public void tearDown() {
		ProductDatabasesWrapper.resetDatabases();
	}

	@Test(expected = InvalidBarcodeException.class)
	public void invalidBarcodeTest() throws InvalidBarcodeException {
		this.scanningController = new ItemScanningSubcontroller(
			this.barcodeScanner, this.scanningScale, this.purchaseManager);
		try {
			this.scanningController.scanItem("ABC", 1.0);
		} catch (NonexistentBarcodeException | InvalidWeightException | WeightOverloadException e) {
			fail("Should throw an InvalidBarcodeException, not other exceptions");
		}
	}

	@Test(expected = InvalidWeightException.class)
	public void invalidWeightTest() throws InvalidWeightException {
		this.scanningController = new ItemScanningSubcontroller(
			this.barcodeScanner, this.scanningScale, this.purchaseManager);
		try {
			this.scanningController.scanItem("0", -1.0);
		} catch (NonexistentBarcodeException | InvalidBarcodeException | WeightOverloadException e) {
			fail("Should throw an InvalidBarcodeException, not other exceptions");
		}
	}

	@Test(expected = WeightOverloadException.class)
	public void overloadedScanningScaleTest() throws WeightOverloadException {
		this.scanningController = new ItemScanningSubcontroller(
			this.barcodeScanner, this.scanningScale, this.purchaseManager);
		try {
			this.scanningController.scanItem(ControllerTestConstants.VALID_BARCODE_STRING, 11.0);
		} catch (NonexistentBarcodeException | InvalidBarcodeException | InvalidWeightException e) {
			fail("Should throw a WeightOverloadException, not other exceptions");
		}
	}

	@Test
	public void overloadedScanningScaleItemRemovedTest() {
		this.scanningController = new ItemScanningSubcontroller(
			this.barcodeScanner, this.scanningScale, this.purchaseManager);
		try {
			this.scanningController.scanItem(ControllerTestConstants.VALID_BARCODE_STRING, 11.0);
		} catch (WeightOverloadException e1) {
			try {
				assertEquals(this.scanningScale.getCurrentWeight(), 0, 0.001);
			} catch (OverloadException e2) {
				fail("Should not be overloaded");
			}
			return;
		}
		catch (NonexistentBarcodeException | InvalidBarcodeException | InvalidWeightException e1) {
			fail("Should throw a WeightOverloadException, not other exceptions");
		}
		fail("Did not throw an exception when expected");
	}

	// should reject a barcode with no matching product in the database
	@Test(expected = NonexistentBarcodeException.class)
	public void noMatchingBarcodeTest() throws NoSuchItemException {
		this.scanningController = new ItemScanningSubcontroller(
			this.barcodeScanner, this.scanningScale, this.purchaseManager);
		BarcodedItem item = new BarcodedItem(new Barcode("32156"), 3.2);
		purchaseManager.addItem(item);
	}

	private static class NoNotificationBarcodeScanner extends BarcodeScanner {
		@Override
		public void scan(Item item) {
			// do nothing
		}
	}

	@Test(expected = InvalidBarcodeException.class)
	public void scanFailureTest() throws InvalidBarcodeException {
		this.barcodeScanner = new NoNotificationBarcodeScanner();
		this.scanningController = new ItemScanningSubcontroller(
			this.barcodeScanner, this.scanningScale, this.purchaseManager);
		Barcode validItemBarcode = new Barcode(ControllerTestConstants.VALID_BARCODE_STRING);
		try {
			databasesWrapper.getProductByBarcode(validItemBarcode);
		} catch (NonexistentBarcodeException e) {
			fail("This test requires a valid Barcode or it will not test " +
				 "behaviour of ItemScanningSubcontroller barcodeScanner failures");
		}
		try {
			this.scanningController.scanItem(validItemBarcode.toString(), 1.0);
		} catch (NonexistentBarcodeException | InvalidWeightException  | WeightOverloadException e) {
			fail("Expected an InvalidBarcodeException to be thrown, not other Exceptions");
		}
	}

	private static class AlwaysNotificationBarcodeScanner extends BarcodeScanner {
		@Override
		public void scan(Item item) {
			// item must be a BarcodedItem for this test stub
			for(BarcodeScannerListener l : listeners) {
				l.barcodeScanned(this, ((BarcodedItem) item).getBarcode());
			}
		}
	}

	@Test
	public void scanSuccessTest() {
		this.barcodeScanner = new AlwaysNotificationBarcodeScanner();
		this.scanningController = new ItemScanningSubcontroller(
			this.barcodeScanner, this.scanningScale, this.purchaseManager);
		Barcode validItemBarcode = new Barcode(ControllerTestConstants.VALID_BARCODE_STRING);
		try {
			databasesWrapper.getProductByBarcode(validItemBarcode);
		} catch (NonexistentBarcodeException e) {
			fail("This test requires a valid Barcode");
		}
		try {
			this.scanningController.scanItem(validItemBarcode.toString(), 1.0);
		} catch (Exception e) {
			fail("No Exception should be thrown");
		}
	}

	@Test
	public void addItemToPurchaseTest() {
		BarcodedItem item1 = new BarcodedItem(new Barcode(ControllerTestConstants.VALID_BARCODE_STRING), 6.7);
		try {
			purchaseManager.addItem(item1);
		} catch (NoSuchItemException e) {
			fail("Should successfully detect barcode");
		}
		assertEquals(purchaseManager.getItems().size(), 1);
	}
	
	@Test(expected = WeightOverloadException.class)
	public void failedToBagItemTest() throws WeightOverloadException {
		this.barcodeScanner = new AlwaysNotificationBarcodeScanner();
		this.scanningController = new ItemScanningSubcontroller(
				this.barcodeScanner, this.scanningScale, this.purchaseManager);
		try {
			this.scanningController.scanItem(ControllerTestConstants.VALID_BARCODE_STRING, 4.6);
			this.scanningController.scanItem(ControllerTestConstants.VALID_BARCODE_STRING, 3.4);
		} catch (InvalidBarcodeException | NonexistentBarcodeException | InvalidWeightException e) {
			fail("Should throw WeightOverloadException, not other exceptions");
		}
	}

	@Test(expected = NonexistentBarcodeException.class)
	public void nonexistentBarcodeTest() throws NonexistentBarcodeException {
		this.barcodeScanner = new AlwaysNotificationBarcodeScanner();
		this.scanningController = new ItemScanningSubcontroller(
			this.barcodeScanner, this.scanningScale, this.purchaseManager);
		try {
			this.scanningController.scanItem("890754", 1.0);
		} catch ( InvalidWeightException | WeightOverloadException | InvalidBarcodeException e) {
			fail("Should throw an NonexistentBarcodeException, not other exceptions");
		}
	}
}
