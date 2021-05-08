package selfcheckout.software.controllers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.*;
import org.lsmr.selfcheckout.external.ProductDatabases;
import selfcheckout.software.controllers.exceptions.ControlSoftwareException;
import selfcheckout.software.controllers.exceptions.NoSuchItemException;
import selfcheckout.software.controllers.exceptions.NonexistentBarcodeException;
import selfcheckout.software.controllers.exceptions.NonexistentPLUCodeException;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class PurchaseManagerTests {

	private PurchaseManager purchaseManager;
	private ProductDatabasesWrapper dbWrapper;

	@Before
	public void setup() {
		ProductDatabasesWrapper.initializeDatabases();
		this.dbWrapper = new ProductDatabasesWrapper();
		this.purchaseManager = new PurchaseManager(this.dbWrapper);
	}

	@After
	public void teardown() {
		ProductDatabasesWrapper.resetDatabases();
	}

	@Test
	public void testGetProductsEmpty() {
		// initially no products
		assertEquals(this.purchaseManager.getProducts().size(), 0);
	}

	@Test
	public void testAddBarcodedItemSingle() {
		try {
			purchaseManager.addItem(ControllerTestConstants.VALID_BARCODED_ITEM);
		} catch (NoSuchItemException e) {
			fail("Barcode must be valid");
		}
		assertEquals(purchaseManager.getProducts().size(), 1);
	}

	@Test
	public void testAddPLUCodedItemSingle() {
		try {
			purchaseManager.addItem(ControllerTestConstants.VALID_PLUCODED_ITEM);
		} catch (NoSuchItemException e) {
			fail("Price lookup code must be valid");
		}
		assertEquals(purchaseManager.getProducts().size(), 1);
	}

	@Test
	public void testAddBagItemSingle() {
		try {
			purchaseManager.addItem(new BagItem(1.0));
		} catch (NoSuchItemException e) {
			fail("BagItem must be valid");
		}
		assertEquals(purchaseManager.getProducts().size(), 0);
	}

	private static final int NUM_ITEMS = 5;

	@Test
	public void testAddItemMultiple() {
		try {
			for (int i = 0; i < NUM_ITEMS; i++) {
				purchaseManager.addItem(ControllerTestConstants.VALID_BARCODED_ITEM);
				purchaseManager.addItem(ControllerTestConstants.VALID_PLUCODED_ITEM);
			}
		} catch (NoSuchItemException e) {
			fail("Barcode or PLU code must be valid");
		}
		assertEquals(purchaseManager.getProducts().size(), NUM_ITEMS * 2);
	}

	@Test(expected = NoSuchItemException.class)
	public void testOutOfInventoryBarcodedItem() throws NoSuchItemException {
		for (int i = 0; i < 101; i++) {
			purchaseManager.addItem(ControllerTestConstants.VALID_BARCODED_ITEM);
		}
	}

	@Test(expected = NoSuchItemException.class)
	public void testOutOfInventoryPLUCodedItem() throws NoSuchItemException {
		for (int i = 0; i < 101; i++) {
			purchaseManager.addItem(ControllerTestConstants.VALID_PLUCODED_ITEM);
		}
	}

	@Test
	public void testBagItemPrice() {
		BigDecimal price = this.purchaseManager.getItemPrice(new BagItem(0.01));
		assertEquals(BigDecimal.ZERO.compareTo(price), 0);
	}

	@Test
	public void testNoItemsGetTotalPrice() {
		assertEquals(BigDecimal.ZERO.compareTo(this.purchaseManager.getTotalPrice()), 0);
	}

	@Test
	public void testSingleBarcodedItemGetTotalPrice() {
		try {
			this.purchaseManager.addItem(ControllerTestConstants.VALID_BARCODED_ITEM);
		} catch (NoSuchItemException e) {
			fail("Product must exist");
		}
		Barcode scannedItemBarcode = new Barcode(ControllerTestConstants.VALID_BARCODE_STRING);
		BigDecimal singleItemPrice;
		try {
			singleItemPrice = this.dbWrapper.getCostByBarcode(scannedItemBarcode);
		} catch (NonexistentBarcodeException e) {
			fail("Product should exist");
			return;
		}
		assertEquals(singleItemPrice.compareTo(this.purchaseManager.getTotalPrice()), 0);
	}
	
	@Test
	public void testSinglePLUCodedItemGetTotalPrice() {
		try {
			this.purchaseManager.addItem(ControllerTestConstants.VALID_PLUCODED_ITEM);
		} catch (NoSuchItemException e) {
			fail("Product must exist");
		}
		PriceLookupCode enteredPLUCode = new PriceLookupCode(ControllerTestConstants.VALID_PLUCODE_STRING);
		BigDecimal singleItemPrice;
		try {
			singleItemPrice = this.dbWrapper.getCostByPLUCode(enteredPLUCode);
		} catch (NonexistentPLUCodeException e) {
			fail("Product should exist");
			return;
		}
		assertEquals(singleItemPrice.compareTo(this.purchaseManager.getTotalPrice()), 0);
	}

	// Tests if the total price of PLUCodedItem (some price/1kg) was calculated properly
	@Test
	public void testSinglePLUCodedItemGetTotalPriceDifferentWeight() {
		PriceLookupCode enteredPLUCode = new PriceLookupCode(ControllerTestConstants.VALID_PLUCODE_STRING);
		BigDecimal singleItemPrice;
		double itemWeight = 2.0; // 2kg
		try {
			PLUCodedItem item = new PLUCodedItem(enteredPLUCode, itemWeight);
			this.purchaseManager.addItem(item);
		} catch (NoSuchItemException e) {
			fail("Product must exist");
		}

		try {
			singleItemPrice = this.dbWrapper.getCostByPLUCode(enteredPLUCode).multiply(new BigDecimal(itemWeight));
		} catch (NonexistentPLUCodeException e) {
			fail("Product should exist");
			return;
		}
		assertEquals(singleItemPrice.compareTo(this.purchaseManager.getTotalPrice()), 0);
	}
	
	@Test
	public void testMultiItemGetTotalPrice() {
		for (int i = 0; i < NUM_ITEMS; i++) {
			try {
				this.purchaseManager.addItem(ControllerTestConstants.VALID_BARCODED_ITEM);
				this.purchaseManager.addItem(ControllerTestConstants.VALID_PLUCODED_ITEM);
			} catch (NoSuchItemException e) {
				fail("Product must exist");
			}
		}
		Barcode scannedItemBarcode = new Barcode(ControllerTestConstants.VALID_BARCODE_STRING);
		PriceLookupCode enteredPLUCode = new PriceLookupCode(ControllerTestConstants.VALID_PLUCODE_STRING);
		BigDecimal singleBarcodedItemPrice;
		BigDecimal singlePLUCodedItemPrice;
		try {
			singleBarcodedItemPrice = this.dbWrapper.getCostByBarcode(scannedItemBarcode);
			singlePLUCodedItemPrice =  this.dbWrapper.getCostByPLUCode(enteredPLUCode);
		} catch (NonexistentBarcodeException | NonexistentPLUCodeException e) {
			fail("Product should exist");
			return;
		}
		BigDecimal BarcodedItemTotalPrice = singleBarcodedItemPrice.multiply(new BigDecimal(NUM_ITEMS));
		BigDecimal PLUCodedItemTotalPrice = singlePLUCodedItemPrice.multiply(new BigDecimal(NUM_ITEMS));
		
		BigDecimal expectedTotalPrice = BarcodedItemTotalPrice.add(PLUCodedItemTotalPrice);
		assertEquals(expectedTotalPrice.compareTo(this.purchaseManager.getTotalPrice()), 0);
	}

	private static class FakeItemType extends Item {
		public FakeItemType(double weight) {
			super(weight);
		}
	}

	@Test(expected = ControlSoftwareException.class)
	public void testAddItemNotImplemented() {
		FakeItemType fakeItem = new FakeItemType(1.0);
		try {
			purchaseManager.addItem(fakeItem);
		} catch (NoSuchItemException e) {
			fail("Should not throw NonexistentBarcodeException for non-barcode product");
		}
	}

	@Test(expected = ControlSoftwareException.class)
	public void testGetTotalPriceNoSuchBarcodedItem() {
		try {
			this.purchaseManager.addItem(ControllerTestConstants.VALID_BARCODED_ITEM);
		} catch (NoSuchItemException e) {
			fail("Product must exist");
		}
		// remove item from database
		ProductDatabases.BARCODED_PRODUCT_DATABASE.remove(
			ControllerTestConstants.VALID_BARCODE);
		// now cannot calculate total price
		this.purchaseManager.getTotalPrice();
	}

	@Test(expected = ControlSoftwareException.class)
	public void testGetTotalPriceNoSuchPLUItem() {
		try {
			this.purchaseManager.addItem(ControllerTestConstants.VALID_PLUCODED_ITEM);
		} catch(NoSuchItemException e) {
			fail("Product must exist");
		}
		//remove the plu item form database
		ProductDatabases.PLU_PRODUCT_DATABASE.remove(ControllerTestConstants.VALID_PLUCODE);
		this.purchaseManager.getTotalPrice();
	}

	@Test(expected = ControlSoftwareException.class)
	public void testGetTotalPriceNonBarcodedItem() {
		FakeItemType fakeItem = new FakeItemType(1.0);
		this.purchaseManager.getItems().add(fakeItem);
		// remove item from database
		ProductDatabases.BARCODED_PRODUCT_DATABASE.remove(
			ControllerTestConstants.VALID_BARCODE);
		// now cannot calculate total price, should throw ControlSoftwareException
		this.purchaseManager.getTotalPrice();
	}

	@Test(expected = ControlSoftwareException.class)
	public void testGetBarcodedProductsNoSuchItem() {
		try {
			this.purchaseManager.addItem(ControllerTestConstants.VALID_BARCODED_ITEM);
		} catch (NoSuchItemException e) {
			fail("Product must exist");
		}
		// remove item from database
		ProductDatabases.BARCODED_PRODUCT_DATABASE.remove(
			ControllerTestConstants.VALID_BARCODE);
		// now cannot calculate total price
		this.purchaseManager.getProducts();
	}

	@Test(expected = ControlSoftwareException.class)
	public void testGetPLUProductsNoSuchItem() {
		try {
			this.purchaseManager.addItem(ControllerTestConstants.VALID_PLUCODED_ITEM);
		} catch (NoSuchItemException e) {
			fail("Product must exist");
		}
		// remove item from the database
		ProductDatabases.PLU_PRODUCT_DATABASE.remove(ControllerTestConstants.VALID_PLUCODE);
		// now cannot calculate total price
		this.purchaseManager.getProducts();
	}

	@Test(expected = ControlSoftwareException.class)
	public void testGetProductsNonBarcodedItem() {
		FakeItemType fakeItem = new FakeItemType(1.0);
		this.purchaseManager.getItems().add(fakeItem);
		// remove item from database
		ProductDatabases.BARCODED_PRODUCT_DATABASE.remove(
			ControllerTestConstants.VALID_BARCODE);
		// now cannot calculate total price, should throw ControlSoftwareException
		this.purchaseManager.getProducts();
	}

	@Test
	public void getTotalWeightEmpty() {
		assertEquals(this.purchaseManager.getTotalWeight(), 0.0, 0.001);
	}

	@Test
	public void testTotalWeightSingleBarcodedItem() {
		try {
			this.purchaseManager.addItem(ControllerTestConstants.VALID_BARCODED_ITEM);
		} catch (NoSuchItemException e) {
			fail("Product must exist");
		}
		assertEquals(this.purchaseManager.getTotalWeight(),
			ControllerTestConstants.VALID_BARCODED_ITEM.getWeight(), 0.001);
	}

	@Test
	public void testTotalWeightSinglePLUItem() {
		try {
			this.purchaseManager.addItem(ControllerTestConstants.VALID_PLUCODED_ITEM);
		} catch (NoSuchItemException e) {
			fail("Product must exist");
		}
		assertEquals(this.purchaseManager.getTotalWeight(),
			ControllerTestConstants.VALID_PLUCODED_ITEM.getWeight(), 0.001);
	}

	@Test
	public void testTotalWeightMultiItem() {
		for (int i = 0; i < NUM_ITEMS; i++) {
			try {
				this.purchaseManager.addItem(ControllerTestConstants.VALID_BARCODED_ITEM);
				this.purchaseManager.addItem(ControllerTestConstants.VALID_PLUCODED_ITEM);
			} catch (NoSuchItemException e) {
				fail("Product must exist");
			}
		}
		double totalExpectedWeight = ControllerTestConstants.VALID_BARCODED_ITEM.getWeight() * NUM_ITEMS +
				ControllerTestConstants.VALID_PLUCODED_ITEM.getWeight() * NUM_ITEMS ;
		assertEquals(this.purchaseManager.getTotalWeight(), totalExpectedWeight, 0.001);
	}

	@Test
	public void testGetCustomerOrderSummary() {
		try {
			this.purchaseManager.addItem(ControllerTestConstants.VALID_BARCODED_ITEM);
			this.purchaseManager.addItem(ControllerTestConstants.VALID_PLUCODED_ITEM);
			this.purchaseManager.addItem(new BagItem(1.0));
		} catch (NoSuchItemException e) {
			fail("Product must exist");
		}
		String orderSummary = this.purchaseManager.getCustomerOrderSummary();
		assertTrue(orderSummary.contains("Apple"));
		assertTrue(orderSummary.contains("Bread"));
		assertTrue(orderSummary.contains("Bag"));
	}
}
