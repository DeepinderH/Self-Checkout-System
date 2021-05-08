package selfcheckout.software.controllers.subcontrollers;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.devices.ElectronicScale;
import org.lsmr.selfcheckout.devices.OverloadException;
import selfcheckout.software.controllers.BagItem;
import selfcheckout.software.controllers.ProductDatabasesWrapper;
import selfcheckout.software.controllers.PurchaseManager;
import selfcheckout.software.controllers.exceptions.ControlSoftwareException;
import selfcheckout.software.controllers.exceptions.InvalidWeightException;
import selfcheckout.software.controllers.exceptions.NoSuchItemException;
import selfcheckout.software.controllers.exceptions.WeightOverloadException;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class BagAdditionSubcontrollerTests {

	private BagAdditionSubcontroller bas;
	private PurchaseManager purchaseManager;
	private ElectronicScale baggingScale;

	@Before
	public void setUp() {
		this.baggingScale = new ElectronicScale(10, 1);
		this.purchaseManager = new PurchaseManager(new ProductDatabasesWrapper());
		this.bas = new BagAdditionSubcontroller(this.baggingScale, this.purchaseManager);
	}

	@Test(expected = InvalidWeightException.class)
	public void negativeWeightTest() throws InvalidWeightException {
		try {
			this.bas.handleBagAddition(-1.0);
		} catch (WeightOverloadException e) {
			fail("InvalidWeightException was expected, not other Exceptions");
		}
	}

	@Test(expected = InvalidWeightException.class)
	public void exceedingMaxWeightTest() throws InvalidWeightException {
		try {
			this.bas.handleBagAddition(3.0);
		} catch (WeightOverloadException e) {
			fail("InvalidWeightException was expected, not other Exceptions");
		}
	}

	private static class WeightOnlyItem extends Item {
		public WeightOnlyItem(double weight) {
			super(weight);
		}
	}

	@Test(expected = WeightOverloadException.class)
	public void overloadedScaleExceptionTest() throws WeightOverloadException {
		Item heavyItem = new WeightOnlyItem(9.99);
		this.baggingScale.add(heavyItem);
		try {
			this.bas.handleBagAddition(1.0);
		} catch (InvalidWeightException e) {
			fail("WeightOverloadException was expected, not other Exceptions");
		}
	}

	@Test
	public void overloadedScaleItemRemovedTest() {
		Item heavyItem = new WeightOnlyItem(9.99);
		this.baggingScale.add(heavyItem);
		try {
			this.bas.handleBagAddition(1.0);
		} catch (InvalidWeightException e) {
			fail("WeightOverloadException was expected, not other Exceptions");
		} catch (WeightOverloadException e) {
			// this is expected
		}

		try {
			double currentScaleWeight = this.baggingScale.getCurrentWeight();
			assertEquals(currentScaleWeight, 9.99, 0.001);
		} catch (OverloadException e) {
			fail("bag should have been removed from bagging area, leaving *not* overloaded");
		}
	}

	@Test
	public void singleAcceptableBagTest() {
		try {
			this.bas.handleBagAddition(1.0);
		} catch (WeightOverloadException | InvalidWeightException e) {
			fail("No Exception was expected");
		}
		ArrayList<Item> items = this.purchaseManager.getItems();
		assertEquals(items.size(), 1);
		assertTrue(items.get(0) instanceof BagItem);
		assertEquals(items.get(0).getWeight(), 1.0, 0.001);
		assertEquals(this.bas.getCurrentBagWeight(), 1.0, 0.001);
		assertEquals(this.purchaseManager.getTotalBagWeight(), 1.0, 0.001);
		assertEquals(this.purchaseManager.getTotalWeight(), 1.0, 0.001);
	}

	@Test
	public void multipleAcceptableBagTest() {
		try {
			this.bas.handleBagAddition(1.0);
			this.bas.handleBagAddition(0.5);
			this.bas.handleBagAddition(0.25);
		} catch (WeightOverloadException | InvalidWeightException e) {
			fail("No Exception was expected");
		}
		ArrayList<Item> items = this.purchaseManager.getItems();
		assertEquals(items.size(), 3);
		assertTrue(items.get(0) instanceof BagItem);
		assertEquals(this.bas.getCurrentBagWeight(), 1.75, 0.001);
		assertEquals(this.purchaseManager.getTotalBagWeight(), 1.75, 0.001);
		assertEquals(this.purchaseManager.getTotalWeight(), 1.75, 0.001);
	}

	@Test
	public void changeBagWeightLimit() {
		// initial max bag weight is 2.0
		assertEquals(this.bas.getMaxBagWeight(), 2.0, 0.001);
		this.bas.changeBagWeightLimit(3.0);
		assertEquals(this.bas.getMaxBagWeight(), 3.0, 0.001);
		this.bas.resetBagWeightLimit();
		assertEquals(this.bas.getMaxBagWeight(), 2.0, 0.001);
	}

	private static class AddItemErrorPurchaseManager extends PurchaseManager {
		public AddItemErrorPurchaseManager(ProductDatabasesWrapper pdw) {
			super(pdw);
		}

		@Override
		public void addItem(Item item) throws NoSuchItemException {
			throw new NoSuchItemException("No such item");
		}
	}

	@Test(expected = ControlSoftwareException.class)
	public void noSuchItemExceptionTest() {
		PurchaseManager addItemErrorPurchaseManager = new AddItemErrorPurchaseManager(new ProductDatabasesWrapper());
		this.bas = new BagAdditionSubcontroller(this.baggingScale, addItemErrorPurchaseManager);
		try {
			this.bas.handleBagAddition(1.0);
		} catch (WeightOverloadException | InvalidWeightException e) {
			fail("ControlSoftwareException was expected, not other exceptions");
		}
	}
}
