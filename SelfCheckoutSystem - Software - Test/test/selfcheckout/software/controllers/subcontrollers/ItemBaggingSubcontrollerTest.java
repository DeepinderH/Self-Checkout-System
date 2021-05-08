package selfcheckout.software.controllers.subcontrollers;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.devices.ElectronicScale;

import org.lsmr.selfcheckout.devices.OverloadException;
import selfcheckout.software.controllers.ControllerTestConstants;
import selfcheckout.software.controllers.ProductDatabasesWrapper;
import selfcheckout.software.controllers.PurchaseManager;
import selfcheckout.software.controllers.exceptions.*;

/**
 * Tests the interactions between the different scale listeners
 *
 */
public class ItemBaggingSubcontrollerTest {

	public ProductDatabasesWrapper databaseWrapper;
	public ElectronicScale baggingScale;
	public ElectronicScale scanningScale;
	public ItemBaggingSubcontroller baggingController;
	public PurchaseManager purchaseManager;

	@Before
	public void setUp() {
		ProductDatabasesWrapper.initializeDatabases();
		databaseWrapper = new ProductDatabasesWrapper();
		purchaseManager = new PurchaseManager(databaseWrapper);
		// create two very weak scales, each with sensitivity of 1 and overload capacity of 20 g
		baggingScale = new ElectronicScale(20, 1);
		scanningScale = new ElectronicScale(10, 1);
		baggingController = new ItemBaggingSubcontroller(baggingScale, scanningScale, purchaseManager);
	}

	@After
	public void tearDown() {
		ProductDatabasesWrapper.resetDatabases();
	}

	@Test(expected = InvalidWeightException.class)
	public void testNegativeWeight() throws InvalidWeightException {
		BarcodedItem item = new BarcodedItem(new Barcode(ControllerTestConstants.VALID_BARCODE_STRING), 1.0);
		this.scanningScale.add(item);
		try {
			baggingController.handleItemBagging(item, -1.0);
		} catch (WeightMismatchException | WeightOverloadException e) {
			fail("Should throw an InvalidWeightException, not other exceptions");
		}
	}

	@Test(expected = InvalidWeightException.class)
	public void testZeroWeight() throws InvalidWeightException {
		BarcodedItem item = new BarcodedItem(new Barcode(ControllerTestConstants.VALID_BARCODE_STRING), 1.0);
		this.scanningScale.add(item);
		try {
			baggingController.handleItemBagging(item, 0.0);
		} catch (WeightMismatchException | WeightOverloadException e) {
			fail("Should throw an InvalidWeightException, not other exceptions");
		}
	}

	@Test(expected = WeightMismatchException.class)
	public void testWeightMismatch() throws WeightMismatchException {
		BarcodedItem item = new BarcodedItem(new Barcode("0"), 1.0);
		this.scanningScale.add(item);
		try {
			baggingController.handleItemBagging(item, 2.0);
		} catch (InvalidWeightException | WeightOverloadException e) {
			fail("Should throw a WeightMisMatchException, not other exceptions");
		}
	}

	@Test(expected = WeightOverloadException.class)
	public void testOverloadedBaggingArea() throws WeightOverloadException {
		BarcodedItem item = new BarcodedItem(new Barcode(ControllerTestConstants.VALID_BARCODE_STRING), 21.0);
		try {
			this.purchaseManager.addItem(item);
		} catch (NoSuchItemException e) {
			fail("item should exist");
		}
		this.scanningScale.add(item);
		try {
			baggingController.handleItemBagging(item, 21.0);
		} catch (InvalidWeightException | WeightMismatchException e) {
			fail("Should throw a WeightOverloadException, not other Exceptions");
		}
	}

	@Test
	public void testItemTransferredToBaggingArea() {
		BarcodedItem item = new BarcodedItem(new Barcode("0"), 2.0);
		this.scanningScale.add(item);
		try {
			baggingController.handleItemBagging(item, 2.0);
		} catch (ItemBaggingException e) {
			fail("Should not throw any exceptions");
		}
		try {
			assertEquals(this.baggingScale.getCurrentWeight(), 2.0, 0.001);
			assertEquals(this.scanningScale.getCurrentWeight(), 0.0, 0.001);
		} catch (OverloadException e) {
			fail("Scale overloaded");
		}
	}

	@Test
	public void addZeroPlasticBagsTest() {
		try {
			this.baggingController.addPlasticBagsUsed(0);
		} catch (WeightOverloadException e) {
			fail("Should not have been overloaded");
		}
		assertTrue(this.purchaseManager.getItems().isEmpty());
	}

	@Test
	public void addOnePlasticBagTest() {
		try {
			this.baggingController.addPlasticBagsUsed(1);
		} catch (WeightOverloadException e) {
			fail("Should not have been overloaded");
		}
		assertEquals(this.purchaseManager.getItems().size(), 1);
	}

	@Test
	public void addMultiplePlasticBagTest() {
		try {
			this.baggingController.addPlasticBagsUsed(10);
		} catch (WeightOverloadException e) {
			fail("Should not have been overloaded");
		}
		assertEquals(this.purchaseManager.getItems().size(), 10);
	}

	@Test(expected = WeightOverloadException.class)
	public void overloadBaggingScaleTest() throws WeightOverloadException {
		this.baggingController.addPlasticBagsUsed(1000);
	}

	@Test
	public void overloadBaggingScaleEmptiedTest() {
		try {
			this.baggingController.addPlasticBagsUsed(1000);
		} catch (WeightOverloadException e1) {
			assertTrue(this.purchaseManager.getItems().isEmpty());
			try {
				assertEquals(this.baggingScale.getCurrentWeight(), 0.0, 0.001);
			} catch (OverloadException e2) {
				fail("should not have been overloaded as bags should have ben removed");
			}
			return;
		}
		fail("Should have thrown WeightOverloadException");
	}
}
