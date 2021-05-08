package selfcheckout.software.controllers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.external.CardIssuer;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import selfcheckout.software.controllers.exceptions.IncorrectAttendantLoginInformationException;
import selfcheckout.software.controllers.exceptions.NonexistentBarcodeException;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ControllerPurchaseTrackingTests {

	private ProductDatabasesWrapper dbWrapper;
	private SelfCheckoutController scc;

	@Before
	public void setup() {
		SelfCheckoutStation station = new BasicSelfCheckoutStation();
		ProductDatabasesWrapper.initializeDatabases();
		this.dbWrapper = new ProductDatabasesWrapper();
		CardIssuer cardIssuer = new CardIssuer(ControllerTestConstants.CARD_ISSUER_NAME);
		MembershipDatabaseWrapper members = new MembershipDatabaseWrapper();
		GiftCardDatabaseWrapper giftCardDatabaseWrapper = new GiftCardDatabaseWrapper();
		AttendantDatabaseWrapper attendantDatabaseWrapper = new AttendantDatabaseWrapper(new AttendantDatabase());
		attendantDatabaseWrapper.initializeAccountLoginInformationDatabase();
		this.scc = new SelfCheckoutController(
			station, this.dbWrapper, cardIssuer, members,
			giftCardDatabaseWrapper, attendantDatabaseWrapper);
		try {
			this.scc.getAttendantConsoleController().unblockStation(
				AttendantConsoleConstant.VALID_ATTENDANT_ID,
				AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
		} catch (IncorrectAttendantLoginInformationException e) {
			fail("Attendant credentials should be valid during setup");
		}
	}

	@After
	public void teardown() {
		ProductDatabasesWrapper.resetDatabases();
	}

	@Test
	public void testGetCurrentProductsEmpty() {
		assertEquals(this.scc.getCurrentProducts().size(), 0);
	}

	@Test
	public void testGetCurrentProductsSingle() {
		BasicSelfCheckoutStation.scanOneItemSuccessfully(
			this.scc, this.dbWrapper);
		assertEquals(this.scc.getCurrentProducts().size(), 1);
	}

	private static final int NUM_ITEMS = 5;

	@Test
	public void testGetCurrentProductsMultiple() {
		for (int i = 0; i < NUM_ITEMS; i++) {
			BasicSelfCheckoutStation.scanOneItemSuccessfully(this.scc, this.dbWrapper);
			BasicSelfCheckoutStation.bagOneItemSuccessfully(this.scc);
		}
		assertEquals(this.scc.getCurrentProducts().size(), NUM_ITEMS);
	}

	@Test
	public void testGetFullBarcodedProductList() {
		ProductDatabasesWrapper.resetDatabases();

		Barcode barcode = new Barcode("739252945192046282");
		String description = "product description";
		BigDecimal price = new BigDecimal("10.00");
		BarcodedProduct newProduct = new BarcodedProduct(barcode, description, price);
		try {
			this.dbWrapper.getCostByBarcode(barcode);
			fail("There should be no existing product with this barcode");
		} catch (NonexistentBarcodeException e) {
			// this is our expected case - we want to add a new product
		}
		this.dbWrapper.addDatabaseItem(newProduct);

		assertEquals(this.scc.getFullBarcodedProductList().size(), 1);
		assertEquals(this.scc.getFullBarcodedProductList().get(0), newProduct);
	}

	@Test
	public void testNoItemsGetTotalPrice() {
		assertEquals(BigDecimal.ZERO.compareTo(this.scc.getTotalPrice()), 0);
	}

	@Test
	public void testSingleItemGetTotalPrice() {
		BasicSelfCheckoutStation.scanOneItemSuccessfully(this.scc, this.dbWrapper);
		Barcode scannedItemBarcode = new Barcode(ControllerTestConstants.VALID_BARCODE_STRING);
		BigDecimal singleItemPrice;
		try {
			singleItemPrice = this.dbWrapper.getCostByBarcode(scannedItemBarcode);
		} catch (NonexistentBarcodeException e) {
			fail("Product should exist");
			return;
		}
		assertEquals(singleItemPrice.compareTo(this.scc.getTotalPrice()), 0);
	}

	@Test
	public void testMultiItemGetTotalPrice() {
		for (int i = 0; i < NUM_ITEMS; i++) {
			BasicSelfCheckoutStation.scanOneItemSuccessfully(this.scc, this.dbWrapper);
			BasicSelfCheckoutStation.bagOneItemSuccessfully(this.scc);
		}
		Barcode scannedItemBarcode = new Barcode(ControllerTestConstants.VALID_BARCODE_STRING);
		BigDecimal singleItemPrice;
		try {
			singleItemPrice = this.dbWrapper.getCostByBarcode(scannedItemBarcode);
		} catch (NonexistentBarcodeException e) {
			fail("Product should exist");
			return;
		}
		BigDecimal expectedTotalPrice = singleItemPrice.multiply(new BigDecimal(NUM_ITEMS));
		assertEquals(expectedTotalPrice.compareTo(this.scc.getTotalPrice()), 0);
	}
}
