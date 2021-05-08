package selfcheckout.software.controllers.subcontrollers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.devices.ElectronicScale;
import org.lsmr.selfcheckout.devices.OverloadException;

import selfcheckout.software.controllers.ControllerTestConstants;
import selfcheckout.software.controllers.ProductDatabasesWrapper;
import selfcheckout.software.controllers.PurchaseManager;
import selfcheckout.software.controllers.exceptions.*;

import static org.junit.Assert.*;

public class PLUItemInputSubcontrollerTests {
	
	private ElectronicScale weighingScale;
	private PurchaseManager purchaseManager;
	private ProductDatabasesWrapper databasesWrapper;
	private PLUItemInputSubcontroller pluInputSubcontroller;

	@Before
	public void setUp() {
		ProductDatabasesWrapper.initializeDatabases();
		// create two very weak scales, each with sensitivity of 1 and overload capacity of 20 g
		this.weighingScale = new ElectronicScale(10, 1);
		this.databasesWrapper = new ProductDatabasesWrapper();
		this.purchaseManager = new PurchaseManager(this.databasesWrapper);
		this.pluInputSubcontroller = new PLUItemInputSubcontroller(this.weighingScale, this.purchaseManager);
	}

	@After
	public void tearDown() {
		ProductDatabasesWrapper.resetDatabases();
	}

	@Test (expected = InvalidPLUCodeException.class)
	public void invalidPLUCodeTest() throws InvalidPLUCodeException {
		try {
			this.pluInputSubcontroller.enterPLUCode("AsYRj", 2.0);
		} catch (NonexistentPLUCodeException | InvalidWeightException | WeightOverloadException e) {
			fail("Should throw InvalidPLUCodeException, not other exceptions");
		} 
	}
	
	@Test (expected = InvalidPLUCodeException.class)
	public void invalidPLUCodeTest2() throws InvalidPLUCodeException {
		try {
			this.pluInputSubcontroller.enterPLUCode("123", 2.0);
		} catch (NonexistentPLUCodeException | InvalidWeightException | WeightOverloadException e) {
			fail("Should throw InvalidPLUCodeException, not other exceptions");
		} 
	}
	
	@Test (expected = NonexistentPLUCodeException.class)
	public void nonexistentPLUCodeTest() throws NonexistentPLUCodeException {
		try {
			this.pluInputSubcontroller.enterPLUCode("1234", 2.0);
		} catch (InvalidPLUCodeException | InvalidWeightException | WeightOverloadException e) {
			fail("Should throw NonexistentPLUCodeException, not other exceptions");
		}
	}

	@Test (expected = InvalidWeightException.class)
	public void invalidWeightTest() throws InvalidWeightException {
		try {
			this.pluInputSubcontroller.enterPLUCode(ControllerTestConstants.VALID_PLUCODE_STRING, -1.0);
		} catch (NonexistentPLUCodeException | InvalidPLUCodeException | WeightOverloadException e) {
			fail("Should throw InvalidWeightException, not other exceptions");
		}
	}

	@Test (expected = WeightOverloadException.class)
	public void overloadWeighingScale() throws WeightOverloadException {
		try {
			this.pluInputSubcontroller.enterPLUCode(ControllerTestConstants.VALID_PLUCODE_STRING, 11.0);
		} catch (NonexistentPLUCodeException | InvalidPLUCodeException | InvalidWeightException e) {
			fail("Should throw WeightOverloadException, not other exceptions");
		}
	}

	@Test
	public void overloadWeighingScaleRemoveItemTest() {
		try {
			this.pluInputSubcontroller.enterPLUCode(ControllerTestConstants.VALID_BARCODE_STRING, 11.0);
		} catch (WeightOverloadException e) {
			try {
				assertEquals(this.weighingScale.getCurrentWeight(), 0, 0.001);
			} catch (OverloadException e1) {
				fail("OverloadException should not be thrown");
			}
			return;
		} catch (NonexistentPLUCodeException | InvalidPLUCodeException | InvalidWeightException e) {
			fail("Should throw WeightOverloadException, not other exceptions");
		}
		fail("Exception was expected but was not thrown");
	}

	@Test
	public void pluCodeEnteredSuccessfullyTest() {
		PriceLookupCode plu = new PriceLookupCode(ControllerTestConstants.VALID_PLUCODE_STRING);
		try {
			databasesWrapper.getProductByPLUCode(plu);
		} catch (NonexistentPLUCodeException e) {
			fail("Price lookup code should be valid");
		}
		
		try {
			this.pluInputSubcontroller.enterPLUCode(plu.toString(), 2.0);
		} catch (NonexistentPLUCodeException | InvalidPLUCodeException | InvalidWeightException
				| WeightOverloadException e) {
			fail("Should not be throwing any exceptions");
		}
	}
	
	@Test
	public void itemAddedToPurchaseTest() {
		PriceLookupCode plu = new PriceLookupCode(ControllerTestConstants.VALID_PLUCODE_STRING);
		try {
			databasesWrapper.getProductByPLUCode(plu);
		} catch (NonexistentPLUCodeException e) {
			fail("Price lookup code should be valid");
		}
		
		try {
			this.pluInputSubcontroller.enterPLUCode(plu.toString(), 2.0);
		} catch (NonexistentPLUCodeException | InvalidPLUCodeException | InvalidWeightException
				| WeightOverloadException e) {
			fail("Should not throw any exceptions");
		}
		assertEquals(purchaseManager.getItems().size(), 1);
	}

	// Attempts to add new item when there is another item on the scale
	@Test (expected = WeightOverloadException.class)
	public void failedToBagItemTest() throws WeightOverloadException {
		try {
			this.pluInputSubcontroller.enterPLUCode(ControllerTestConstants.VALID_PLUCODE_STRING, 2.4);
			this.pluInputSubcontroller.enterPLUCode(ControllerTestConstants.VALID_PLUCODE_STRING, 1.3);
		} catch (NonexistentPLUCodeException | InvalidPLUCodeException | InvalidWeightException e) {
			fail("Should throw WeightOverloadExceptions, not other exceptions");
		}
	}
}
